package contests.practice20170916;

import java.util.*;

/*
11 5
0 1
1 1
1 1
2 1
2 1
3 1
3 1
3 1
5 1
7 1
7 1
11 5
11 3
1 1
4 1
1 2
10 2
10 2
6 2
6 1
10 2
11 3
0 4
7 3
0 18
1 20
1 15
2 12
2 6
3 8
3 8
0 0

 */
public class family {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        while(true) {
            int n = in.nextInt(), k = in.nextInt();
            if(n == 0 && k == 0) break;

            int[] parent = new int[n+1];
            List<Integer>[] children = new List[n+1];
            for(int i = 0; i <= n; i++) children[i] = new ArrayList<>();
            int[] wealth = new int[n+1];

            for(int i = 1; i <= n; i++) {
                parent[i] = in.nextInt();
                wealth[i] = in.nextInt();
                children[parent[i]].add(i);
            }

            int[] order = new int[n+1];
            int[] take = new int[n+1];
            int[] next = new int[n+1];
            int node = 0, idx = 0;
            while(true) {
                if(next[node] == 0) order[idx++] = node;
                if(next[node] >= children[node].size()) {
                    take[node] = idx;
                    if(node == 0) break;
                    node = parent[node];
                    continue;
                }
                node = children[node].get(next[node]++);
            }

            int[] prev = new int[n+2];
            for(int j = 1; j <= k; j++) {
                int[] dp = new int[n+2];
                for(int i = n; i >= 1; i--) {
                    int nextTake = take[order[i]];

                    if(j == 1 || prev[nextTake] != 0)
                        dp[i] = Math.max(wealth[order[i]] + prev[nextTake], dp[i+1]);
                    else
                        dp[i] = dp[i+1];
                }
                prev = dp;
            }
            System.out.println(prev[1]);
        }
    }
    /*
    dp(i, 0) = dp(i+1, 0)
    dp(i, j) = max(wealth(i)+dp(take(i), j-1), dp(skip(i), j))
     */
}
