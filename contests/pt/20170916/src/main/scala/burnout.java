package contests.practice20170916;

import java.util.*;

public class burnout {
    static class Time {
        final long total, _on, _off;
        final boolean toggle;
        Time(long _on, long _off, boolean toggle) {
            this._on = _on; this._off = _off; this.toggle = toggle;
            total = _on + _off;
        }
        long on(boolean startOn) { if(startOn) return _on; else return _off; }
        long off(boolean startOn) { if(startOn) return _off; else return _on; }
        Time repeat(long rep, boolean startOn) {
            if(!toggle)
                return new Time(on(startOn)*rep, off(startOn)*rep, false);
            else if(rep%2==0)
                return new Time(total*(rep/2), total*(rep/2), false);
            else
                return new Time(total*(rep/2)+on(startOn), total*(rep/2)+off(startOn), toggle);
        }
        Time repeatUntil(long target, boolean startOn) {
            long div, mul;
            if(toggle) {
                div = total;
                mul = 2;
            } else {
                div = on(startOn);
                mul = 1;
            }
            long times = (target-1)/div;
            if(target > (times*div)+on(startOn))
                return repeat(times*mul+1, startOn);
            else
                return repeat(times*mul, startOn);
        }
    }
    static abstract class Elem {
        final Time time;
        Elem(Time time) { this.time = time; }

        abstract long sim(long target, boolean startOn);
    }
    static class Value extends Elem {
        Value(long v) { super(new Time(v, 0, true)); }
        long sim(long target, boolean startOn) { return target; }
    }
    static class Group extends Elem {
        final List<Elem> elems;
        final long repeats;
        final Time innerTime;
        Group(List<Elem> elems, long repeats, Time innerTime) {
            super(innerTime.repeat(repeats, true));
            this.elems = elems;
            this.repeats = repeats;
            this.innerTime = innerTime;
        }
        static Group create(List<Elem> elems, long repeats) {
            long tOn = 0, tOff = 0;
            boolean on = true;
            for(Elem e : elems) {
                tOn += e.time.on(on);
                tOff += e.time.off(on);
                if(e.time.toggle) on ^= true;
            }
            return new Group(elems, repeats, new Time(tOn, tOff, !on));
        }
        long sim(long target, boolean startOn) {
            Time start = innerTime.repeatUntil(target, startOn);
            long total = start.total;
            long rem = target-start._on;
            boolean on = start.toggle ? !startOn : startOn;

            for(Elem e : elems) {
                if(rem <= e.time.on(on)) {
                    return total + e.sim(rem, on);
                } else {
                    total += e.time.total;
                    rem -= e.time.on(on);
                    if(e.time.toggle) on ^= true;
                }
            }
            throw new RuntimeException();
        }
    }
    static class Parser {
        char[] line;
        int i;
        Parser(char[] line) {
            this.line = line;
        }
        Group parseGroup() {
            List<Elem> elems = new ArrayList<>();
            long repeats = 1;
            while(i < line.length) {
                if(line[i] >= '0' && line[i] <= '9') {
                    elems.add(new Value(parseNum()));
                } else if(line[i] == '(') {
                    i++;
                    elems.add(parseGroup());
                } else if(line[i] == ')') {
                    i += 2;
                    repeats = parseNum();
                    break;
                } else i++;
            }
            return Group.create(elems, repeats);
        }
        long parseNum() {
            long n = 0;
            while(i < line.length) {
                if(line[i] >= '0' && line[i] <= '9') {
                    n = n*10+line[i++]-'0';
                } else break;
            }
            return n;
        }
    }
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        while(true) {
            long n = in.nextLong();
            if(n == 0) break;

            in.nextLine();
            Group root = new Parser(in.nextLine().toCharArray()).parseGroup();
            System.out.println(root.sim(n, true));
        }
    }
}
