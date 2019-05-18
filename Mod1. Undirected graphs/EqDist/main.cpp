//
//  main.cpp
//  EqDist
//
//  Created by Vsevolod Molchanov on 22.03.17.
//  Copyright © 2017 Vsevolod Molchanov. All rights reserved.
//

#include <iostream>
#include <vector>
#include <queue>

using namespace std;

const int MAXM = 500000;
vector<int> graph[MAXM];
vector<int> dist[MAXM];

void eqDist(int v, int m, int n) {
    
    bool *mark = new bool[n] {false};
    
    queue<int> q;
    q.push(v);
    while(!q.empty()) {
        int a = q.front();
        q.pop();
        for(int i = 0; i <= graph[a].size() - 1; i++) {
            if(!mark[graph[a][i]]) {
                mark[graph[a][i]] = true;
                q.push(graph[a][i]);
                dist[m][graph[a][i]] = dist[m][a] + 1;
            }
        }
    }
    delete []mark;
}

int main(int argc, const char * argv[]) {
    
    int n, m;
    cin >> n >> m;
    for(int i = 0; i <= m - 1; i++) {
        int u, v;
        cin >> u >> v;
        graph[v].push_back(u);
        graph[u].push_back(v);
    }
    
    int k;
    cin >> k;
    for(int i = 0; i<= k - 1; i++) {
        int v;
        cin >> v;
        for(int j = 0; j <= n - 1; j++) {
            dist[i].push_back(-1);
        }
        dist[i][v] = 0;
        eqDist(v, i, n);
        dist[i][v] = 0;
    }
    
    bool isVertices = false;
    for(int i = 0; i <= n - 1; i++) {
        bool vert = true;
        for(int j = 0; j <= k - 2; j++) {
            if(dist[j][i] == -1 || dist[j][i] != dist[j + 1][i]) {
                vert = false;
                break;
            }
        }
        if(vert) {
            isVertices = true;
            cout << i << " ";
        }
    }
    if(!isVertices) {
        cout << "-";
    }
    cout << endl;
    
    return 0;
}
