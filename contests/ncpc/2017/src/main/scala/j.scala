object j extends App {
  val s@Array(l, r) = readLine.split(' ').map(_.toInt)
  (s.min, s.max) match {
    case (0, 0) => println("Not a moose")
    case (a, b) if a == b => println(s"Even ${a+b}")
    case (a, b) => println(s"Odd ${b*2}")
  }
}
