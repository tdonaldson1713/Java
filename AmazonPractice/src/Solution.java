import java.util.ArrayList;

public class Solution {
	public static void main(String[] args) {
		Tree t = new Tree(5);
		t.x = 5;
		t.left = new Tree(8);
		t.right = new Tree(9);
		t.left.left = new Tree(12);
		t.left.right = new Tree(2);
		t.right.left = new Tree(8);
		t.right.right = new Tree(4);
		t.right.left.left = new Tree(2);
		t.right.right.left = new Tree(5);

		System.out.println(solution(t));
		
		//System.out.println(solution(5, 3, -1, 5));
	}
/*
	public static int solution(int A, int B, int C, int D) {
		// Let's set A to be the greatest value in the list.
		boolean found = false;
		if (A > B && A > C && A > D) {
			found = true;
		}
		
		if (!found) {
			if (B > C && B > D) {
				int temp = A;
				A = B;
				B = temp;
			} else if (C > B && C > D) {
				int temp = A;
				A = C;
				C = temp;
			} else if (D > C && D > B) {
				int temp = A;
				A = D;
				D = temp;
			}
		}
		
		// This sets up the best B
		if (Math.abs(A-C) > Math.abs(A-B)) {
			int temp = B;
			B = C; C = temp;
		}
		if (Math.abs(A-D) > Math.abs(A-B)) {
			int temp = B;
			B = D; D = temp;
		}
		
		// This sets up the best C
		if (Math.abs(B-D) > Math.abs(B-C)) {
			int temp = C;
			C = D;
			D = temp;
		}
		
		// This sets up the best D
		if (Math.abs(D-C) > Math.abs(C-D)) {
			int temp = C;
			C = D;
			D = C;
		}
		
		return Math.abs(A-B) + Math.abs(B-C) + Math.abs(C-D);
	}*/
	
	
	public static int solution(Tree T) {
		ArrayList<Integer> array = new ArrayList<Integer>();
		int highestAmp = 0;
		highestAmp = inorder(T, array, highestAmp, 0);
		return highestAmp;
	}
	
	public static int inorder(Tree T, ArrayList<Integer> array, int highestAmp, int currentAmp) {
		if (T != null) {
			array.add(T.x);
			inorder(T.left, array, highestAmp, currentAmp);	
			inorder(T.right, array, highestAmp, currentAmp);
			currentAmp = getAmplitude(array);
			
			if (currentAmp >= highestAmp) {
				highestAmp = currentAmp;
			}
		}
		
		return highestAmp;
	}
	
	public static int getAmplitude(ArrayList<Integer> array) {
		int amp = 0; 
		int largest = 0;
		
		
		// Get the largest value.
		for (int a = 0; a < array.size(); a++) {
			if (array.get(a) > largest) {
				largest = array.get(a);
			}
		}
		
		// Now let's find the largest difference, or amplitude.
		for (int a = 0; a < array.size(); a++) {
			if (largest - array.get(a) > amp) {
				amp = largest - array.get(a);
			}
		}
	
		System.out.print("Array:");
		for (int a = 0; a < array.size(); a++) {
			System.out.print(" " + array.get(a));
		}

		System.out.print(", Amp: " + amp + "\n");
		
		array.remove(array.size()-1);
	
		return amp;
	}
}
