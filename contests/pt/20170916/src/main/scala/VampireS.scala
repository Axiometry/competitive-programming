object VampireS extends App {
  val table = collection.mutable.HashMap[Int, Int]()
  def hasVamp(n: Int): Boolean = table.contains(n) || {
    val ns = n.toString.sorted
    (2 to math.ceil(math.sqrt(n)).toInt).exists(a => n % a == 0 && ns == s"$a${n/a}".sorted)
  }
  
  Iterator.continually(readInt).takeWhile(_ != 0).foreach { n =>
    val idx = Iterator.iterate(n)(_+1).find(hasVamp).get
    val res = table.getOrElseUpdate(idx, idx)
    for(i <- n until idx)
      table.put(i, res)
    println(res)
  }
}
