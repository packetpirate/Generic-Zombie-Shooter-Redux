package com.grave.objects.weapons;

import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;

import com.grave.Globals;
import com.grave.achievements.Metrics;
import com.grave.entities.Entity;
import com.grave.entities.Player;
import com.grave.gfx.Layers;
import com.grave.misc.Pair;
import com.grave.objects.weapons.melee.MeleeWeapon;
import com.grave.objects.weapons.ranged.RangedWeapon;

public abstract class Weapon implements Entity {
	protected Sound useSound;

	protected boolean equipped;
	protected long lastUsed;

	public Weapon() {
		this.useSound = null;

		this.equipped = false;
		this.lastUsed = -getCooldown();
	}

	public abstract void use(Player player, Pair<Float> position, float theta, long cTime);
	public abstract boolean canUse(long cTime);

	public boolean isCritical() {
		float roll = Globals.rand.nextFloat();

		if(this instanceof MeleeWeapon) return (roll <= Player.getPlayer().getMeleeCritChance());
		else if(this instanceof RangedWeapon) return (roll <= Player.getPlayer().getRangeCritChance());

		return false;
	}

	public boolean isEquipped() { return equipped; }
	public void equip() {
		equipped = true;
		if(Globals.debug) System.out.printf("%s equipped!\n\n", getName());
	}
	public void unequip() {
		equipped = false;
		if(Globals.debug) System.out.printf("%s un-equipped!\n\n", getName());
	}
	public boolean isChargedWeapon() { return false; };
	public boolean isCharging() { return false; }

	public abstract int getPrice();
	public boolean canSell() { return true; }
	public abstract Pair<Integer> getDamageRange();
	public abstract Pair<Integer> getDamageRangeTotal();
	public abstract double getDamageTotal(boolean critical);
	public abstract double rollDamage(boolean critical);
	public DamageType getDamageType() { return DamageType.NONE; }

	public abstract float getKnockback();
	public abstract Image getInventoryIcon();
	public abstract long getCooldown();

	public abstract WType getType();
	public abstract int getLevelRequirement();
	public abstract Metrics getWeaponMetric();

	@Override
	public int getLayer() { return Layers.NONE.val(); }
}