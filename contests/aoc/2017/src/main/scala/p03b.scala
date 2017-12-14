import utils.memoize

object p03b extends App {
  import p03._
  
  val answer: Int => Int = memoize {
    case 1 => 1
    case n =>
      val c@(x, y) = numToCoord(n)
      val others = Seq(
        (x-1, y-1),
        (x-1, y),
        (x-1, y+1),
        (x, y-1),
        (x, y+1),
        (x+1, y-1),
        (x+1, y),
        (x+1, y+1)
      )
      others.map(coordToNum).filter(_ < n).map(answer).sum
  }
  val N = readInt
  println(Iterator.from(1).map(answer).find(_ > N).get)
}
