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
vector<int> F(Maxn, 0);
unordered_map<int, int> mp;

void chuanbi(){
    for(int i = 2; i < Maxn; i++){
        if(F[i] == 0)
        for(int j = i; j < Maxn; j += i){
            if(F[j] == 0) F[j] = i;
        }
    }
}

void solve(ll x){
    while(x > 1){
        int p = F[x];
        int cnt = 0;
        while(x % p == 0){
            x /= p;
            cnt++;
        }
        mp[p] += cnt;
    }
}

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    chuanbi();
    ll a, b; cin >> a >> b;
    solve(a);
    solve(b);
    for(auto it : mp){
        cout << it.fi << " " << it.se << " ";
    }


    return 0;
}