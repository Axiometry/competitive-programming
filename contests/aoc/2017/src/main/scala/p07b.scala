import scala.annotation.tailrec
import scala.collection.IterableView
import scala.collection.immutable.Queue
import scala.util.matching.Regex

object p7b extends App {
  val NoChildren = Set.empty[String]
  implicit class RegexContext(sc: StringContext) {
    def r = new Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }
  object Iterable {
    def unapplySeq[T](s: Iterable[T]): Option[Seq[T]] = Some(s.toSeq)
  }
  
  val lines = io.Source.fromInputStream(System.in).getLines.toSeq
  val children = lines.map {
    case r"([a-z]+)$name [^ ]+ -> (.*)$ch" => (name, ch split ", " toSet)
    case r"([a-z]+)$name .*" => (name, NoChildren)
  }.toMap
  val weights = lines.map {
    case r"""([a-z]+)$name \(([0-9]+)$weight\).*""" => (name, weight.toInt)
  }.toMap
  
  @tailrec def toposort(weightTotals: Map[String, Int], q: Queue[String]): Option[Int] = q match {
    case dude +: qs =>
      val ch = children(dude).view
      val byWeight = ch.groupBy(weightTotals)
      
      if(byWeight.size <= 1) { // balanced
        val newW = weightTotals.updated(dude, ch.map(weightTotals).sum + weights(dude))
        val newQ = qs ++ children.collect {
          case (n2, ch2) if !newW.contains(n2) && ch2.contains(dude) && ch2.forall(newW.contains) => n2
        }
        
        toposort(newW, newQ)
      } else if(byWeight.size == 2) { // one weight is off
        val (badWeight, Iterable(badDude)) = byWeight.minBy(_._2.size) // bad weight with one dude
        val (goodWeight, _)                = byWeight.maxBy(_._2.size) // other good weights
        
        Some(weights(badDude) + (goodWeight - badWeight))
      } else throw new RuntimeException("bad input")
    case _ => None
  }
  
  val leaves = children.collect {
    case (dude, NoChildren) => dude
  }
  println(toposort(Map.empty, leaves.to).get)
}
