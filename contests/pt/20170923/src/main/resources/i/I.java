// author: Grzegorz Herman

import java.io.*;
import java.util.*;

public class I
{
    static class Fraction
    {
        public long num;
        public long den;

        public Fraction(long count, boolean black)
        {
            num = black ? count : 0;
            den = count;
        }
    };

	public static void main(String[] args) throws IOException 
	{
		BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter wr = new PrintWriter(System.out);
        int T = Integer.parseInt(rd.readLine());
        for (int t=0; t<T; t++)
        {
            int N = Integer.parseInt(rd.readLine());
            Fraction total = new Fraction(0, false);
            Fraction[] items = new Fraction[N];
            for (int i=0; i<N; i++)
            {
                String[] ss = rd.readLine().split(" ");
                items[i] = new Fraction(Integer.parseInt(ss[0]), (ss[1].equals("B")));
                total.num += items[i].num;
                total.den += items[i].den;
            }            
            if (total.num==0 || total.num==total.den)
            {
                wr.println(total.den);
                continue;
            }
            Fraction current = new Fraction(0, false);
            int answer = 0;
            for (int i=0; i<N; i++)
            {
                long d = current.num * total.den - current.den * total.num;
                long e = (items[i].num == items[i].den) ? (total.num - total.den) : total.num;
                long f = d / e;
                if (e * f == d && f > 0 && f <= items[i].den)
                {
                    //wr.printf("found: i=%d, d=%d, e=%d, f=%d\n", i, d, e, f);
                    answer++;
                }
                current.num += items[i].num;
                current.den += items[i].den;
            }
            wr.println(answer);
        }
        wr.close();
	}
}
