//
//  main.c
//  Kruskal
//
//  Created by Vsevolod Molchanov on 22.03.17.
//  Copyright © 2017 Vsevolod Molchanov. All rights reserved.
//

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#define swap(x,y) do {   \
typeof(x) _x = x;        \
typeof(y) _y = y;        \
x = _y;                  \
y = _x;                  \
} while(0)

struct Pair{
    int x, y;
};

struct Tree{
    struct Pair a;
    int depth;
    struct Tree *parent;
};

struct Road{
    int l, r;
    double distance;
};

struct Tree* initTree(int x, int y) {
    struct Tree *t = (struct Tree*)malloc(sizeof(struct Tree));
    t->a.x = x;
    t->a.y = y;
    t->depth = 0;
    t->parent = t;
    return t;
}

struct Road initRoad(int x, int y, double distance) {
    struct Road r;
    r.l = x;
    r.r = y;
    r.distance = distance;
    return r;
}

struct Tree* find(struct Tree *x){
    if(x->parent == x)
        return x;
    else
        return find(x->parent);
}

void Union(struct Tree *x, struct Tree *y){
    struct Tree *rootX = find(x);
    struct Tree *rootY = find(y);
    if(rootX->depth < rootY->depth) {
        rootX->parent = rootY;
    } else {
        rootY->parent = rootX;
        if(rootX->parent == rootY->parent && rootX != rootY)
            rootX->depth++;
    }
}

int compareRoads(struct Road a, struct Road b) {
    if (a.distance == b.distance)
        return 0;
    return a.distance < b.distance ? -1 : 1;
}

void Heapify(struct Road *roads, int i, int n){
    int l = 2 * i + 1;
    int r = l + 1;
    int j = i;
    if( (r < n) && (compareRoads(roads[i], roads[r]) == 1) ) j = r;
    if( (l < n) && (compareRoads(roads[j], roads[l]) == 1) ) j = l;
    if(i != j){
        swap(roads[i], roads[j]);
        Heapify(roads, j, n);
    }
}

void BuildHeap(struct Road *roads, int n){
    int i = n / 2 - 1;
    while(i >= 0){
        Heapify(roads, i, n);
        i--;
    }
}

double spanningTree(struct Tree **t, struct Road *r, int j, int n, int m) {
    int l = m - 1;
    double dist = 0;
    for(int i = 0; i < m && j < n - 1; i++) {
        if(find(t[r[0].l]) != find(t[r[0].r])) {
            j++;
            dist += r[0].distance;
            Union(t[r[0].l], t[r[0].r]);
        }
        r[0] = r[l];
        Heapify(r, 0, l + 1);
    }
    return dist;
}

double mstKruskal(struct Tree **t, struct Road *r, int j, int n, int m) {
    return spanningTree(t, r, j, n, m);
}

int main(int argc, const char * argv[]) {
    
    int n;
    scanf("%d", &n);
    
    struct Tree **t = (struct Tree**)malloc(n * sizeof(struct Tree*));
    for(int i = 0; i <= n - 1; i++) {
        int x, y;
        scanf("%d %d", &x, &y);
        t[i] = initTree(x, y);
    }
    
    struct Road *r = (struct Road*)malloc((n-1) * n/2 * sizeof(struct Road));
    int k = 0, j = 0;
    for(int i = 0; i <= n - 1; i++) {
        for(j = i + 1; j <= n - 1; j++) {
            r[k++] = initRoad(i, j, sqrt(pow(t[i]->a.x - t[j]->a.x, 2) + pow(t[i]->a.y - t[j]->a.y, 2)));
        }
    }
    
    BuildHeap(r, k);
    double distance = mstKruskal(t, r, 0, n, k);
    printf("%.2f\n", distance);
    
    for(int i = 0; i <= n - 1; i++) {
        free(t[i]);
    }
    free(t);
    free(r);
    
    return 0;
}

