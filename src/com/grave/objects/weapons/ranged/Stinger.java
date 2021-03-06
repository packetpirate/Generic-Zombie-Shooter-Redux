package com.grave.objects.weapons.ranged;

import org.newdawn.slick.Image;

import com.grave.AssetManager;
import com.grave.achievements.Metrics;
import com.grave.entities.Player;
import com.grave.gfx.Animation;
import com.grave.gfx.particles.Particle;
import com.grave.gfx.particles.ProjectileType;
import com.grave.math.Dice;
import com.grave.misc.Pair;
import com.grave.objects.weapons.ArmConfig;
import com.grave.objects.weapons.Explosion;
import com.grave.objects.weapons.WType;
import com.grave.talents.Talents;

public class Stinger extends RangedWeapon {
	private static final int PRICE = 38_000;
	private static final int AMMO_PRICE = 10_000;
	private static final long COOLDOWN = 0L;
	private static final int CLIP_SIZE = 1;
	private static final int START_CLIPS = 2;
	private static final int MAX_CLIPS = 4;
	private static final long RELOAD_TIME = 3_000L;
	private static final float KNOCKBACK = 20.0f;
	private static final float EXP_RADIUS = 128.0f;
	private static final String ANIMATION_NAME = "GZS_Stinger_Missile";
	private static final String EXP_NAME = "GZS_Explosion";
	private static final String FIRE_SOUND = "missile";
	private static final String RELOAD_SOUND = "buy_ammo2";

	private static final Dice DAMAGE = new Dice(25, 10);
	private static final int DAMAGE_MOD = 250;

	public Stinger() {
		super(Size.LARGE);

		AssetManager assets = AssetManager.getManager();

		this.useSound = assets.getSound(Stinger.FIRE_SOUND);
		this.reloadSound = assets.getSound(Stinger.RELOAD_SOUND);

		addMuzzleFlash();
	}

	@Override
	public void use(Player player, Pair<Float> position, float theta, long cTime) {
		Animation animation = AssetManager.getManager().getAnimation(Stinger.ANIMATION_NAME);

		float velocity = getProjectile().getVelocity();
		float width = getProjectile().getWidth();
		float height = getProjectile().getHeight();
		long lifespan = getProjectile().getLifespan();
		Particle particle = new Particle(animation, position, velocity, theta,
										 0.0f, new Pair<Float>(width, height),
										 lifespan, cTime);

		boolean critical = isCritical();
		double dmg = getDamageTotal(critical);
		if(Talents.Munitions.DEMOLITIONS.active()) dmg += (dmg * 0.5);

		Explosion exp = new Explosion(Explosion.Type.NORMAL, Stinger.EXP_NAME,
									  new Pair<Float>(0.0f, 0.0f),
									  dmg, critical, Stinger.KNOCKBACK,
									  Stinger.EXP_RADIUS, cTime);
		Missile missile = new Missile(particle, exp);
		projectiles.add(missile);

		super.use(player, position, theta, cTime);
	}

	@Override
	public Pair<Integer> getDamageRange() { return Stinger.DAMAGE.getRange(Stinger.DAMAGE_MOD); }

	@Override
	public double rollDamage(boolean critical) { return Stinger.DAMAGE.roll(Stinger.DAMAGE_MOD, critical); }

	@Override
	public float getKnockback() { return Stinger.KNOCKBACK; }

	@Override
	public long getReloadTime() { return Stinger.RELOAD_TIME; }

	@Override
	public ArmConfig getArmConfig() { return ArmConfig.STINGER; }

	@Override
	public Image getInventoryIcon() { return WType.STINGER.getImage(); }

	@Override
	public int getClipSize() { return Stinger.CLIP_SIZE; }

	@Override
	public int getClipCapacity() { return getClipSize(); }

	@Override
	public int getStartClips() { return Stinger.START_CLIPS; }

	@Override
	public int getMaxClips() { return Stinger.MAX_CLIPS; }

	@Override
	public long getCooldown() { return Stinger.COOLDOWN; }

	@Override
	public ProjectileType getProjectile() { return ProjectileType.MISSILE; }

	@Override
	public String getProjectileName() { return null; }

	@Override
	public int getPrice() { return Stinger.PRICE; }

	@Override
	public int getAmmoPrice() { return Stinger.AMMO_PRICE; }

	@Override
	public WType getType() { return WType.STINGER; }

	@Override
	public int getLevelRequirement() { return 15; }

	@Override
	public Metrics getWeaponMetric() { return Metrics.STINGER; }

	@Override
	public String getName() {
		return WType.STINGER.getName();
	}

	@Override
	public String getDescription() {
		return WType.STINGER.getDescription();
	}
}
