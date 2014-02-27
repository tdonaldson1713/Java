int main ( ) {
    float a, b, c, d;
    a = 2.0; b = 4.5;
    c = 3.3; d = 2.2;

    if (a > b && a > c)
        d = a;
    else if (b > a)
        if (b > c)
            d = b;
        else
            d = c;
}
