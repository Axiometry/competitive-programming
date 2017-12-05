package contests.practice20170916;

import java.util.*;

public class vampire {
    static char[] perm = new char[10];
    static boolean[] taken = new boolean[10];

    private static boolean isVamp1(int n) {
        return permute(n, Integer.toString(n).toCharArray(), 0);
    }
    private static boolean permute(int n, char[] digits, int pos) {
        if(pos == digits.length) {
            int x = Integer.parseInt(new String(perm, 0, pos));
            int ten = 1;
            for(int m = pos-2; m >= 0; m--) {
                ten *= 10;
                if(perm[m] == '0') continue;
                int a = x / ten;
                int b = x % ten;
                if(n == a*b) return true;
            }
            return false;
        }
        for(int i = 0; i < digits.length; i++) {
            if(!taken[i] && (pos != 0 || digits[i] != '0')) {
                perm[pos] = digits[i];
                taken[i] = true;
                boolean result = permute(n, digits, pos+1);
                taken[i] = false;
                if(result) return true;
            }
        }
        return false;
    }

    private static boolean isVamp2(int n) {
        char[] nd = new char[10];
        Arrays.fill(nd, digits(nd, 0, n), 10, '-');
        Arrays.sort(nd);
        char[] d = new char[10];
        int lim = (int) Math.ceil(Math.sqrt(n));
        for(int a = 2; a <= lim; a++) {
            if(n % a != 0) continue;
            int b = n / a;
            Arrays.fill(d, digits(d, digits(d, 0, a), b), 10, '-');
            Arrays.sort(d);
            if(Arrays.equals(nd, d)) return true;
        }
        return false;
    }
    private static int digits(char[] d, int s, int i) {
        int idx = s, n = i;
        while(n > 0) {
            d[idx++] = (char) ((n%10) + '0');
            n /= 10;
        }
        return idx;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        Integer[] cache = new Integer[1_002_000];
        for(int i = 0; i < cache.length; i++) cache[i] = i;
        Map<Integer, Integer> memo = new HashMap<>();
        while(true) {
            int n = in.nextInt();
            if(n == 0) break;

            for(int i = n;; i++) {
                if(memo.containsKey(cache[i]) || isVamp1(i)) {
                    Integer res = memo.putIfAbsent(cache[i], cache[i]);
                    if(res == null)
                        res = cache[i];
                    for(int j = n+1; j < i; j++)
                        memo.put(cache[j], res);
                    System.out.println(res);
                    break;
                }
            }
        }
    }
}
