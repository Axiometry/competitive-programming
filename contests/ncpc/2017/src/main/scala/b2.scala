object b2 extends App {
  case class Runner(name: String, a: Int, b: Int)
  @inline def sd2i(sd: String) = sd.replace(".", "").toInt
  @inline def i2sd(i: Int) = f"${i/100}%d.${i%100}%02d"
  
  // Read input
  val n = readInt
  val runners = List.fill(n)(readLine.split(" ")).map {
    case Array(name, a, b) => Runner(name, sd2i(a), sd2i(b))
  }
  
  // Sort runners by later lap scores
  val sorted = runners.sortBy(_.b)
  // Fn to calculate score for a choice of four runners
  def score(l: List[Runner]) = l.head.a + l.tail.map(_.b).sum
  // Find min score for each choice of initial runner with next best 3 later runners
  val ans = sorted.map(r0 => r0 :: sorted.filter(_ != r0).take(3)).minBy(score)
  
  println(i2sd(score(ans)))
  ans.foreach(r => println(r.name))
}
