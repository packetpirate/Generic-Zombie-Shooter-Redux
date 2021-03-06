package com.grave.objects.weapons.ranged;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.BasicGameState;

import com.grave.AssetManager;
import com.grave.Globals;
import com.grave.achievements.Metrics;
import com.grave.entities.Player;
import com.grave.gfx.Animation;
import com.grave.gfx.particles.Particle;
import com.grave.gfx.particles.Projectile;
import com.grave.gfx.particles.ProjectileType;
import com.grave.gfx.particles.emitters.BloodGenerator;
import com.grave.math.Dice;
import com.grave.misc.Pair;
import com.grave.objects.weapons.ArmConfig;
import com.grave.objects.weapons.WType;
import com.grave.states.GameState;

public class AK47 extends RangedWeapon {
	private static final int PRICE = 2_500;
	private static final int AMMO_PRICE = 80;
	private static final long COOLDOWN = 100L;
	private static final int CLIP_SIZE = 30;
	private static final int START_CLIPS = 4;
	private static final int MAX_CLIPS = 10;
	private static final long RELOAD_TIME = 2_000L;
	private static final float KNOCKBACK = 1.0f;
	private static final float MAX_DEVIATION = (float)(Math.PI / 18.0);
	private static final String FIRE_SOUND = "m4a1_shot_01";
	private static final String RELOAD_SOUND = "buy_ammo2";

	private static final Dice DAMAGE = new Dice(1, 10);
	private static final int DAMAGE_MOD = 4;

	private Animation muzzleFlash;

	public AK47() {
		super(Size.MEDIUM);

		AssetManager assets = AssetManager.getManager();

		this.muzzleFlash = assets.getAnimation("GZS_MuzzleFlash");
		this.useSound = assets.getSound(AK47.FIRE_SOUND);
		this.reloadSound = assets.getSound(AK47.RELOAD_SOUND);
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		super.update(gs, cTime, delta);

		// Update muzzle flash animation.
		if(muzzleFlash.isActive(cTime)) muzzleFlash.update(cTime);
	}

	@Override
	public void render(GameState gs, Graphics g, long cTime) {
		super.render(gs, g, cTime);

		// Render muzzle flash.
		Pair<Float> mp = new Pair<Float>((Player.getPlayer().getPosition().x + 5.0f), (Player.getPlayer().getPosition().y - 28.0f));
		if(muzzleFlash.isActive(cTime)) muzzleFlash.render(g, mp, Player.getPlayer().getPosition(), (Player.getPlayer().getRotation() - (float)(Math.PI / 2)));
	}

	@Override
	public void use(Player player, Pair<Float> position, float theta, long cTime) {
		Color color = getProjectile().getColor();
		float velocity = getProjectile().getVelocity();
		float width = getProjectile().getWidth();
		float height = getProjectile().getHeight();
		long lifespan = getProjectile().getLifespan();

		boolean critical = isCritical();
		double dmg = getDamageTotal(critical);

		float deviation = Globals.rand.nextFloat() * (MAX_DEVIATION / 2) * (Globals.rand.nextBoolean() ? 1 : -1);

		Particle particle = new Particle(color, position, velocity, (theta + deviation),
										 0.0f, new Pair<Float>(width, height),
										 lifespan, cTime);
		Projectile projectile = new Projectile(particle, BloodGenerator.BURST, dmg, critical);
		projectiles.add(projectile);

		super.use(player, position, theta, cTime);
	}

	@Override
	public Pair<Integer> getDamageRange() { return AK47.DAMAGE.getRange(AK47.DAMAGE_MOD); }

	@Override
	public double rollDamage(boolean critical) { return AK47.DAMAGE.roll(AK47.DAMAGE_MOD, critical); }

	@Override
	public float getKnockback() { return AK47.KNOCKBACK; }

	@Override
	public long getReloadTime() { return AK47.RELOAD_TIME; }

	@Override
	public ArmConfig getArmConfig() { return ArmConfig.AK47; }

	@Override
	public Image getInventoryIcon() { return WType.AK47.getImage(); }

	@Override
	public int getClipSize() { return AK47.CLIP_SIZE; }

	@Override
	public int getStartClips() { return AK47.START_CLIPS; }

	@Override
	public int getMaxClips() { return AK47.MAX_CLIPS; }

	@Override
	public long getCooldown() { return AK47.COOLDOWN; }

	@Override
	public ProjectileType getProjectile() { return ProjectileType.ASSAULT; }

	@Override
	public String getProjectileName() { return null; }

	@Override
	public int getPrice() { return AK47.PRICE; }

	@Override
	public int getAmmoPrice() { return AK47.AMMO_PRICE; }

	@Override
	public WType getType() { return WType.AK47; }

	@Override
	public int getLevelRequirement() { return 8; }

	@Override
	public Metrics getWeaponMetric() { return Metrics.AK47; }

	@Override
	public String getName() {
		return WType.AK47.getName();
	}

	@Override
	public String getDescription() {
		return WType.AK47.getDescription();
	}
}
