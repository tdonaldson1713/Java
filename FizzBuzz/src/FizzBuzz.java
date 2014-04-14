
public class FizzBuzz {
	public static void main(String[] args) {
		String fizz = "fizz", buzz = "buzz";
		
		for (int a = 1; a < 101; a++) {
			if (a % 15 == 0) {
				System.out.println(fizz + buzz + ": " + a);
			} else if (a % 3 == 0) {
				System.out.println(fizz + ": " + a);
			} else if (a % 5 == 0) {
				System.out.println(buzz + ": " + a);
			} else {
				System.out.println(a);
			}
		}
	}
}
