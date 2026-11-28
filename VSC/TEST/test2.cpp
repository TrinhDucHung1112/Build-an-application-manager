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

unordered_map<ll, ll> mp;
ll powMod(ll a, ll b){
    a %= MOD;
    if(b == 0) return 1;
    ll x = powMod(a,  b >> 1);
    x = x * x % MOD;
    if(b & 1) return x * a % MOD;
    return x;
}

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    freopen("sumdiv.in", "r", stdin);
    freopen("sumdiv.out", "w", stdout);
    ll a, b; cin >> a >> b;
    if(b == 0){
        cout << 1;
        return 0;
    }
    for(ll i = 2; i <= a / i; i++){
        if(a % i == 0){
            ll cnt = 0;
            while(a % i == 0){
                cnt++;
                a /= i;
            }
            mp[i] =  (mp[i] + cnt * (b % (MOD - 1))) % (MOD - 1);
        }
    }
    if(a > 1) mp[a] = b % (MOD - 1);
    ll tong = 1;
    for(auto it : mp){
            if(it.fi == MOD) continue;
            ll tu = (powMod(it.fi, it.se + 1) - 1 + MOD) % MOD;
            ll mau = powMod(it.fi - 1, MOD - 2);
            tong = tong * tu % MOD;
            tong = tong * mau % MOD;
    }
    cout << tong;
    
    return 0;
}