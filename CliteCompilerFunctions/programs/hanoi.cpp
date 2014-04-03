void moveTower (int disks, char start, char end, char temp) {
   if (disks == 1) ;
   else {
      moveTower (disks-1, start, temp, end);
      // move one disk from start to end
      moveTower (disks-1, temp, end, start);
   }
}
int main() {
   int totalDisks;
   totalDisks = 3;
   moveTower(totalDisks, 'A', 'B', 'C');
}
