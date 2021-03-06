package com.grave.objects.weapons.ranged;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.grave.AssetManager;
import com.grave.achievements.Metrics;
import com.grave.entities.Player;
import com.grave.gfx.Camera;
import com.grave.gfx.particles.Particle;
import com.grave.gfx.particles.Projectile;
import com.grave.gfx.particles.ProjectileType;
import com.grave.gfx.particles.emitters.BloodGenerator;
import com.grave.math.Calculate;
import com.grave.math.Dice;
import com.grave.misc.Pair;
import com.grave.objects.weapons.ArmConfig;
import com.grave.objects.weapons.DamageType;
import com.grave.objects.weapons.WType;
import com.grave.states.GameState;

public class AWP extends RangedWeapon {
	private static final int PRICE = 5_000;
	private static final int AMMO_PRICE = 125;
	private static final long COOLDOWN = 1_000L;
	private static final int CLIP_SIZE = 10;
	private static final int START_CLIPS = 2;
	private static final int MAX_CLIPS = 5;
	private static final long RELOAD_TIME = 2_000L;
	private static final float LASER_SIGHT_RANGE = 300.0f;
	private static final float KNOCKBACK = 10.0f;
	private static final String FIRE_SOUND = "sniper_shot";
	private static final String RELOAD_SOUND = "buy_ammo2";

	private static final Dice DAMAGE = new Dice(5, 4);
	private static final int DAMAGE_MOD = 40;

	public AWP() {
		super(Size.MEDIUM, false);

		AssetManager assets = AssetManager.getManager();

		this.useSound = assets.getSound(AWP.FIRE_SOUND);
		this.reloadSound = assets.getSound(AWP.RELOAD_SOUND);

		this.shakeEffect = new Camera.ShakeEffect(200L, 50L, 15.0f);

		addMuzzleFlash();
	}

	@Override
	public void render(GameState gs, Graphics g, long cTime) {
		super.render(gs, g, cTime);

		Player player = Player.getPlayer();
		float theta = (player.getRotation() - (float)(Math.PI / 2));

		// Render a laser sight.
		if(equipped) {
			Pair<Float> muzzlePos = new Pair<Float>((player.getPosition().x + 5.0f), (player.getPosition().y));
			Pair<Float> laserPos = Calculate.rotateAboutPoint(player.getPosition(), muzzlePos, theta);
			float x2 = (laserPos.x + ((float)Math.cos(theta) * AWP.LASER_SIGHT_RANGE));
			float y2 = (laserPos.y + ((float)Math.sin(theta) * AWP.LASER_SIGHT_RANGE));

			g.setColor(Color.red);
			g.drawLine(laserPos.x, laserPos.y, x2, y2);
		}
	}

	@Override
	public boolean canUse(long cTime) { return (super.canUse(cTime) && release); }

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
		projectile.setPenetrations(2);
		projectiles.add(projectile);

		super.use(player, position, theta, cTime);
	}

	@Override
	public Pair<Integer> getDamageRange() { return AWP.DAMAGE.getRange(AWP.DAMAGE_MOD); }

	@Override
	public double rollDamage(boolean critical) { return AWP.DAMAGE.roll(AWP.DAMAGE_MOD, critical); }

	@Override
	public DamageType getDamageType() { return DamageType.PIERCING; }

	@Override
	public float getKnockback() { return AWP.KNOCKBACK; }

	@Override
	public long getReloadTime() { return AWP.RELOAD_TIME; }

	@Override
	public ArmConfig getArmConfig() { return ArmConfig.AWP; }

	@Override
	public Image getInventoryIcon() { return WType.AWP.getImage(); }

	@Override
	public int getClipSize() { return AWP.CLIP_SIZE; }

	@Override
	public int getStartClips() { return AWP.START_CLIPS; }

	@Override
	public int getMaxClips() { return AWP.MAX_CLIPS; }

	@Override
	public long getCooldown() { return AWP.COOLDOWN; }

	@Override
	public ProjectileType getProjectile() { return ProjectileType.RIFLE; }

	@Override
	public String getProjectileName() { return null; }

	@Override
	public int getPrice() { return AWP.PRICE; }

	@Override
	public int getAmmoPrice() { return AWP.AMMO_PRICE; }

	@Override
	public WType getType() { return WType.AWP; }

	@Override
	public int getLevelRequirement() { return 15; }

	@Override
	public Metrics getWeaponMetric() { return Metrics.AWP; }

	@Override
	public String getName() {
		return WType.AWP.getName();
	}

	@Override
	public String getDescription() {
		return WType.AWP.getDescription();
	}
}
