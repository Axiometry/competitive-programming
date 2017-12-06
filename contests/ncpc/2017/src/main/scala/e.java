import java.util.PriorityQueue;
import java.util.Scanner;

/*
3 3
-5 2 -5
-1 -2 -1
5 4 -5
2 2


2 3
-2 -3 -4
-3 -2 -3
2 1


 */
public class e {
    static int[] dx = {-1, 0, 1, 1, 1, 0, -1, -1}, dy = {-1, -1, -1, 0, 1, 1, 1, 0};


    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        int w = scan.nextInt();
        int h = scan.nextInt();

        int[][] depth = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                depth[i][j] = scan.nextInt();
            }
        }

        int si = scan.nextInt()-1;
        int sj = scan.nextInt()-1;

        // The lowest depth to which water can empty, as filled in by Dijkstra's
        int[][] v = new int[w][h];
        // Position in the grid, sortable based on its v
        class Pos implements Comparable<Pos> {
            int x, y;
            Pos(int x, int y) {
                this.x = x;
                this.y = y;
            }
            public int compareTo(Pos o) {
                int c = Integer.compare(v[x][y], v[o.x][o.y]);
                if(c == 0)
                    c = Integer.compare(x, o.x);
                if(c == 0)
                    c = Integer.compare(y, o.y);
                return c;
            }
        }

        // Caching Pos objects to bound number of creations to O(w*h)
        Pos[][] ps = new Pos[w][h];
        // Queue of Pos objects adjacent to explored Pos objects, sorted by lowest v
        PriorityQueue<Pos> q = new PriorityQueue<>();
        // Set up start position
        ps[si][sj] = new Pos(si, sj);
        v[si][sj] = depth[si][sj];
        q.offer(ps[si][sj]);
        // Run Dijkstra's
        while(!q.isEmpty()) {
            // Explore a Pos
            Pos p = q.poll();
            // Move in all 8 directions
            for(int d = 0; d < 8; d++) {
                int x = p.x + dx[d], y = p.y + dy[d];
                if(x < 0 || x >= w || y < 0 || y >= h) continue;
                // Skip land
                if(depth[x][y] >= 0) continue;

                int nv = Math.max(v[p.x][p.y], depth[x][y]);
                // If we haven't seen this Pos or we have a better v for it
                if(ps[x][y] == null || nv < v[x][y]) {
                    if(ps[x][y] != null) {
                        // We have seen it, so remove it from q before we
                        //   change v to avoid breaking the sorting contract
                        q.remove(ps[x][y]);
                    } else
                        ps[x][y] = new Pos(x, y);
                    // Update v and (re)insert Pos for later exploration
                    // Note that we will never add a Pos that we have explored:
                    //   because we explore in increasing v order, we will never see
                    //   an nv lower than an explored Pos' v (principle of Dijkstra's)
                    v[x][y] = nv;
                    q.offer(ps[x][y]);
                }
            }
        }

        // Sum the total depths (total number of 1x1x1 meter cubes of water drained)
        long sum = 0;
        for(int x = 0; x < w; x++) {
            for(int y = 0; y < h; y++) {
                sum += -v[x][y];
            }
        }
        System.out.println(sum);
    }

}
