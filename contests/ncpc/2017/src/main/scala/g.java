import java.util.Scanner;
import static java.lang.Math.min;

/*
3 4
2 7
3 5
1 6
1 9

 */
public class g {
    static class BIT {
        long[] freqSum;
        BIT(int n) {
            int len = 1;
            while(len < n)
                len <<= 1;
            freqSum = new long[++len];
        }
        void add(int index, long value) {
            index++;
            while(index < freqSum.length) {
                freqSum[index] += value;
                index += Integer.lowestOneBit(index);
            }
        }
        long sum(int index) {
            index++;
            long ans = 0;
            while(index > 0) {
                ans += freqSum[index];
                index -= Integer.lowestOneBit(index);
            }
            return ans;
        }
        long sum(int a, int b) {
            return sum(b) - sum(a-1);
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        int n = in.nextInt();
        int m = in.nextInt();

        // Table of the scores of every team
        int[] s = new int[n];
        int[] p = new int[n];

        // Tables storing the input data to replay later
        int[] it = new int[m];
        int[] ip = new int[m];
        // Number of problems our team solved
        int os = 0;
        for(int i = 0; i < m; i++) {
            it[i] = in.nextInt()-1;
            ip[i] = in.nextInt();

            if(it[i] == 0) os++;
        }
        // Table of our penalty points for each number of solves
        int[] op = new int[os+2];
        op[os+1] = Integer.MAX_VALUE;
        for(int i = 0, oi = 1; i < m; i++) {
            if(it[i] == 0) {
                op[oi] = op[oi-1] + ip[i];
                oi++;
            }
        }

        // Binary-indexed tree: a given index i stores a sum of
        //   (1) the number of teams with i-1 solves and lower penalty than us with i-1 solves
        //   (2) the number of teams with i solves and not lower penalty than us with i solves
        // Summing from 0 to r gives us number of people worse than us once we've solved r problems
        BIT bit = new BIT(os+2);
        // Initially everyone has 0 solves and is no better
        bit.add(0, n-1);

        // Simulate the contest
        for(int i = 0; i < m; i++) {
            int ts = s[it[i]], tp = p[it[i]];

            // Remove the team from the BIT before the new solve
            if(it[i] != 0) {
                // tp < op[min(ts, os+1)] : team has lower penalty for ts (or os+1) solves
                int before = tp < op[min(ts, os+1)] ? ts+1 : ts;
                bit.add(before, -1);
            }

            // Add the new solve
            s[it[i]] = ++ts;
            p[it[i]] = tp += ip[i];

            // Add the team back to the BIT based on the new solve
            if(it[i] != 0) {
                int after = tp < op[min(ts, os+1)] ? ts+1 : ts;
                bit.add(after, 1);
            }

            // Print our rank
            //   = number of people better than us + 1
            //   = (n-1) - number of people worse than us + 1
            //   = n - number of people worse than us
            System.out.println(n - bit.sum(0, s[0]));
        }
    }
}
