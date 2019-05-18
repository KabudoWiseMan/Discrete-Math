//
//  main.c
//  Dividers
//
//  Created by Vsevolod Molchanov on 28.02.17.
//  Copyright © 2017 Vsevolod Molchanov. All rights reserved.
//

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

int Partition(unsigned long *array, int low, int high){
    int i = low;
    int j = low;
    while(j < high){
        if(array[j] > array[high]){
            unsigned long remember = array[i];
            array[i] = array[j];
            array[j] = remember;
            i++;
        }
        j++;
    }
    unsigned long remember = array[i];
    array[i] = array[high];
    array[high] = remember;
    return i;
}

void QuickSort(unsigned long *array, int low, int high){
    while(low < high){
        int q = Partition(array, low, high);
        QuickSort(array, low, q - 1);
        low = q + 1;
    }
}

int main(int argc, const char * argv[]) {
    
    unsigned long x;
    scanf("%lu", &x);
    
    printf("graph {\n");
    
    unsigned long *dividers = (unsigned long*)malloc(2000 * sizeof(unsigned long));
    int n = 0;
    for(unsigned long i = 1; i <= (unsigned long)sqrt(x); i++) {
        if(!(x % i) ) {
            dividers[n] = i;
            if(i != x / i ) {
                dividers[n + 1] = x / i;
                n += 2;
            } else {
                n += 1;
            }
            
        }
    }

    QuickSort(dividers, 0, n - 1);
    
    for(int i = 0; i <= n - 1; i++) {
        printf("    %lu\n", dividers[i]);
    }
    
    for(int i = 0; i <= n - 1; i++) {
        for(int j = i + 1; j <= n - 1; j++) {
            if(!(dividers[i] % dividers[j])) {
                char flag = 1;
                for(int k = i + 1; k < j; k++) {
                    if(!(dividers[k] % dividers[j]) && !(dividers[i] % dividers[k])) {
                        flag = 0;
                        break;
                    }
                }
                if(flag) {
                    printf("    %lu -- %lu\n", dividers[i], dividers[j]);
                }
            }
        }
    }
    
    printf("}\n");
    
    free(dividers);
    
    return 0;
}
