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

const int Maxn = 1e5 + 5;
int n, m; 
vector<int> adj[Maxn];
vector<int> radj[Maxn];
stack<int> st;
vector<int> vis(Maxn, false);
vector<int> id(Maxn, 0);

void init(){
    cin >> n >> m;
    for(int i = 1 ; i <= m; i++){
        int x, y; cin >> x >> y;
        adj[x].pb(y);
        radj[y].pb(x);
    }
}

void dfs1(int u){
    vis[u] = 1;
    for(int v : adj[u]){
        if(!vis[v]) dfs1(v); 
    }
    st.push(u);
}

void dfs2(int u, int cnt){
    vis[u] = 1;
    id[u] = cnt;
    for(int v : radj[u]){
        if(!vis[v]) dfs2(v, cnt);
    }
}

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    init();
    for(int i = 1; i <= n ;i++){
        if(!vis[i]) dfs1(i);
    }
    fill(vis.begin(), vis.end(), false);
    int cnt = 0;
    while(!st.empty()){
        int x = st.top(); st.pop();
        if(vis[x]) continue;
        cnt++;
        dfs2(x, cnt);
    }
    vector<int> out(n + 1, 0);
    vector<int> in(n + 1, 0);
    for(int i = 1; i <= n ; i++){
        for(int j : adj[i]){
            if(id[j] != id[i]){
                out[i]++;
                in[j]++;
            }
        }
    }
    int S = 0, T = 0;
    for(int i = 1; i <= n ;i++){
        if(out[i] == 0) S++;
        if(in[i] == 0) T++;
    }
    if(cnt == 1) cout << 0;
    else cout << max(S, T);


    return 0;
}