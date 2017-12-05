package common

import java.io._
import java.lang.reflect.InvocationTargetException
import java.security.Permission
import java.util.concurrent.Executors

import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.io.Source
import scala.xml.XML

object TestHarness {
  val DefaultConfig = ConfigFactory.parseString(
    """error-buffer-size = 5000000
      |program-timeout = 30 seconds
      |collect-output = true
    """.stripMargin)
  
  private case class ExitError(status: Int) extends Error
  private class NoExitSecurityManager extends SecurityManager {
    // allow anything.
    override def checkPermission(perm: Permission): Unit = ()
    
    override def checkPermission(perm: Permission, context: Any): Unit = ()
    
    override def checkExit(status: Int): Unit = {
      super.checkExit(status)
      throw ExitError(status)
    }
  }
  
  class ByteArrayRingBuffer(capacity: Int) extends OutputStream {
    var data = new Array[Byte](capacity)
    var pos = 0
    var filled = false
    
    def write(b: Int): Unit = synchronized {
      if(pos == capacity) {
        filled = true
        pos = 0
      }
      data(pos) = b.toByte
      pos += 1
    }
    
    def toByteArray: Array[Byte] = synchronized {
      if(filled) {
        val ret = new Array[Byte](capacity)
        System.arraycopy(data, pos, ret, 0, capacity - pos)
        System.arraycopy(data, 0, ret, capacity - pos, pos)
        ret
      } else java.util.Arrays.copyOf(data, pos)
    }
  }
  
  sealed trait Result
  object Result {
    case class Success(time: Long) extends Result
    case class WrongAnswer(line: Option[Int], expected: Option[Seq[String]], actual: Option[Seq[String]]) extends Result
    case class RuntimeError(ex: Throwable) extends Result
    case class Timeout(time: Long) extends Result
    case object Unknown extends Result
  }
  
