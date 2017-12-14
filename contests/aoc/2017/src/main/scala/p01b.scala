object p1b extends App {
  val s = readLine
  val result = s.zip(s.drop(s.size/2)+s).map {
    case (a, b) if a == b => a-'0'
    case _ => 0
  }.sum
  println(result)
}
