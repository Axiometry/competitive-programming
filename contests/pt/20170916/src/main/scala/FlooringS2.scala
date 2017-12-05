object FlooringS2 extends App {
  def primes = {
    val size = 1000
    val prime = Array.fill(size + 1)(true)
    for(i <- 2 to size; j <- i + i to size by i)
      prime(j) = false
    (2 to size).filter(prime).map(BigInt(_))
  }
  
  // Problem: find smallest number of square tiles that form x distinct rectangles using all the tiles
  //        = find smallest integer n with either x*2 or x*2-1 divisors (x divisors <= sqrt(n))
  //        = find smallest integer n whose prime factor multiset has x*2 or x*2-1 submultisets
  //        = find smallest integer n whose prime factor multiset { pi^mi } has product{mi+1} equal to x*2 or x*2-1
  //        = for y in (x*2, x*2-1),
  //            for all sequences { mi>0 } where product{mi+1} = y,
  //              map to integer n with prime factors { pi^mi },
  //           minimize on n
  def solve(n: Int, p: Int): BigInt = if(n == 1) 1 else
    (for(i <- 2 to n; if n%i==0) yield primes(p).pow(i-1) * solve(n/i, p+1)).min
  def answer(n: Int): BigInt = solve(n*2, 0).min(solve(n*2-1, 0))
  
  Iterator.continually(readInt).takeWhile(_ != 0).map(answer).foreach(println)
}
