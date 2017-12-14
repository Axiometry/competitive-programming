object p04a extends App {
  println(io.Source.fromInputStream(System.in).getLines.map(_.split("\\W+")).count(words => words.length == words.toSet.size))
}
