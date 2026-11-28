#include <bits/stdc++.h>
using namespace std;

#define se second
#define pb push_back
#define fi first
#define el endl
#define ppi pair<pair<int, int>, int>

using ll = long long;
using db = double;
const int MOD = 1e9 + 7;
const ll INF = 1e18;

typedef pair<int, int> ii;
int n, m, q;
vector<ii> adj[200005];
int visited[200005];

void nhap(){
    for(int i = 0; i < m; i++){
        int x, y, w;
        cin >> x >> y >> w;
        adj[x].pb({y, w});
        adj[y].pb({x, w});
    }
}

void Dijkstra(int s, int t){
    vector<int> d(n + 1, 1e9);
    d[s] = 0;
    priority_queue<ii, vector<ii>, greater<>> pq;
    pq.push({0, s});
    while(!pq.empty()){
        ii tmp = pq.top(); pq.pop();
        int dis = tmp.fi, u = tmp.se;
        if(dis > d[u]) continue;
        for(ii e : adj[u]){
            int v = e.fi, w = e.se;
            if(d[v] > d[u] + w){
                d[v] = d[u] + w;
                pq.push({d[v], v});
            }
        }
    }
    cout << d[t] << el;
}

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n >> m;
    nhap();
    cin >> q;
    while(q--){
        int s, t; cin >> s >> t;
        Dijkstra(s, t);
    }
    return 0;
}
