// Author: Adam Polak
#include <iostream>
#include <string>
#include <vector>
using namespace std;

long long gcd(long long a, long long b) { return b > 0 ? gcd(b, a % b) : a; }

int main() {
  ios_base::sync_with_stdio(false);
  int Z;
  cin >> Z;
  while (Z--) {
    int n;
    cin >> n;
    vector<int> num(n);
    vector<bool> col(n);
    for (int i = 0; i < n; ++i) {
      string temp;
      cin >> num[i] >> temp;
      col[i] = temp[0] == 'W';
    }
    long long black = 0, white = 0;
    for (int i = 0; i < n; ++i)
      if (col[i]) white += num[i]; else black += num[i];
    if (black == 0 || white == 0) {
      cout << black + white << endl;
      continue;
    }
    long long div = gcd(black, white);
    black /= div;
    white /= div;
    int result = 0;
    long long cur_black = 0, cur_white = 0;
    for (int i = 0; i < n; ++i) {
      if (col[i]) {
        if (cur_black > 0 && cur_black % black == 0) {
          long long target_white = white * (cur_black / black);
          if (cur_white < target_white && cur_white + num[i] >= target_white) {
            ++result;
            cur_white -= target_white;
            cur_black = 0;
          }
        }
        cur_white += num[i];
      } else {
        if (cur_white > 0 && cur_white % white == 0) {
          long long target_black = black * (cur_white / white);
          if (cur_black < target_black && cur_black + num[i] >= target_black) {
            ++result;
            cur_black -= target_black;
            cur_white = 0;
          }
        }
        cur_black += num[i];
      }
    }
    cout << result << endl;
  }
}
