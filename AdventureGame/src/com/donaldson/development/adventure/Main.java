package com.donaldson.development.adventure;

public class Main {
	public static void main(String[] args) {
		Human character = new Human();
		System.out.println(character);
		character.increaseExperience(55);
		character.end_of_battle();
	}

}
