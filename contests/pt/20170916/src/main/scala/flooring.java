package contests.practice20170916;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.*;

public class flooring {
    static BigInteger[] primes;
    static {
        int sz = 1000;
        boolean[] isPrime = new boolean[sz+1];
        for(int i = 0; i <= sz; i++) isPrime[i] = true;
        for(int i = 2; i <= sz; i++)
            for(int j = i*i; j <= sz; j += i)
                isPrime[j] = false;
        primes = IntStream.rangeClosed(2, sz).filter(i -> isPrime[i]).mapToObj(BigInteger::valueOf).toArray(BigInteger[]::new);
    }

    static BigInteger solve(int n, int p) {
        if(n == 1) return BigInteger.ONE;
        BigInteger best = null;
        for(int i = 2; i <= n; i++) {
            if(n%i != 0) continue;
            BigInteger factor = primes[p].pow(i-1);
            BigInteger ans = factor.multiply(solve(n/i, p+1));
            if(best == null || ans.compareTo(best) < 0)
                best = ans;
        }
        return best;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        while(true) {
            int n = in.nextInt();
            if(n == 0) break;

            System.out.println(solve(n*2, 0).min(solve(n*2-1, 0)));
        }
    }
}
/*
object Flooring extends App {
  def primes = {
    val size = 1000
    val prime = Array.fill(size + 1)(true)
    for(i <- 2 to size; j <- i + i to size by i)
      prime(j) = false
    (2 to size).filter(prime).map(BigInt(_))
  }

  def solve(n: Int, p: Int): BigInt = if(n == 1) 1 else
    (for(i <- 2 to n; if n%i==0) yield primes(p).pow(i-1) * solve(n/i, p+1)).min
  def answer(n: Int): BigInt = solve(n*2, 0).min(solve(n*2-1, 0))

  Iterator.continually(readInt).takeWhile(_ != 0).map(answer).foreach(println)
}
 */
