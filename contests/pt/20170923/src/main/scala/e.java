import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

/*                                                                                                                       
3                                                                                                                        
9                                                                                                                        
2 8 4 1 1 4 4 4 4                                                                                                        
 */
public class e {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        int t = scan.nextInt();

        while(t-- > 0) {
            String s = "r";

            int n = scan.nextInt();

            int[] arr = new int[n];
            for(int i = 0; i < n; i++)
                arr[i] = scan.nextInt();

            Deque<Integer> seq = new ArrayDeque<>();
            seq.add(arr[0]);

            for(int i = 1; i < n; i++) {

                int first = seq.peekFirst();
                int last = seq.peekLast();

                boolean reverse = first > last;
                if(reverse) {
                    int temp = first;
                    first = last;
                    last = temp;
                }

                /*System.out.println(i + " a[" + i + "]:" + arr[i]);
                System.out.println(seq);
                System.out.println(s);
                System.out.println(first);*/

                if(arr[i] == first) {
                    if(!reverse) {
                        seq.addFirst(arr[i]);
                        s = s + "l";
                    } else {
                        seq.addLast(arr[i]);
                        s = s + "r";
                    }
                } else if(arr[i] == last) {
                    if(!reverse) {
                        seq.addLast(arr[i]);
                        s = s + "r";
                    } else {
                        seq.addFirst(arr[i]);
                        s = s + "l";
                    }
                } else {
                    if(arr[i] <= first == !reverse) {
                        seq.addFirst(arr[i]);
                        s = s + "l";
                    } else {
                        seq.addLast(arr[i]);
                        s = s + "r";
                    }
                }
                //System.out.println(seq);
                consolidate(seq);
                //System.out.println();
            }

            if(seq.size() == 1) {
                System.out.println(s);
            } else {
                System.out.println("no");
            }
        }

    }

    static void consolidate(Deque<Integer> seq) {
        if(seq.size() < 2) return;

        if(seq.size() == 2) {
            int front = seq.pollFirst();
            int last = seq.pollFirst();
            if(front == last) {
                seq.add(front + last);
            } else {
                seq.addFirst(last);
                seq.addFirst(front);
            }
        } else if(seq.size() == 3) {
            int front = seq.pollFirst();
            int mid = seq.pollFirst();
            int back = seq.pollLast();

            if(front == mid) {
                seq.add(front + mid);
                seq.add(back);
            } else if(mid == back) {
                seq.add(front);
                seq.add(mid + back);
            } else {
                seq.add(front);
                seq.add(mid);
                seq.add(back);
            }
        } else {
            int frontA = seq.pollFirst();
            int frontB = seq.pollFirst();

            int backA = seq.pollLast();
            int backB = seq.pollLast();
            boolean consolidated = false;
            if(frontA == frontB) {
                consolidated = true;
                seq.addFirst(frontA + frontB);

                if(backA != backB) {
                    seq.addLast(backB);
                    seq.addLast(backA);
                }
            }

            if(backA == backB) {
                consolidated = true;
                seq.addFirst(backA + backB);
            }

            if(consolidated) {
                consolidate(seq);
            } else {
                seq.addFirst(frontB);
                seq.addFirst(frontA);

                seq.addLast(backB);
                seq.addLast(backA);
            }
        }
    }

}                                                                                                                        