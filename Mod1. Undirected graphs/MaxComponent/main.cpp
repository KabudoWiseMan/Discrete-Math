//
//  main.cpp
//  MaxComponent
//
//  Created by Vsevolod Molchanov on 22.03.17.
//  Copyright © 2017 Vsevolod Molchanov. All rights reserved.
//

#include <iostream>
#include <vector>
#include <set>

using namespace std;

const int MAXM = 1000000;
vector<int> graph[MAXM];
bool mark[MAXM] = {false};
vector<int> comp1;
vector<int> comp2;

int MIN(int x, int y){
    return x < y ? x : y;
}

void dfs(int v) {
    comp1.push_back(v);
    int size = (int)graph[v].size();
    for(int i = 0; i <= size - 1; i++) {
        int next = graph[v][i];
        if(!mark[next]) {
            mark[next] = true;
            dfs(next);
        }
    }
}

void maxComp(int n) {
    for(int i = 0; i <= n - 1; i++) {
        comp1.clear();
        if(!mark[i]) {
            mark[i] = true;
            dfs(i);
            int s1 = (int)comp1.size(), s2 = (int)comp2.size();
            if(s1 > s2) {
                comp2 = comp1;
            } else if(s1 == s2) {
                int edges = 0, maxEdges = 0, size = (int)comp1.size(), min = 1000001, maxMin = 1000001;
                for(int j = 0; j <= size - 1; j++) {
                    edges += graph[comp1[j]].size();
                    min = MIN(min, comp1[j]);
                    maxEdges += graph[comp2[j]].size();
                    maxMin = MIN(maxMin, comp2[j]);
                }
                if(edges > maxEdges || (edges == maxEdges && min < maxMin)) {
                    comp2 = comp1;
                }
            }
        }
    }
}

void printGraph(int m, int n, int *vert1, int *vert2) {
    
    cout << "graph {" << endl;
    
    int size = (int)comp2.size();
    for(int i = 0; i <= size - 1; i++) {
        mark[comp2[i]] = false;
    }
    
    for(int i = 0; i <= n - 1; i++) {
        if(!mark[i]) {
            cout << "   " << i << " [color = red]" << endl;
        } else {
            cout << "   " << i << endl;
        }
    }
    
    for(int i = 0; i <= m - 1; i++) {
        if(!mark[vert1[i]] || !mark[vert2[i]]) {
            cout << "   " << vert1[i] << " -- " << vert2[i] << " [color = red]" << endl;
        } else {
            cout << "   " << vert1[i] << " -- " << vert2[i] << endl;
        }
    }
    
    cout << "}" << endl;
    
}

int main(int argc, const char * argv[]) {
    
    int n, m;
    cin >> n >> m;
    
    int *vert1 = new int[m];
    int *vert2 = new int[m];
    for(int i = 0; i <= m - 1; i++) {
        int u, v;
        cin >> u >> v;
        vert1[i] = u;
        vert2[i] = v;
        graph[v].push_back(u);
        graph[u].push_back(v);
    }
    
    maxComp(n);
    
    printGraph(m, n, vert1, vert2);
    
    delete[] vert1;
    delete[] vert2;
    
    return 0;
}
