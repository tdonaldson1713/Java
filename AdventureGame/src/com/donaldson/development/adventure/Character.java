package com.donaldson.development.adventure;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Character {
	private int mLevel;
	private double mStrength;
	private double mDefense;
	private double mAgility;
	private double mSpeed;
	private double mHealth;
	private double mMaxHealth;
	private double mCurrentExp;
	private double mMaxExp;
	
	public Character() {
		mLevel = 0;
		mStrength = mDefense = mAgility = mSpeed = mMaxHealth = 0;
		mCurrentExp = mMaxExp = 0;
	}

	public Character(int l, double str, double d, double a, double spd, double h, double e, double me) {
		mLevel = l;
		mStrength = str;
		mDefense = d;
		mAgility = a;
		mSpeed = spd;
		mHealth = h;
		mMaxHealth = h;
		mCurrentExp = e;
		mMaxExp = me;
	}

	public int getLevel() {
		return mLevel;
	}

	public void setLevel(int level) {
		mLevel = level;
	}
	
	public double getStrength() {
		return mStrength;
	}
	
	public void setStrength(double strength) {
		mStrength = strength;
	}

	public double getDefense() {
		return mDefense;
	}

	public void setDefense(double defense) {
		mDefense = defense;
	}

	public double getAgility() {
		return mAgility;
	}

	public void setAgility(double agility) {
		mAgility = agility;
	}

	public double getSpeed() {
		return mSpeed;
	}

	public void setSpeed(double speed) {
		mSpeed = speed;
	}

	public double getHealth() {
		return mHealth;
	}

	public void setHealth(double health) {
		mHealth = health;
	}

	public void setFullHealth() {
		mHealth = mMaxHealth;
	}

	public void initial_set_full_health() {
		mMaxHealth = mHealth;
	}
	
	public double getCurrentExp() {
		return mCurrentExp;
	}

	public void setCurrentExp(double currentExp) {
		mCurrentExp = currentExp;
	}

	public double getMaxExp() {
		return mMaxExp;
	}

	public void setMaxExp(double maxExp) {
		mMaxExp = maxExp;
	}

	public String toString() {
		String stats = "Level = " + mLevel + "\nStr = " + mStrength + "\nDef = " +
				mDefense + "\nAgility = " + mAgility + "\nSpeed = " + mSpeed + 
				"\nHealth = " + mHealth + "\n";
		return stats;
	}

	public void save_character(String characterFile) {
		try {
			BufferedWriter b_out = new BufferedWriter(new FileWriter(characterFile));
			b_out.append("level=" + getLevel());
			b_out.append("\nstr=" + getStrength());
			b_out.append("\ndef=" + getDefense());
			b_out.append("\nagi=" + getAgility());
			b_out.append("\nspd=" + getSpeed());
			b_out.append("\nhp=" + getHealth());
			b_out.append("\nc_exp=" + getCurrentExp());
			b_out.append("\nm_exp=" + getMaxExp());
			b_out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * We need to get the characters statistics from the file...just
	 * in case the played previously
	 */
	public boolean getCharacterFromFile(String characterFile) {
		ArrayList<String> lines = new ArrayList<String>();
		BufferedReader reader = null;

		try {
			File file = new File(characterFile);

			if (file.exists()) {
				reader = new BufferedReader(new FileReader(file));
				String line;

				while ((line = reader.readLine()) != null) {
					lines.add(line.substring(line.indexOf("=")+1, line.length()));
				}

				for (int a = 0; a < lines.size(); a++) {
					switch (a) {
					case 0:
						setLevel(Integer.valueOf(lines.get(a)));
						break;
					case 1:
						setStrength(Double.valueOf(lines.get(a)));
						break;
					case 2: 
						setDefense(Double.valueOf(lines.get(a)));
						break;
					case 3:
						setAgility(Double.valueOf(lines.get(a)));
						break;
					case 4:
						setSpeed(Double.valueOf(lines.get(a)));
						break;
					case 5: 
						setHealth(Double.valueOf(lines.get(a)));
						mMaxHealth = getHealth();
						break;
					case 6:
						setCurrentExp(Double.valueOf(lines.get(a)));
						break;
					case 7:
						setMaxExp(Double.valueOf(lines.get(a)));
						break;
					} 
				}

				reader.close();

				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}
}
