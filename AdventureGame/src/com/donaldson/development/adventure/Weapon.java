package com.donaldson.development.adventure;

public class Weapon {
	private String mHand;
	private String mWeaponName;
	private double mBase;
	private double mMax;
	private double mDPS;
	private int mRequiredLevel;
	
	public String getHand() {
		return mHand;
	}

	public void setHand(String mHand) {
		this.mHand = mHand;
	}

	public String getWeaponName() {
		return mWeaponName;
	}
	
	public void setWeaponName(String weaponName) {
		mWeaponName = weaponName;
	}
	
	public double getBase() {
		return mBase;
	}
	
	public void setBase(double baseDamage) {
		mBase = baseDamage;
	}
	
	public double getMax() {
		return mMax;
	}
	
	public void setMax(double maxDamage) {
		mMax = maxDamage;
	}
	
	public double getDPS() {
		return mDPS;
	}
	
	public void setDPS(double dPS) {
		mDPS = dPS;
	}

	public int getRequiredLevel() {
		return mRequiredLevel;
	}

	public void setRequiredLevel(int requiredLevel) {
		mRequiredLevel = requiredLevel;
	}	
	
	public String toString() {
		String stats = "Hand: " + mHand + "\nName: " + mWeaponName + "\nDmg: " + 
				mBase + "-" + mMax + "\nLevel: " + mRequiredLevel + "\n";
		return stats;
	}
}
