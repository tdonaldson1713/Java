package com.donaldson.development.adventure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CharacterDialog {
	private final static int num_stat_inc = 2;
	private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	
	public static Character levelup(Character c, int level) {		
		System.out.println("Congratulations! You are now level " + level + "!");
		
		display_level_menu();
		
		for (int a = 0; a < num_stat_inc; a++) {
			System.out.println("Enter stat to increase: ");
			try {
				String input = br.readLine();
				
				try {
					int selection = Integer.parseInt(input);	
					
					switch(selection) {
					case 1:
						c.setStrength(c.getStrength() + 1);
						break;
					case 2:
						c.setDefense(c.getDefense() + 1);
						break;
					case 3:
						c.setAgility(c.getAgility() + 1);
						break;
					case 4:
						c.setSpeed(c.getSpeed() + 1);
						break;
					case 5: // Health increases by 5 per level instead of 1.
						c.setHealth(c.getHealth() + 5);
						break;
					}
					
				} catch(NumberFormatException nfe){
		            System.err.println("Invalid input! Enter 1-5");
		        }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println();
		return c;
	}
	
	private static void display_level_menu() {
		System.out.println("1. Str");
		System.out.println("2. Def");
		System.out.println("3. Agi");
		System.out.println("4. Spd");
		System.out.println("5. Hp");
	}
	
	public static void display_exp(Character c) {
		System.out.println("Current exp: " + c.getCurrentExp() + " / " + c.getMaxExp());
	}
}
