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

const int Maxn = 1e6 + 5;
int n;
unordered_map<int, int> mp;

ll powMod(ll a, ll b){
    a %= MOD;
    if(b == 0) return 1;
    ll x = powMod(a,  b >> 1);
    x = x * x % MOD;
    if(b & 1) return x * a % MOD;
    return x;
}

void init(){
    cin >> n;
    ll soluong = 1;
    ll tong = 1;
    ll sum = 1;
    for(int i = 1; i <= n; i++){
        int x, y; cin >> x >> y;
        mp[x] += y;
        soluong = soluong * (y + 1) % MOD;
    }
    for(auto it : mp){
        ll tu = (powMod(it.fi, it.se + 1) - 1 + MOD) % MOD;
        ll mau = powMod(it.fi - 1, MOD - 2);
        tong = tong * tu % MOD;
        tong = tong * mau % MOD;
    }
    ll cnt = 1;
    ll tich = 1;
    for(auto it : mp){
        cnt = cnt * (it.se + 1) % (2 * (MOD - 1));
    }
    for(auto it : mp){
        ll mu;
        if(cnt % 2 == 0){
            mu = (cnt / 2) * it.se % (MOD - 1);
        }
        else{
            mu = cnt * (it.se / 2) % (MOD - 1);
        }
        tich = tich * powMod(it.fi, mu) % MOD;
    }
    cout << soluong << " " << tong << " " << tich;
}

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    init();

    return 0;
}