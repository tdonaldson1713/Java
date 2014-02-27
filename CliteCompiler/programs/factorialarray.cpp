int main ( ) {
    //int n, i, f[6];
    int n, i, j, f[6];
    n = 5;
    i = 1;
    j = 0;
    f[0] = 1;
    f[1] = 1;
    while (i < n) {
        j = i;
        i = i + 1;
        //f[i] = f[i-1] * i;
        f[i] = f[j] * i;
    }
}

//{ <f[6], [1, 1, 2, 6, 24, 120]>, <n, 5>, <j, 4>, <i, 5> }
