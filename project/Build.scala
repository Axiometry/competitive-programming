import java.io._

import sbt._
import Keys._
import complete.DefaultParsers._

import scala.util.Try
import scala.xml.XML
import scala.util.control.Exception._

@deprecated("Suppresses the deprecation warning on sbt.Build", "0")
object Build extends sbt.Build {
  
  implicit class FileUtils(val file: File) extends AnyVal {
    def segments: List[String] = file.getPath.split(File.separatorChar).toList.filterNot(_.length == 0)
  }
  
  val setup = taskKey[Unit]("Set up directory structure")
  val setupProblems = inputKey[Unit]("Create default problem template")
  val runTests = inputKey[Unit]("Test a problem on some input")
  
  val baseSettings = Seq(
    version := "1.0",
    scalaVersion := "2.11.1",
    libraryDependencies ++= Seq(
      "org.graphstream" % "gs-core" % "1.3",
      "org.graphstream" % "gs-ui" % "1.3"
    )
  )
  val commonSettings = baseSettings ++ Seq(
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
      "com.iheart" %% "ficus" % "1.4.3"
    )
  )
  val childSettings = baseSettings ++ Seq(
    setup := {
      val base = thisProject.value.base
      val srcDir = base / "src/main/scala"
      val resDir = base / "src/main/resources"
      srcDir.mkdirs()
      resDir.mkdirs()
      
      for(f <- srcDir.listFiles) {
        val fdata = resDir / f.base
        fdata.mkdirs()
      }
    },
    setupProblems := {
      val base = thisProject.value.base
      val srcDir = base / "src/main/scala"
      val resDir = base / "src/main/resources"
      val args = spaceDelimited("<arg>").parsed
      
      val problems: Seq[String] =
        if(args.isEmpty)
          srcDir.listFiles().toSeq.filter(f => f.ext == "scala" || f.ext == "java").map(_.base)
        else args
      
      for(problem <- problems) {
        val fScala = srcDir / s"$problem.scala"
        val fJava = srcDir / s"$problem.java"
        val fdata = resDir / problem
        if(!fScala.exists && !fJava.exists) {
          val writer = new PrintWriter(fScala)
          writer.println(s"object $problem extends App {")
          writer.println("  ")
          writer.println("}")
          writer.flush()
          writer.close()
        }
        fdata.mkdirs()
      }
    },
    runTests := {
      val log = streams.value.log
      val base = thisProject.value.base
      val args = spaceDelimited("<arg>").parsed
  
      val classpathOption = "-classpath" :: Path.makeString(Attributed.data((fullClasspath in Compile).value)) :: Nil
      val forkOptionTemplate = ForkOptions(
        bootJars = Nil,
        javaHome = javaHome.value,
        connectInput = false,
        outputStrategy = None,
        runJVMOptions = javaOptions.value,
        workingDirectory = None,
        envVars = envVars.value
      )
      
      def runProblemTests(problem: String, specifiedTests: Seq[String]): Boolean = {
        val workingDir @ Some(problemDataDir) = Some(base / "src/main/resources" / problem)
        
        def runTest(inputFile: String, outputFile: String): xml.Elem = {
          val pipedOutput = new PipedOutputStream()
          val pipedInput = new PipedInputStream(pipedOutput)
  
          val scalaOptions = classpathOption ::: "common.TestHarness" :: problem :: inputFile :: outputFile :: Nil
          val forkOptions = forkOptionTemplate.copy(
            workingDirectory = workingDir,
            outputStrategy = Some(CustomOutput(pipedOutput))
          )
          
          val process = Fork.java.fork(forkOptions, scalaOptions)
          ultimately(process.destroy()) {
            val data = Try(XML.load(pipedInput))
            val code = process.exitValue()
            if(code != 0) throw new RuntimeException(s"Process exited with code $code")
            data.get
          }
        }
  
        @inline def testName(file: File) = file.getParentFile.relativeTo(problemDataDir).map(_.segments).getOrElse(Nil).map(_ + "/").mkString + file.base
        val testFileFinder: String => Map[String, File] =
          if(specifiedTests.isEmpty) ext => (problemDataDir ** s"*.$ext").get.map(f => testName(f) -> f).toMap
          else ext => specifiedTests.map(t => t -> problemDataDir / s"$t.$ext").toMap
        val testInputFiles = testFileFinder("in")
        val testOutputFiles = testFileFinder("out")
        val tests = testInputFiles.keySet | testOutputFiles.keySet
  
        val badTests = {
          // For when test cases are specified
          val testsWithNonexistentFiles = (testInputFiles.toSeq ++ testOutputFiles.toSeq).filter(!_._2.isFile).map(_._1).toSet
          // For when test cases are discovered
          val testsWithUnmatchedFiles = (tests &~ testInputFiles.keySet) | (tests &~ testOutputFiles.keySet)
    
          testsWithNonexistentFiles | testsWithUnmatchedFiles
        }
        if(badTests.nonEmpty)
          throw new FileNotFoundException("Test cases are missing files: " + badTests.mkString(", "))
  
  
        log.info(s"Running $problem test cases...")
        for(test <- tests.toSeq.sorted) {
          log.info(s"  $test...")
    
          val inputFile = testInputFiles(test).absolutePath
          val outputFile = testOutputFiles(test).absolutePath
          
          try {
            val resultXml = runTest(inputFile, outputFile)
            val Seq(<type>{typeNode}</type>) = resultXml \ "type"
            val Seq(data) = resultXml \ "data"
            
            resultXml \ "std-err" \ "line" match {
              case Seq() =>
              case lines =>
                log.warn(s"    Standard error:")
                for(<line>{line}</line> <- lines)
                  log.warn(s"      $line")
            }
            
            typeNode.text match {
              case "success" =>
                val Seq(<time>{time}</time>) = data \ "time"
                
                log.info(s"  $test: Success ($time ms)")
              case "wrong-answer" =>
                val lineString = data \ "line" match {
                  case Seq(<line>{line}</line>) => s"line $line"
                  case _ => "unknown line"
                }
                data \ "expected" match {
                  case Seq(expected) =>
                    log.warn("    Expected:")
                    for(<line>{line}</line> <- expected.child)
                      log.warn(s"      $line")
                  case _ =>
                }
                data \ "actual" match {
                  case Seq(actual) =>
                    log.warn("    Actual:")
                    for(<line>{line}</line> <- actual.child)
                      log.warn(s"      $line")
                  case _ =>
                }
  
                log.error(s" $test: Failure: wrong answer ($lineString)")
                return false
              case "timeout" =>
                val Seq(<time>{time}</time>) = data \ "time"
                
                log.error(s" $test: Failure: timeout ($time ms)")
                return false
              case "runtime-error" =>
                def parseAndPrintError(indent: String, kind: String, elem: xml.Node): Unit = {
                  val Seq(<class>{errorClass}</class>) = elem \ "class"
                  val message = elem \ "message" match {
                    case Seq(<message>{m}</message>) => ": " + m.text
                    case _ => ""
                  }
                  val stack = elem \ "stack" match {
                    case Seq(<stack>{lines @ _*}</stack>) => lines
                    case _ => Nil
                  }
  
                  log.error(s"$indent$kind$errorClass$message")
                  for(<line>{line}</line> <- stack)
                    log.error(s"$indent| $line")
                  
                  for(causeElem <- elem \ "cause") {
                    log.error(s"$indent|")
                    parseAndPrintError(s"$indent| ", "Caused by ", causeElem)
                  }
                  for(suppressedElem <- elem \ "suppressed") {
                    log.error(s"$indent|")
                    parseAndPrintError(s"$indent| ", "Suppressed ", suppressedElem)
                  }
                }
                
                val Seq(errorElem) = data \ "error"
                
                parseAndPrintError("   ", "", errorElem)
                log.error(s" $test: Failure: runtime error")
                return false
              case _ =>
                log.error(s" $test: Failure: unknown")
                return false
            }
          } catch {
            case e: InterruptedException =>
              log.warn(s"  $test: Run canceled.")
              throw e
          }
        }
        true
      }
      
      args match {
        case problem +: tests if !(base / "src/main/scala").listFiles.exists(_.base == problem) =>
          throw new FileNotFoundException("Problem file is missing")
        case problem +: tests => runProblemTests(problem, tests)
        case _ => (base / "src/main/scala").listFiles.filter(_.isFile).forall(f => runProblemTests(f.base, Nil))
      }
    }
  )
  
  override def projectDefinitions(base: File) = {
    val common = Project(id = "common", base = base / "common").settings(commonSettings)
    val contests = for {
      groupDir <- (base / "contests").listFiles if groupDir.isDirectory
      contestDir <- groupDir.listFiles if contestDir.isDirectory
      contestId = s"${groupDir.name}-${contestDir.name}"
    } yield Project(id = contestId, base = contestDir).settings(childSettings).dependsOn(common)
    
    common +: contests
  }
}