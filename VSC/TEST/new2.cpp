#include <bits/stdc++.h>
using namespace std;

#define se second
#define pb push_back
#define fi first
#define el endl
#define ppi pair<pair<int, int>, int>
#define pip pair<int, pair<int, int>>
#define pii pair<int, int>

using ll = long long;
using db = double;
const int MOD = 1e9 + 7;
const ll INF = 1e18;

double cal(pii p1, pii p2){
    int x = (p1.fi - p2.fi) * (p1.fi - p2.fi);
    int y = (p1.se - p2.se) * (p1.se - p2.se);
    return sqrt(x + y);
}

bool check(double mid, const vector<ppi> &v, int n, double m){
    pair<int, int> bd = {0, 0};
    int cnt = 0;
    double V = mid;
    double t = 0;
    for(int i = 0 ; i < v.size(); i++){
        double d = cal(bd, v[i].fi);
        if(d / V <= v[i].se - t){
            cnt++;
            t += d / V;
            V = V * m;
            bd = v[i].fi;
        }
    }
    return cnt == n;
}

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n; cin >> n;
    vector<ppi> v;
    for(int i = 0 ; i < n ; i++){
        int x, y, s; cin >> x >> y >> s;
        v.pb({{x, y}, s});
    }
    sort(v.begin(), v.end(), [](ppi p1, ppi p2){
        if(p1.se != p2.se) return p1.se < p2.se;
        return p1 < p2;
    });
    double m; cin >> m;
    double l = 0, r = 1e9, ans = 0;
    for(int i = 0; i < 100; i++){
        double mid = l + (r - l) / 2;
        if(check(mid, v, n, m)){
            ans = mid;
            r = mid;
        }
        else{
            l = mid;
        }
    }
    cout << fixed << setprecision(10) << ans << el;

    return 0;
}