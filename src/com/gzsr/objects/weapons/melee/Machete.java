package com.gzsr.objects.weapons.melee;

import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;

public class Machete extends MeleeWeapon {
	private static final Pair<Float> HIT_AREA_SIZE = new Pair<Float>(96.0f, 64.0f);
	private static final float HIT_AREA_OFFSET = -32.0f;
	private static final float IMAGE_DISTANCE = -8.0f;
	private static final long ATTACK_TIME = 400L;
	private static final long COOLDOWN = 500L;
	private static final float THETA_OFFSET = (float)(Math.PI / 3.6);
	private static final int MIN_DAMAGE_COUNT = 4;
	private static final int MIN_DAMAGE_SIDES = 4;
	private static final int MIN_DAMAGE_MOD = 2;
	private static final String ICON_NAME = "GZS_Machete_Icon";
	
	public Machete() {
		super();
		
		img = AssetManager.getManager().getImage("GZS_Machete");
		useSound = AssetManager.getManager().getSound("throw2");
		
		damage = new Dice(MIN_DAMAGE_COUNT, MIN_DAMAGE_SIDES);
	}
	
	@Override
	public int rollDamage() { return damage.roll(MIN_DAMAGE_MOD, isCurrentCritical()); }
	
	@Override
	public float getDistance() { return Machete.HIT_AREA_OFFSET; }
	
	@Override
	public float getImageDistance() { return Machete.IMAGE_DISTANCE; }

	@Override
	public Pair<Float> getHitAreaSize() { return Machete.HIT_AREA_SIZE; }

	@Override
	public float getThetaOffset() { return Machete.THETA_OFFSET; }

	@Override
	public long getAttackTime() { return Machete.ATTACK_TIME; }
	
	@Override
	public long getCooldown() { return Machete.COOLDOWN; }

	@Override
	public boolean isChargedWeapon() { return false; }

	@Override
	public int getPrice() { return 0; }

	@Override
	public Pair<Integer> getDamage() { return damage.getRange(MIN_DAMAGE_MOD); }

	@Override
	public float getKnockback() { return 5.0f; }

	@Override
	public Image getInventoryIcon() { return AssetManager.getManager().getImage(ICON_NAME); }
	
	@Override
	public String getName() {
		return "Machete";
	}

	@Override
	public String getDescription() {
		return "A simple blade, but it's sharp enough to take their heads off.";
	}
}