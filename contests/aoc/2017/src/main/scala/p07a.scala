import utils.{lines, RegexContext}

object p07a extends App {
  import p07._
  
  val haveParent = children.values.reduce(_|_)
  println(children.keys.find(!haveParent.contains(_)).get)
}
object p07 {
  val NoChildren = Set.empty[String]
  
  lazy val children = lines.map {
    case r"([a-z]+)$name [^ ]+ -> (.*)$ch" => (name, ch split ", " toSet)
    case r"([a-z]+)$name .*" => (name, NoChildren)
  }.toMap
  lazy val weights = lines.map {
    case r"""([a-z]+)$name \(([0-9]+)$weight\).*""" => (name, weight.toInt)
  }.toMap
}