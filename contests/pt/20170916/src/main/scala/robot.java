package contests.practice20170916;

import java.util.*;

/*
5 6
*....X
.....*
.....*
.....*
N....*
6 5
....X
.****
.****
.****
.****
N****
3 3
.E.
***
.X.
0 0

 */
public class robot {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        String dirs = "NESW";
        long MOD = 1000000;
        int[][] offs = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };

        while(true) {
            int R = in.nextInt(), C = in.nextInt();
            if(R == 0 && C == 0) break;
            in.nextLine();

            int sr = 0, sc = 0, sd = 0, er = 0, ec = 0;
            boolean[][] isCrater = new boolean[R][C];
            for(int r = 0; r < R; r++) {
                char[] line = in.nextLine().toCharArray();
                for(int c = 0; c < C; c++) {
                    switch(line[c]) {
                        case 'N':
                        case 'E':
                        case 'S':
                        case 'W':
                            sr = r; sc = c;
                            sd = dirs.indexOf(line[c]);
                            break;
                        case 'X': er = r; ec = c; break;
                        case '*': isCrater[r][c] = true;
                    }
                }
            }

            class State {
                int r, c, d;
                State(int r, int c, int d) { this.r = r; this.c = c; this.d = d; }
            }
            long[][][] ways = new long[4][R][C];
            boolean[][][] visited = new boolean[4][R][C];
            int[][][] best = new int[4][R][C];
            ArrayDeque<State> q = new ArrayDeque<>();
            visited[sd][sr][sc] = true;
            ways[sd][sr][sc] = 1;
            q.offer(new State(sr, sc, sd));

            while(!q.isEmpty()) {
                State s = q.poll();
                int b = best[s.d][s.r][s.c];
                long w = ways[s.d][s.r][s.c];

                for(int i = 1; i <= 3; i += 2) {
                    int nd = (s.d+i)%4;

                    if(!visited[nd][s.r][s.c]) {
                        visited[nd][s.r][s.c] = true;
                        best[nd][s.r][s.c] = b+1;
                        ways[nd][s.r][s.c] = w;
                        q.offer(new State(s.r, s.c, nd));
                    } else if(best[nd][s.r][s.c] == b+1) {
                        ways[nd][s.r][s.c] = (ways[nd][s.r][s.c] + w) % MOD;
                    }
                }

                for(int i = 1;; i++) {
                    int nr = s.r + i*offs[s.d][0], nc = s.c + i*offs[s.d][1];
                    if(nr < 0 || nc < 0 || nr >= R || nc >= C || isCrater[nr][nc]) break;

                    if(!visited[s.d][nr][nc]) {
                        visited[s.d][nr][nc] = true;
                        best[s.d][nr][nc] = b+1;
                        ways[s.d][nr][nc] = w;
                        q.offer(new State(nr, nc, s.d));
                    } else if(best[s.d][nr][nc] == b+1) {
                        ways[s.d][nr][nc] = (ways[s.d][nr][nc] + w) % MOD;
                    }
                }
            }
            int b = Integer.MAX_VALUE;
            long w = 0;
            for(int i = 0; i < 4; i++) {
                if(best[i][er][ec] <= b) {
                    if(best[i][er][ec] < b) {
                        b = best[i][er][ec];
                        w = 0;
                    }
                    w = (ways[i][er][ec] + w) % MOD;
                }
            }
            System.out.println(b + " " + w);
        }
    }
}
