import scala.annotation.tailrec

object FlooringS extends App {
  def primes = {
    val size = 1000
    val prime = Array.fill(size + 1)(true)
    for(i <- 2 to size; j <- i + i to size by i)
      prime(j) = false
    (2 to size).filter(prime)
  }
  
  def divisors(n: Int) = for(i <- 1 to n; if n%i==0) yield i
  def _factorCombinations(n: Int): Seq[List[Int]] = if(n == 1) Seq(List()) else divisors(n).filter(_ != 1).flatMap(d => _factorCombinations(n/d).map(d :: _))
  def factorCombinations(n: Int) = _factorCombinations(n).filter(_.nonEmpty)
  def candidates(n: Int) = factorCombinations(n).map(_.zip(primes).map { case (i, p) => BigInt(p).pow(i-1) }.product)
  def answer(n: Int): BigInt = if(n == 1) 1 else candidates(n*2).min.min(candidates(n*2-1).min)
  
  @tailrec def _factors(n: Int, f: Int, xs: List[Int]): List[Int] = if(n == 1) xs else if(n%f == 0) _factors(n/f, f, f :: xs) else _factors(n, f+1, xs)
  def factors(n: Int) = _factors(n, 2, Nil)
  def findRects(tiles: Int) = { val f = factors(tiles); (1 to f.size).flatMap(i => f.combinations(i).map(_.product).map(x => if(x <= tiles/x) (x, tiles/x) else (tiles/x, x))).toSet }
  def answerBrute(n: Int) = Iterator.from(2).indexWhere(i => findRects(i).size == n)+2
  
  Iterator.continually(readInt).takeWhile(_ != 0).map(answer).foreach(println)
}
