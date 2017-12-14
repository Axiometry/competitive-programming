import utils.{readLines, RegexContext}

object p07a extends App {
  val NoChildren = Set.empty[String]
  
  val children = readLines.map {
    case r"([a-z]+)$name [^ ]+ -> (.*)$ch" => (name, ch split ", " toSet)
    case r"([a-z]+)$name .*" => (name, NoChildren)
  }.toMap
  
  val haveParent = children.values.reduce(_|_)
  println(children.keys.find(!haveParent.contains(_)).get)
}
