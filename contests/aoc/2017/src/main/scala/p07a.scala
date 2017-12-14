import scala.util.matching.Regex

object p07a extends App {
  val NoChildren = Set.empty[String]
  implicit class RegexContext(sc: StringContext) {
    def r = new Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }
  
  val lines = io.Source.fromInputStream(System.in).getLines.toSeq
  val children = lines.map {
    case r"([a-z]+)$name [^ ]+ -> (.*)$ch" => (name, ch split ", " toSet)
    case r"([a-z]+)$name .*" => (name, NoChildren)
  }.toMap
  
  val haveParent = children.values.reduce(_|_)
  println(children.keys.find(!haveParent.contains(_)).get)
}
