object stars extends App {
  val n = readInt
  @inline def test(i: Int, j: Int) =
    if(n%(i+j) == 0 || n%(i+j) == i) println(s"$i $j")
  for(i <- 2 until n) {
    test(i, i-1)
    test(i, i)
  }
}
