import utils.readLines

object p04b extends App {
  println(readLines.map(_.split("\\W+").map(_.sorted)).count(words => words.length == words.toSet.size))
}
