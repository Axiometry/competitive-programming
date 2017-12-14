object p04b extends App {
  println(io.Source.fromInputStream(System.in).getLines.map(_.split("\\W+").map(_.sorted)).count(words => words.length == words.toSet.size))
}
