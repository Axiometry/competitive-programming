import scala.annotation.tailrec
import utils.readLines

object p09a extends App {
  import p09._
  
  def score(g: Group, i: Int): Int = i + g.children.map {
    case ch: Group => score(ch, i+1)
    case _ => 0
  }.sum
  
  readLines.map(parse).map(score(_, 0)).foreach(println)
}
object p09 {
  sealed trait Child
  case class Group(children: List[Child]) extends Child {
    override def toString: String = "{" + children.mkString(",") + "}"
  }
  case class Garbage(s: String) extends Child {
    override def toString: String = "<" + s + ">"
  }
  
  def parse(s: String): Group = {
    val (root, Nil) = parseGroup(s.toList)
    root
  }
  def parseGroup(ch: List[Char]): (Group, List[Char]) = ch match {
    case '}' :: chr => (Group(Nil), chr)
    case _ =>
      val (child, chr) = parseInner(ch)
      parseTailGroup(chr, child :: Nil)
  }
  @tailrec def parseTailGroup(ch: List[Char], children: List[Child]): (Group, List[Char]) = ch match {
    case ',' :: chr =>
      val (child, chrr) = parseInner(chr)
      parseTailGroup(chrr, child :: children)
    case '}' :: chr => (Group(children.reverse), chr)
    case Nil => (Group(children.reverse), Nil)
  }
  def parseInner(ch: List[Char]): (Child, List[Char]) = ch match {
    case '<' :: chs => parseGarbage(chs, Nil)
    case '{' :: chs => parseGroup(chs)
  }
  @tailrec def parseGarbage(ch: List[Char], acc: List[Char]): (Garbage, List[Char]) = ch match {
    case '!' :: _ :: chs => parseGarbage(chs, acc)
    case '>' :: chs => (Garbage(acc.mkString.reverse), chs)
    case c :: chs => parseGarbage(chs, c :: acc)
    case Nil => (Garbage(acc.mkString.reverse), Nil)
  }
}