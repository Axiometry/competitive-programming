import utils.readLines

object p09b extends App {
  import p09._
  
  def charSum(g: Child): Int = g match {
    case Group(children) => children.view.map(charSum).sum
    case Garbage(s) => s.length
  }
  
  readLines.map(parse).map(charSum).foreach(println)
}
