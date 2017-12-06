import java.util.*;

/*
3
42 1 1 1
90 1 1 2
110 1 1 3


4
0 0 0 0
120 120 120 120
240 240 240 240
0 120 240 2017

 */
public class c {
	static class Card {
		int i, id;
		
		int[] angle;
		Card[] left, right;
	}
	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		
		int n = in.nextInt();
		Card[] cards = new Card[n];
		// Cards sorted by their angle for all three angles
		Card[][] cx = new Card[3][n];
		// Read input
		for(int i = 0; i < n; i++) {
			Card c = new Card();
			c.angle = new int[] {
				in.nextInt(),
				in.nextInt(),
				in.nextInt()
			};
			c.left = new Card[3];
			c.right = new Card[3];
			c.id = in.nextInt();
			c.i = i;
			cards[i] = cx[0][i] = cx[1][i] = cx[2][i] = c;
		}
        // Sort cx[x] for each angle x
		for(int _x = 0; _x < 3; _x++) {
			final int x = _x;
			Arrays.sort(cx[x], (i, j) -> {
				int c = Integer.compare(i.angle[x], j.angle[x]);
				if(c == 0)
					return Integer.compare(i.id, j.id);
				return c;
			});
		}
		// Build a circular doubly-linked linked list for each angle x
        for(int x = 0; x < 3; x++) {
		    cx[x][0].left[x] = cx[x][n-1];
		    cx[x][n-1].right[x] = cx[x][0];
            for(int i = 1; i < n; i++)
                cx[x][i].left[x] = cx[x][i-1];
            for(int i = 0; i < n-1; i++)
                cx[x][i].right[x] = cx[x][i+1];
        }
        // Sort all cards by their total scores
		TreeSet<Card> sorted = new TreeSet<>((i, j) -> {
			int ci = totalScore(i);
			int cb = totalScore(j);
			int c = Integer.compare(ci, cb);
			if(c == 0)
				return -Integer.compare(i.id, j.id);
			return c;
		});
		sorted.addAll(Arrays.asList(cards));
		// Remove cards in sorted order, updating linked lists as we go
		while(!sorted.isEmpty()) {
			Card best = sorted.pollFirst();
			sorted.remove(best);

            System.out.println(best.id);

			// Find neighbors to update
			Set<Card> update = new HashSet<>();
			for(int x = 0; x < 3; x++) {
				update.add(best.left[x]);
				update.add(best.right[x]);
			}
			update.remove(best);

			// Remove neighbors from sorting before affecting their total scores
			sorted.removeAll(update);
			// Remove best from the neighbors' left/right
			for(Card c : update) {
				for(int x = 0; x < 3; x++) {
					if(c.left[x] == best) {
						c.left[x] = best.left[x];
						best.left[x].right[x] = c;
					}
					if(c.right[x] == best) {
						c.right[x] = best.right[x];
						best.right[x].left[x] = c;
					}
				}
			}
			// Put updated neighbors back
			sorted.addAll(update);
		}
	}
	// Calculate total angle score as per problem spec
	static int totalScore(Card c) {
		return angleScore(c, 0) + angleScore(c, 1) + angleScore(c, 2);
	}
	// Calculate angle score (sum of angles between neighbors, 0 if two or more on same angle)
	static int angleScore(Card c, int i) {
		Card l = c.left[i], r = c.right[i];
		if(l.angle[i] == c.angle[i]) return 0;
		else if(r.angle[i] == c.angle[i]) return 0;
		int al = l.angle[i] < c.angle[i] ? c.angle[i] - l.angle[i] : c.angle[i] + 360 - l.angle[i];
		int ar = r.angle[i] > c.angle[i] ? r.angle[i] - c.angle[i] : r.angle[i] + 360 - c.angle[i];
		return al+ar;
	}
}
