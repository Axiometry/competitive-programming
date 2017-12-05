import scala.annotation.tailrec

object FamilyS extends App {
  while(readLine.split(" ").map(_.toInt) match {
    case Array(0, 0) => false
    case Array(n, k) =>
      val (parent, wealth) = (1 to n).map(_ => readLine.split(" ").map(_.toInt)).unzip(a => (a(0), a(1)))
      val children = parent.zip(1 to n).groupBy(_._1).mapValues(_.map(_._2).toList)
      
     /* val order, take = Array.ofDim[Int](n+1)
      def dfs(idx: Int, node: Int): Int = {
        order(idx) = node
        val last = children(node).foldLeft(idx+1)(dfs)
        take(node) = last
      }*/
      @tailrec def preOrder(visit: List[Int], visited: List[Int]): List[Int] = visit match {
        case node :: remaining =>
          preOrder(children.getOrElse(node, Nil) ++ remaining, node :: visited)
        case Nil => visited.reverse
      }
      @tailrec def postOrder(visit: List[Int], visited: List[Int]): List[Int] = visit match {
        case node :: remaining =>
          postOrder(children.getOrElse(node, Nil).reverse ++ remaining, node :: visited)
        case Nil => visited
      }
      
      val order = preOrder(List(0), Nil)
      println(order)
      val post = postOrder(List(0), List(n+1))
      val next = post.zip(post.drop(1)).toMap
      val take = order.foldLeft(Map.empty[Int, Int]) { (take, i) =>
        val nn = if(i != 0 && next(i) == parent(i-1)) take(next(i)) else next(i)
        take.updated(i, nn)
      }
      
      val result = (1 to k).foldLeft[Int => Int](i => 0) { (prev, j) =>
        val dp = (n to 1 by -1).scanLeft(0) { (v, i) =>
          val nv = prev(take(order(i)))
          if(j == 1 || nv != 0)
            math.max(wealth(order(i)-1) + nv, v)
          else v
        }
        i => if(i == 0 || i > n) 0 else dp(n-i)
      }
      println(result(1))
      
      /*@tailrec def findTake(visit: List[Int], take: Map[Int, Int]) = visit match {
        case node :: remaining =>
          findTake(children(visit))
      }*/
      true
  }) {}
}
