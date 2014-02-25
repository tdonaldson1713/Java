package com.donaldson.development.adventure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Human extends Character implements CharacterInterface {
	// Pertains to setup of the character
	private String mainHandWeaponFile = "Files/main_hand.dat";
	private String offHandWeaponFile = "Files/off_hand.dat";
	private String characterFile = "Files/character_stats.dat";
	private BufferedReader reader = null;
	private DecimalFormat df = new DecimalFormat("0.00");

	// Pertains to the character.
	private Weapon mMainHand;
	private Weapon mOffHand;
	private boolean def_inc = false;
	
	// Character stats and attacks
	private double additional_damage_for_crit = 0.05;
	private double beginningDefense = -1;
	private double crit_base = 0.0000000;
	private double crit_bonus_damage = 0.40;
	private double crit_max = 0.0500000;
	private double exp_inc_perc = 1.15;
	private double shield_slam_damage = 1.2;
	private double weapon_slam_damage = 0.6;
	
	
	public Human() {
		if(!getCharacterFromFile(characterFile)) {
			setCharacterBaseStats();
		}
		
		mMainHand = getWeaponFromFile(mainHandWeaponFile);
		mOffHand = getWeaponFromFile(offHandWeaponFile);
	}

	/*
	 * The player attacks with their mainhand weapon.
	 * It does 100% damage
	 */
	public double attack_strike() {
		double damage = ((mMainHand.getMax() - mMainHand.getBase()) * Math.random()) + mMainHand.getBase();
		return Double.valueOf(df.format(damage_crit(damage)));
	}

	/*
	 * The player can attack with their offhand weapon
	 * if the offhand is a weapon, then it does 60% damage.
	 * if the offhand is a shield, then it does 120% damage.
	 */
	public double attack_slam() {
		double slam_damage;
		slam_damage = ((mOffHand.getMax() - mOffHand.getBase()) * Math.random()) + mOffHand.getBase();

		if (mOffHand.getDPS() != 0) { // Weapon 60% damage
			slam_damage *= weapon_slam_damage;
			return Double.valueOf(df.format(damage_crit(slam_damage)));
		} else { // Shield 120% damage
			slam_damage *= shield_slam_damage;
			return Double.valueOf(df.format(damage_crit(slam_damage)));
		}
	}

	/*
	 * The player increases their defense by 110% for the length of the battle.
	 */
	public String attack_defend() {
		beginningDefense = getDefense();
		double new_defense = Double.valueOf(df.format(beginningDefense * 1.1));
		setDefense(new_defense);
		def_inc = true;
		return "Defense raised to " + new_defense;
	}

	/*
	 * Chance to do a critical strike.
	 * This is a percentage of the damage with the strength
	 * of the Human playing a role into how much it scales 
	 * the critical strike damage.
	 */
	private double damage_crit(double damage) {
		if (is_between(Math.random())) {
			System.out.println("Critical Strike!");
			return (damage * crit_bonus_damage) + damage;
		}
		
		return damage;
	}
	
	private double additional_crit_chance() {
		double agi = getAgility();
		double additional_damage = 0;
		while (agi > 0) {
			if (agi < 20) {
				additional_damage += additional_damage_for_crit * agi;
			} else {			
				additional_damage += 1;
			}
			
			agi -= 20;
		}
		
		/*
		 *  We have to divide by 100 here to make it into the same scale as crit_max.
		 *  For example, if we don't divide by 100, the crit chance would be, at base
		 *  agility, 55%. However, if we divide by 100, it'll only be 5.5%, just as it
		 *  is supposed to be.
		 */
		return additional_damage / 100;
	}
	
	private boolean is_between(double value) {
		if (value < (crit_max + additional_crit_chance()) && value > crit_base) {
			return true;
		} else {
			return false;
		}
	}
	
	public Weapon getMainHand() {
		return mMainHand;
	}

	public Weapon getOffHand() {
		return mOffHand;
	}

	/*
	 * This function must be called at the end of
	 * every battle so that the character's new stats
	 * can be saved to file.
	 */
	public void end_of_battle() {
		if (def_inc) {
			resetDefense();
		}
		
		save_character(characterFile);
	}
	
	/*
	 * This function can be called whenever we need
	 * to save the character information, while outside 
	 * of a battle.
	 */
	public void save() {
		save_character(characterFile);
	}
	/*
	 * Sets the characters health after using an item to heal.
	 */
	public double heal_health(double regen) {
		setHealth(getHealth() + regen);
		return getHealth();
	}
	
	public double full_heal() {
		setFullHealth();
		return getHealth();
	}
	/*
	 * Sets the health of the character after being attacked
	 */
	public double take_damage(double dmg_taken) {
		setHealth(getHealth() - dmg_taken);
		return getHealth();
	}

	/*
	 * Existence of this function is that the player has used the defend
	 * attack and the defense must be reset at the end of battle.
	 */
	private void resetDefense() {
		setDefense(beginningDefense);
	}
	
	/*****************************************************************
	 * From here all the way to the bottom pertains to the loading 
	 * of the character
	 *****************************************************************/
	
	/*
	 *  Get the weapon from the file.
	 */
	private Weapon getWeaponFromFile(String fileName) {
		Weapon weapon = new Weapon();
		ArrayList<String> fileLines = new ArrayList<String>();

		try {
			reader = new BufferedReader(new FileReader(new File(fileName)));

			String line;
			// Get each line from the file
			while ((line = reader.readLine()) != null) {
				line = line.substring(line.indexOf("=")+1, line.length());
				fileLines.add(line);
			}

			for (int a = 0; a < fileLines.size(); a++) {
				// if it's necessary to add more cases, simply just add to the 
				// end of the file and continue with the next index of the arraylist.
				switch(a) {
				case 0:
					weapon.setHand(fileLines.get(a));
					break;
				case 1:
					weapon.setWeaponName(fileLines.get(a));
					break;
				case 2:
					weapon.setBase(Integer.valueOf(fileLines.get(a)));
					break;
				case 3:
					weapon.setMax(Integer.valueOf(fileLines.get(a)));
					break;
				case 4:
					weapon.setDPS(Double.valueOf(fileLines.get(a)));
					break;
				case 5: 
					weapon.setRequiredLevel(Integer.valueOf(fileLines.get(a)));
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return weapon;
	}
	
	@Override
	public void setCharacterBaseStats() {
		setLevel(1);
		setStrength(15);
		setDefense(7);
		setAgility(10);
		setSpeed(5);
		setHealth(50);
		setCurrentExp(0);
		setMaxExp(100);
		
		initial_set_full_health();
		save_character(characterFile);
	}

	@Override
	public void levelup() {
		setLevel(getLevel() + 1);
		CharacterDialog.levelup(this, getLevel());
		increaseExperienceOnLevel();
	}

	@Override
	public void increaseExperience(int monster_exp) {
		boolean level = false;
		
		setCurrentExp(getCurrentExp() + monster_exp);
		while (getCurrentExp() >= getMaxExp()) {
			levelup();
			level = true;
		} 
		
		if (!level) {
			CharacterDialog.display_exp(this);
		}
	}
	
	@Override
	public void increaseExperienceOnLevel() {
		setCurrentExp(getCurrentExp() - getMaxExp());
		setMaxExp(Double.valueOf(df.format(getMaxExp() * exp_inc_perc)));
		CharacterDialog.display_exp(this);
	}
}