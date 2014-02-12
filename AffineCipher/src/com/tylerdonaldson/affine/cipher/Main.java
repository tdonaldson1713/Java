package com.tylerdonaldson.affine.cipher;

public class Main {
	public static void main(String[] args) {
		String test = "This is testing all the different possiblilties of The alphabet & checking that it works 100% of the time!";
		
		for (int a = 0; a < 51; a++) {
			test = AffineCipher.Encrypt(test);
		}
		
		System.out.println(test);
		
		for (int b = 0; b < 51; b++) {
			test = AffineCipher.Decrypt(test);
		}
		
		System.out.println(test);
	}
}
