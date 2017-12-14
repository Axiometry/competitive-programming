object p01a extends App {
  val s = readLine
  val result = s.zip(s.drop(1)+s(0)).map {
    case (a, b) if a == b => a-'0'
    case _ => 0
  }.sum
  println(result)
}
