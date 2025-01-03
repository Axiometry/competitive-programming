object p06a extends App {
  val initial = readLine.split("\\W+").toIndexedSeq.map(_.toInt)
  val n = initial.size
  
  var state = Set(initial)
  var last = initial
  var num = 1
  while({
    val (v, i) = last.zipWithIndex.maxBy { case (v, i) => (v, -i) }
    val next = for(j <- last.indices) yield (v/n) + (if((j-i-1+n)%n < v%n) 1 else 0) + (if(i == j) 0 else last(j))
    last = next
    if(!state.contains(next)) {
      state += next
      num += 1
      true
    } else false
  }) ()
  println(num)
}
