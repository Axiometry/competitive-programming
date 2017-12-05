import java.io.*;

/*
3
3
1 B
3 W
2 B
4
3 W
3 B
9 W
1 B
2
2 W
3 W
 */
public class i {
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        int sets = Integer.parseInt(in.readLine());
        for(int set = 1; set <= sets; set++) {
            int n = Integer.parseInt(in.readLine());
            long[] count = new long[n];
            boolean[] isB = new boolean[n];
            long countB = 0, countW = 0;
            for(int i = 0; i < n; i++) {
                String line = in.readLine();
                int idx = line.indexOf(' ');
                long c = Long.parseLong(line.substring(0, idx));
                boolean b = line.charAt(idx+1) == 'B';
                if(b) countB += c; else countW += c;
                count[i] = c;
                isB[i] = b;
            }
            if(countB == 0) {
                System.out.println(countW);
                continue;
            }
            if(countW == 0) {
                System.out.println(countB);
                continue;
            }

            long g = gcd(countB, countW);
            countB /= g;
            countW /= g;

            long bs = 0, ws = 0, ps = 0;
            for(int i = 0; i < n; i++) {
                if(bs != 0 && bs%countB == 0 && ws != 0 && ws%countW == 0 && bs/countB == ws/countW) {
                    bs = 0;
                    ws = 0;
                    ps++;
                }
                if(bs != 0 && bs%countB == 0 && !isB[i] && countW*(bs/countB)-ws <= count[i] && countW*(bs/countB) >= ws) {
                    ws = count[i]-(countW*(bs/countB)-ws);
                    bs = 0;
                    ps++;
                } else if(ws != 0 && ws%countW == 0 && isB[i] && countB*(ws/countW)-bs <= count[i] && countB*(ws/countW) >= bs) {
                    bs = count[i]-(countB*(ws/countW)-bs);
                    ws = 0;
                    ps++;
                } else {
                    if(isB[i])
                        bs += count[i];
                    else
                        ws += count[i];
                }
            }
            System.out.println(ps);
        }
    }

    static long gcd(long a, long b) {
        if (a < b) return gcd(b, a);
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}

