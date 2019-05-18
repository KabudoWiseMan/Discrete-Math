//
//  main.cpp
//  Mars
//
//  Created by Vsevolod Molchanov on 23.03.17.
//  Copyright © 2017 Vsevolod Molchanov. All rights reserved.
//

#include <iostream>
#include <vector>
#include<algorithm>

using namespace std;

const int MAXM = 1000000;
vector<bool> graph[MAXM];
bool mark[MAXM] = {false};
vector<int> team1[MAXM];
vector<int> team2[MAXM];
vector<int> temp;
vector<int> answer;
bool isSol = true;
vector<int> t;

void dfs(int v, int j, int team) {
    mark[v] = true;
    t[v] = team;
    if(team == 1) {
        team1[j].push_back(v);
    } else {
        team2[j].push_back(v);
    }
    for(int i = 0; i <= graph[v].size() - 1; i++) {
        if(graph[v][i] && !mark[i]) {
            dfs(i, j, team == 1? 2 : 1);
        } else if(graph[v][i] && mark[i] && t[i] == team) {
            isSol = false;
            break;
        }
    }
}

void combinations(int n, int l, int team, int z, vector<int> comb) {
    if(team == 1) {
        for(int i = 0; i <= team1[l].size() - 1; i++) {
            comb.push_back(team1[l][i] + 1);
        }
    } else {
        for(int i = 0; i <= team2[l].size() - 1; i++) {
            comb.push_back(team2[l][i] + 1);
        }
    }
    if(l + 1 != n) {
        combinations(n, l + 1, 1, z, comb);
        combinations(n, l + 1, 2, z, comb);
    } else {
        int k = (int)comb.size(), u = 0;
//        cout << comb.size() << endl;
        for(int i = k; i <= z/2 - 1; i++) {
            comb.push_back(temp[u++] + 1);
        }
        sort(comb.rbegin(), comb.rend(), greater<int>());
        
        int count = 0;
        while(answer[count] == comb[count]) {
            count++;
        }
        if(answer[count] > comb[count]) {
            answer = comb;
        }
    }
}

int main(int argc, const char * argv[]) {
    
    int n;
    cin >> n;
    
    for(int i = 0; i <= n - 1; i++) {
        for(int j = 0; j <= n - 1; j++) {
            graph[i].push_back(false);
        }
    }
    
    for(int i = 0; i <= n - 1; i++) {
        answer.push_back(MAXM);
        int minuses = 0;
        for(int j = 0; j <= n - 1; j++) {
            char c;
            cin >> c;
            if(c == '+') {
                graph[i][j] = graph[j][i] = true;
            } else {
                minuses++;
            }
        }
        if(minuses == n) {
            temp.push_back(i);
            mark[i] = true;
        }
    }
    
    if(temp.size() == n){
        for(int i = 0; i <= n/2 - 1; i++) {
            cout << i + 1 << " ";
        }
        cout << endl;
    } else {
        for(int i = 0; i <= n - 1; i++) {
            t.push_back(-1);
        }
        int j = 0;
        for(int i = 0; i <= n - 1; i++) {
            if(!mark[i]) {
                dfs(i, j++, 1);
            }
        }
        if(!isSol) {
            cout << "No solution" << endl;
        } else {
            t.clear();
            combinations(j, 0, 1, n, t);
            combinations(j, 0, 2, n, t);
            for(int i = 0; i <= answer.size() - 1; i++) {
                cout << answer[i] << " ";
            }
            cout << endl;
        }
    }
    
    return 0;
}
