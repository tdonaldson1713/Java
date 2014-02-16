package com.tylerdonaldson.affine.cipher;

public class AffineCipher {
	private static final String alphabet ="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!&#_,.$%@";
	private static final int alphabet_size = alphabet.length();
	private static final int alpha = 32;
	private static final int beta = 16;
	private static final int gamma = 20;


	private static String removespaces(String sent) {
		String new_sent = "";

		for (int a = 0; a < sent.length(); a++) {
			if (sent.charAt(a) != ' ') {
				new_sent += sent.charAt(a);
			}
		}

		return new_sent;
	}

	// E(x) = 32x + 16
	public static String Encrypt(String to_encrypt) {
		to_encrypt = removespaces(to_encrypt);

		String encrypted = "";
		int x, pos_char;

		for (int a = 0; a < to_encrypt.length(); a++) {
			x = alphabet.indexOf(to_encrypt.charAt(a));
			pos_char = (((alpha * x) + beta) % alphabet_size);
			encrypted += alphabet.charAt(pos_char);
		}

		return encrypted;
	}

	// D(x) = 20(y - 16)
	public static String Decrypt(String to_decrypt) {
		String decrypted = "";
		int y, pos_char, input;

		for (int a = 0; a < to_decrypt.length(); a++) {
			y = alphabet.indexOf(to_decrypt.charAt(a));
			input = (gamma * (y - beta));

			while (input < 0) {
				input += alphabet_size;
			}

			pos_char =  input % alphabet_size;
			decrypted += alphabet.charAt(pos_char);
		}

		return decrypted;
	}
}