  def main(args: Array[String]): Unit = {
    val stdOut = System.out
    val securityManager = System.getSecurityManager
  
    @volatile var outIntercept: PipedInputStream = null
    @volatile var errIntercept: ByteArrayRingBuffer = null
  
    def writeResult(typeName: String, dataXml: xml.NodeSeq) = {
      val errLines = Source.fromBytes(errIntercept.toByteArray).getLines()
    
      val xml =
        <result>
          <type>{typeName}</type>
          <std-err>{errLines.map(line => <line>{line}</line>)}</std-err>
          <data>{dataXml}</data>
        </result>
    
      val writer = new OutputStreamWriter(stdOut)
      XML.write(writer, xml, enc = "UTF-8", xmlDecl = true, doctype = null)
      writer.flush()
      
      System.setSecurityManager(securityManager)
      System.exit(0)
    }
    val handleResult: PartialFunction[Result, Unit] = {
      case Result.Success(time) => writeResult("success", <time>{time}</time>)
      case Result.WrongAnswer(line, expected, actual) => writeResult("wrong-answer", Seq(
        line.map(l => <line>{l}</line>),
        expected.map(s => <expected>{s.map(l => <line>{l}</line>)}</expected>),
        actual.map(s => <actual>{s.map(l => <line>{l}</line>)}</actual>)
      ).flatten)
      case Result.Timeout(time) => writeResult("timeout", <time>{time}</time>)
      case Result.RuntimeError(ex) =>
        def errorToXml(ex: Throwable): Seq[xml.Node] = {
          val details = Seq(
            <class>{ex.getClass.getName}</class>,
            <message>{ex.getMessage}</message>,
            <stack>{ex.getStackTrace.map(e => <line>{e.toString}</line>)}</stack>
          )
          val cause = Option(ex.getCause).map(e => <cause>{errorToXml(e)}</cause>).toList
          val suppressed = ex.getSuppressed.map(e => <suppressed>{errorToXml(e)}</suppressed>).toList
          
          details ++ cause ++ suppressed
        }
        
        writeResult("runtime-error", Seq(
          <error>{errorToXml(ex)}</error>
        ))
      case Result.Unknown => writeResult("unknown", Nil)
    }
    
    try {
      val Array(problem, inFile, outFile) = args
      
      @inline def loadConfig(s: String) = if(getClass.getResource(s"$s.conf") != null) Some(ConfigFactory.load(s)) else None
      val config = Seq(loadConfig(s"$problem/testing"), loadConfig("testing"), Some(DefaultConfig)).flatten.reduceLeft(_ withFallback _)
      val errorBufferSize = config.as[Int]("error-buffer-size")
      val programTimeout = config.as[Duration]("program-timeout")
      val collectOutput = config.as[Boolean]("collect-output")

      outIntercept = new PipedInputStream()
      errIntercept = new ByteArrayRingBuffer(errorBufferSize)
      
      val inDataIntercept = new FileInputStream(inFile)
      val outDataIntercept = new FileInputStream(outFile)

      System.setIn(inDataIntercept)
      System.setOut(new PrintStream(new PipedOutputStream(outIntercept)))
      System.setErr(new PrintStream(errIntercept))
      System.setSecurityManager(new NoExitSecurityManager())
      
      implicit val executionContext = ExecutionContext.fromExecutorService(Executors.newCachedThreadPool())
      
      val testFuture = Future[Result] {
        val mainClass = Class.forName(problem)
        val mainMethod = mainClass.getMethod("main", classOf[Array[String]])
        
        val startTime = System.currentTimeMillis
        try mainMethod.invoke(null, Array[String]()) catch {
          case ex: InvocationTargetException => ex.getTargetException match {
            case ExitError(0) =>
            case actualEx => throw actualEx
          }
        }
        
        Result.Success(System.currentTimeMillis - startTime)
      }
      val dataFuture = Future[Result] {
        val expectedLines = Source.fromInputStream(outDataIntercept).getLines()
        val actualLines = Source.fromInputStream(outIntercept).getLines()
        
        if(collectOutput) {
          val expectedLinesSeq = expectedLines.toIndexedSeq
          val actualLinesSeq = actualLines.toIndexedSeq
          
          val expectedSize = expectedLinesSeq.size
          val actualSize = actualLinesSeq.size
          
          if(expectedSize == actualSize)
            expectedLinesSeq.indices.find(line => expectedLinesSeq(line) != actualLinesSeq(line)) match {
              case None => Result.Success(0)
              case Some(line) => Result.WrongAnswer(Some(line), Some(expectedLinesSeq), Some(actualLinesSeq))
            }
          else Result.WrongAnswer(Some(math.min(expectedSize, actualSize)+1), Some(expectedLinesSeq), Some(actualLinesSeq))
        } else {
          val expectedLinesIter = expectedLines ++ Iterator.continually(null)
          val actualLinesIter = actualLines ++ Iterator.continually(null)
  
          expectedLinesIter.zip(actualLinesIter).zipWithIndex.collectFirst {
            case ((null, null), _) => Result.Success(0)
            case ((expected, actual), line) if expected != actual =>
              Result.WrongAnswer(Some(line), Some(Seq(expected)), Some(Seq(actual)))
          }.get
        }
      }
      val timeoutFuture = Future[Result] {
        Thread.sleep(programTimeout.toMillis)
        Result.Timeout(programTimeout.toMillis)
      }
      
      val resultFuture = Future.firstCompletedOf(Seq(testFuture, timeoutFuture)).flatMap {
        case success: Result.Success =>
          System.out.flush()
          System.out.close()
          Future.firstCompletedOf(Seq(dataFuture, timeoutFuture)).map {
            case _: Result.Success => success
            case _: Result.Timeout => Result.WrongAnswer(None, None, None)
            case result => result
          }
        case result => Future.successful(result)
      }.recover {
        case ex => Result.RuntimeError(ex)
      }
      
      
      resultFuture.onSuccess(handleResult)
    } catch {
      case e: Throwable => handleResult(Result.RuntimeError(e))
    }
  }
}
