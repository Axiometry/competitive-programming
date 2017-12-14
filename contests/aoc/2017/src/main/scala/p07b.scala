import scala.annotation.tailrec
import scala.collection.immutable.Queue

import utils.Iterable

object p07b extends App {
  import p07._
  
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
