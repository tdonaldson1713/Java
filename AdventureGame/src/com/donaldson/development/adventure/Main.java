package com.donaldson.development.adventure;

public class Main {
	public static void main(String[] args) {
		Human character = new Human();
		System.out.println(character);
		System.out.println("");
		System.out.println("Def before inc: " + character.getDefense());
		System.out.println("Inc: " + character.attack_defend());
		System.out.println("Def after inc: " + character.getDefense());
		character.end_of_battle();
		System.out.println("Def after end: " + character.getDefense());
		System.out.println("");
		System.out.println("Slam: " + character.attack_slam());	
		System.out.println("");
		System.out.println("Strike: " + character.attack_strike());
		System.out.println("HP after damage taken: " + character.take_damage(character.attack_strike()));
		System.out.println("Full heal: " + character.full_heal());
	}

}
