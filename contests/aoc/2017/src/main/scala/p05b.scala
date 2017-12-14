object p05b extends App {
  val ins = io.Source.fromInputStream(System.in).getLines.map(_.toInt).toArray
  
  var i = 0
  var n = 0
  while({
    val j = ins(i)
    if(j >= 3) ins(i) -= 1
    else       ins(i) += 1
    i += j
    n += 1
    i < ins.length
  }) ()
  println(n)
}
