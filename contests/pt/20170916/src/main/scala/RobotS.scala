import scala.annotation.tailrec
import scala.collection.immutable.Queue

object RobotS extends App {
  case class P(r: Int, c: Int, d: Int) {
    def +(p: P) = P(r+p.r, c+p.c, d)
    def *(i: Int) = P(r*i, c*i, d)
    def left = P(r, c, (d+3)%4)
    def right = P(r, c, (d+1)%4)
  }
  class Lookup[@specialized(Int, Boolean) T](arr: Array[Array[Array[T]]]) {
    def apply(p: P): T = arr(p.d)(p.r)(p.c)
    def update(p: P, v: T) = arr(p.d)(p.r)(p.c) = v
  }
  
  val dirs = "NESW"
  val mod = 1000000
  val offs = Seq(P(-1, 0, 0), P(0, 1, 0), P(1, 0, 0), P(0, -1, 0))
  
  while(readLine.split(" ").map(_.toInt) match {
    case Array(0, 0) => false
    case Array(rows, cols) =>
      var s, e: P = null
      val isCrater = for(r <- 0 until rows; line = readLine) yield for(c <- 0 until cols) yield line(c) match {
        case 'N' | 'E' | 'S' | 'W' => s = P(r, c, dirs.indexOf(line(c))); false
        case 'X' => e = P(r, c, 0); false
        case '*' => true
        case '.' => false
      }
      
      def valid(p: P) = p.r >= 0 && p.c >= 0 && p.r < rows && p.c < cols && !isCrater(p.r)(p.c)
  
      val _best, _ways = Array.ofDim[Int](4, rows, cols)
      val _visited = Array.ofDim[Boolean](4, rows, cols)
      val best = new Lookup(_best)
      val ways = new Lookup(_ways)
      val visited = new Lookup(_visited)
      type Q = Queue[P]
      @tailrec def bfs(q: Q): Unit = q match {
        case p +: qs =>
          val b = best(p)
          val w = ways(p)
  
          def visit(nq: Q, p: P): Q =
            if(!visited(p)) {
              visited(p) = true
              best(p) = b+1
              ways(p) = w
              nq.enqueue(p)
            } else if(b+1 == best(p)) {
              ways(p) = (ways(p) + w) % mod
              nq
            } else nq
          
          val nq = (Iterator.from(1).map(p+offs(p.d)*_).takeWhile(valid) ++ Iterator(p.left, p.right)).foldLeft(qs)(visit)
          bfs(nq)
        case _ =>
      }
      ways(s) = 1
      bfs(Queue(s))
      val es = (0 to 3).map(d => e.copy(d = d))
      val b = es.map(best(_)).min
      val w = es.filter(best(_) == b).map(ways(_)).reduce((a, b) => (a+b)%mod)
      println(s"$b $w")
      true
  }) {}
}
