import java.io.*;
import java.util.*;

/*

3
3
1 2
1 3
2 3
3
1 2
1 2
1 2
1
1 1


*/
public class flippingcards {
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        Integer[] cache = new Integer[500000];
        for(int i = 0; i < 500000; i++)
            cache[i] = i;

        int T = Integer.parseInt(in.readLine());
        testLoop: for(int t = 1; t <= T; t++) {
            int N = Integer.parseInt(in.readLine());

            Set<Integer>[] graph = new Set[3*N];
            TreeSet<Integer> nodesByDegree = new TreeSet<Integer>((x, y) -> {
                int c = Integer.compare(graph[x].size(), graph[y].size());
                if(c == 0)
                    return x.compareTo(y);
                return c;
            });

            for(int i = 0; i < 3*N; i++)
                graph[i] = new HashSet<>();
            for(int i = 0; i < N; i++) {
                String[] parts = in.readLine().split(" ");
                for(String p : parts)
                    graph[i].add(cache[N + Integer.parseInt(p) - 1]);
                for(Integer x : graph[i])
                    graph[x].add(cache[i]);
            }
            for(int i = 0; i < N; i++) {
                nodesByDegree.add(cache[i]);
                nodesByDegree.addAll(graph[i]);
            }

            // Assign card-image pair if card/image has only one possible image/card (top sort ish)
            while(!nodesByDegree.isEmpty() && graph[nodesByDegree.first()].size() == 1) {
                Integer a = nodesByDegree.pollFirst();
                Integer b = graph[a].iterator().next();
                nodesByDegree.remove(b);
                graph[b].remove(a);
                for(Integer c : graph[b]) {
                    nodesByDegree.remove(c);
                    graph[c].remove(b);

                    if(!graph[c].isEmpty()) {
                        nodesByDegree.add(c);
                    } else if(c < N) {
                        // We found a card with no assignable images left
                        System.out.println("impossible");
                        continue testLoop;
                    }
                }
            }

            int cardCount = (int) nodesByDegree.stream().filter(i -> i < N).count();
            if(2*cardCount > nodesByDegree.size()) {
                System.out.println("impossible");
            } else {
                System.out.println("possible");
            }
        }
    }
}
