import java.util.Arrays;
import java.util.Scanner;

/*
6
ASHMEADE 9.90 8.85
BLAKE 9.69 8.72
BOLT 9.58 8.43
CARTER 9.78 8.93
FRATER 9.88 8.92
POWELL 9.72 8.61

 */
public class b {
    public static void main(String[] args) {
        class Runner implements Comparable<Runner> {
            String name;
            int a, b;

            public int compareTo(Runner o) {
                if(o.name.equals(name))
                    return 0;
                int c = Integer.compare(b, o.b);
                if(c == 0)
                    return name.compareTo(o.name);
                return c;
            }
        }
        Scanner in = new Scanner(System.in);

        int n = in.nextInt();
        Runner[] rs = new Runner[n];
        for(int i = 0; i < n; i++) {
            Runner r = new Runner();
            r.name = in.next();
            r.a = Integer.parseInt(in.next().replace(".", ""));
            r.b = Integer.parseInt(in.next().replace(".", ""));
            rs[i] = r;
        }
        Arrays.sort(rs);

        int best = Integer.MAX_VALUE;
        Runner[] bestRunners = null;
        for(int i = 0; i < n; i++) {
            int v = rs[i].a;
            Runner[] rx = new Runner[4];
            rx[0] = rs[i];
            for(int c = 0, j = 0; c < 3; c++, j++) {
                if(j == i) j++;
                v += rs[j].b;
                rx[c+1] = rs[j];
            }
            if(v < best) {
                best = v;
                bestRunners = rx;
            }
        }
        System.out.println(String.format("%d.%02d", best/100, best%100));
        for(Runner r : bestRunners)
            System.out.println(r.name);
    }
}
