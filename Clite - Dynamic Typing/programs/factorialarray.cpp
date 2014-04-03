int main ( ) {
    n = 5;
    i = 1;
    j = 0;
    f[0] = 1;
    f[1] = 1;
    while (i < n) {
        j = i;
        i = i + 1;
        f[i] = f[j] * i;
    }
}
