import scala.collection.breakOut
import scala.reflect.classTag

object treasure extends App {
  def readInts = readLine.split(' ').map(_.toInt)
  implicit def arr2pair(a: Array[Int]): (Int, Int) = (a(0), a(1))
  
  val Array(n, m) = readInts
  val (g, d) = Array.fill(n)(readInts).unzip
  
  val edges = for {
    i <- 1 to m
    val Array(a, b, t) = readInts
    edge <- Seq((a-1, b-1, t), (b-1, a-1, t))
  } yield edge
  val graph = edges.groupBy(_._1).mapValues(_.groupBy(_._2).mapValues(_.head._3))
  
  val dp = Array.ofDim[Int](1001, n)
  dp(0)(0) = 1+g(0)
  for(day <- 0 until 1000; (i, e) <- graph) dp(day)(i) match {
    case 0 =>
    case v =>
      for((j, t) <- e; if day+t <= 1000)
        dp(day+t)(j) = math.max(dp(day+t)(j), v+math.max(0, g(j)-d(j)*(day+t)))
  }
  println(math.max(0, dp.map(_.max).max-1))
}
