package com.grave.objects.weapons.ranged;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import com.grave.AssetManager;
import com.grave.achievements.Metrics;
import com.grave.entities.Player;
import com.grave.gfx.Camera;
import com.grave.gfx.particles.Particle;
import com.grave.gfx.particles.Projectile;
import com.grave.gfx.particles.ProjectileType;
import com.grave.gfx.particles.emitters.BloodGenerator;
import com.grave.math.Dice;
import com.grave.misc.Pair;
import com.grave.objects.weapons.ArmConfig;
import com.grave.objects.weapons.WType;

public class Beretta extends RangedWeapon {
	private static final int AMMO_PRICE = 5;
	private static final long COOLDOWN = 500L;
	private static final int CLIP_SIZE = 12;
	private static final int START_CLIPS = 4;
	private static final int MAX_CLIPS = 10;
	private static final long RELOAD_TIME = 1_500L;
	private static final float KNOCKBACK = 1.0f;
	private static final String FIRE_SOUND = "beretta_shot_01";
	private static final String RELOAD_SOUND = "buy_ammo2";

	private static final Dice DAMAGE = new Dice(1, 12);
	private static final int DAMAGE_MOD = 8;

	public Beretta() {
		super(Size.SMALL, false);

		AssetManager assets = AssetManager.getManager();

		this.useSound = assets.getSound(Beretta.FIRE_SOUND);
		this.reloadSound = assets.getSound(Beretta.RELOAD_SOUND);

		this.shakeEffect = new Camera.ShakeEffect(100L, 20L, 5.0f);

		addMuzzleFlash();
	}

	@Override
	public void use(Player player, Pair<Float> position, float theta, long cTime) {
		Color color = getProjectile().getColor();
		float velocity = getProjectile().getVelocity();
		float width = getProjectile().getWidth();
		float height = getProjectile().getHeight();
		long lifespan = getProjectile().getLifespan();
		Particle particle = new Particle(color, position, velocity, theta,
										 0.0f, new Pair<Float>(width, height),
										 lifespan, cTime);

		boolean critical = isCritical();
		double dmg = getDamageTotal(critical);

		Projectile projectile = new Projectile(particle, BloodGenerator.BURST, dmg, critical);
		projectiles.add(projectile);

		super.use(player, position, theta, cTime);
	}

	@Override
	public Pair<Integer> getDamageRange() { return Beretta.DAMAGE.getRange(Beretta.DAMAGE_MOD); }

	@Override
	public double rollDamage(boolean critical) { return Beretta.DAMAGE.roll(Beretta.DAMAGE_MOD, critical); }

	@Override
	public float getKnockback() { return Beretta.KNOCKBACK; }

	@Override
	public long getReloadTime() { return Beretta.RELOAD_TIME; }

	@Override
	public Image getInventoryIcon() { return WType.BERETTA.getImage(); }

	@Override
	public ArmConfig getArmConfig() { return ArmConfig.BERETTA; }

	@Override
	public int getClipSize() { return Beretta.CLIP_SIZE; }

	@Override
	public int getStartClips() { return Beretta.START_CLIPS; }

	@Override
	public int getMaxClips() { return Beretta.MAX_CLIPS; }

	@Override
	public long getCooldown() { return Beretta.COOLDOWN; }

	@Override
	public ProjectileType getProjectile() { return ProjectileType.HANDGUN; }

	@Override
	public String getProjectileName() { return null; }

	@Override
	public int getPrice() { return 0; }

	@Override
	public boolean canSell() { return false; }

	@Override
	public int getAmmoPrice() { return Beretta.AMMO_PRICE; }

	@Override
	public WType getType() { return WType.BERETTA; }

	@Override
	public int getLevelRequirement() { return 1; }

	@Override
	public Metrics getWeaponMetric() { return Metrics.BERETTA; }

	@Override
	public String getName() {
		return WType.BERETTA.getName();
	}

	@Override
	public String getDescription() {
		return WType.BERETTA.getDescription();
	}
}
