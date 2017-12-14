import utils.readLines

object p04a extends App {
  println(readLines.map(_.split("\\W+")).count(words => words.length == words.toSet.size))
}
