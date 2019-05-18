//
//  main.cpp
//  BridgeNum
//
//  Created by Vsevolod Molchanov on 21.03.17.
//  Copyright © 2017 Vsevolod Molchanov. All rights reserved.
//

#include <iostream>
#include <vector>

using namespace std;

const int MAXM = 500000;
vector<int> matrix[MAXM];
bool mark[MAXM] = {false};
int in[MAXM];
int minIn[MAXM];
int countIn = 0;
int bridges = 0;

void isBridge(int v, int previous) {
    mark[v] = true;
    in[v] = minIn[v] = countIn++;
    for (int i = 0; i <= matrix[v].size() - 1; i++) {
        int next = matrix[v][i];
        if (next == previous){
            continue;
        }
        if (mark[next]) {
            minIn[v] = min(minIn[v], in[next]);
        } else {
            isBridge(next, v);
            minIn[v] = min(minIn[v], minIn[next]);
            if(minIn[next] > in[v]) {
                bridges++;
            }
        }
    }
}

int main(int argc, const char * argv[]) {
    
    int n, m;
    cin >> n >> m;
    
    for(int i = 0; i <= m - 1; i++) {
        int u, v;
        cin >> u >> v;
        matrix[v].push_back(u);
        matrix[u].push_back(v);
    }
    
    for(int i = 0; i <= n - 1; i++) {
        if(!mark[i]){
            isBridge(i, -1);
        }
    }
    
    cout << bridges << endl;
    
    return 0;
}
