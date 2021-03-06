package com.grave.objects.weapons.ranged;

import java.util.Iterator;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.BasicGameState;

import com.grave.AssetManager;
import com.grave.Controls;
import com.grave.achievements.Metrics;
import com.grave.controllers.Scorekeeper;
import com.grave.entities.Player;
import com.grave.gfx.particles.Particle;
import com.grave.gfx.particles.Projectile;
import com.grave.gfx.particles.ProjectileType;
import com.grave.misc.Pair;
import com.grave.objects.weapons.ArmConfig;
import com.grave.objects.weapons.WType;
import com.grave.states.GameState;
import com.grave.status.BurningEffect;

public class MolotovWeapon extends RangedWeapon {
	private static final int PRICE = 0;
	private static final int AMMO_PRICE = -1;
	private static final long COOLDOWN = 0L;
	private static final int CLIP_SIZE = 1;
	private static final int START_CLIPS = 1;
	private static final int MAX_CLIPS = 4;
	private static final float CHARGE_RATE = 0.0015f;
	private static final long RELOAD_TIME = 1_000L;
	private static final String PROJECTILE_NAME = "GZS_Pipe_Bomb";
	private static final String FIRE_SOUND = "throw2";

	private boolean charging;
	private float charge;

	public MolotovWeapon() {
		super(Size.SMALL, false);

		this.useSound = AssetManager.getManager().getSound(MolotovWeapon.FIRE_SOUND);
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		// Basically just checking to see if the reload time has elapsed.
		if(reloading && !isReloading(cTime)) {
			int takeFromInv = getClipSize() - ammoInClip;
			int taken = Math.min(takeFromInv, ammoInInventory);
			ammoInInventory -= taken;
			ammoInClip += taken;

			reloading = false;
		}

		// Update all projectiles.
		if(!getProjectiles().isEmpty()) {
			Iterator<Projectile> it = getProjectiles().iterator();
			while(it.hasNext()) {
				Particle p = it.next();
				if(p.isAlive(cTime)) {
					p.update(gs, cTime, delta);
				} else {
					p.onDestroy((GameState)gs, cTime);
					it.remove();
				}
			}
		}

		if(equipped) {
			if(charging) {
				// If we're charging, increase charge up to max of 1.0f.
				charge += MolotovWeapon.CHARGE_RATE * delta;
				if(charge > 1.0f) charge = 1.0f;
			}

			if(Controls.getInstance().getMouse().isLeftDown()) {
				// If the mouse is down and we're not charging, start charging.
				if(!charging && (getClipAmmo() > 0)) charging = true;
			} else {
				if(charging) {
					// If the mouse is released / down and we're currently
					// charging, release and stop charging!
					release = true;
					charging = false;

					Player player = Player.getPlayer();
					if(canUse(cTime)) use(player, player.getPosition(), player.getRotation(), cTime);
				}
			}
		}
	}

	@Override
	public void render(GameState gs, Graphics g, long cTime) {
		super.render(gs, g, cTime);

		if(equipped && charging) {
			// Render the charge bar.
			Player player = Player.getPlayer();
			g.setColor(Color.white);
			g.drawRect((player.getPosition().x - 24.0f), (player.getPosition().y - 44.0f), 48.0f, 15.0f);

			if(charge < 0.3f) g.setColor(Color.red);
			else if(charge < 0.75f) g.setColor(Color.yellow);
			else g.setColor(Color.green);

			g.fillRect((player.getPosition().x - 23.0f), (player.getPosition().y - 43.0f), (charge * 46.0f), 13.0f);
		}
	}

	@Override
	public void use(Player player, Pair<Float> position, float theta, long cTime) {
		Color color = getProjectile().getColor();
		float velocity = getProjectile().getVelocity();
		float width = getProjectile().getWidth();
		float height = getProjectile().getHeight();
		long lifespan = (long)(getProjectile().getLifespan() * charge);
		Particle particle = new Particle(getProjectileName(), color, position, velocity, theta,
										 0.0f, new Pair<Float>(width, height),
										 lifespan, cTime);

		Molotov molotov = new Molotov(particle);
		projectiles.add(molotov);

		charge = 0.0f;
		super.use(player, position, theta, cTime);
		Scorekeeper.getInstance().addShotsFired(-1); // Molotov throws shouldn't count against accuracy.
	}

	@Override
	public void unequip() {
		super.unequip();

		// Prevents molotov from being thrown after we've switched weapons.
		charging = false;
		charge = 0.0f;
	}

	@Override
	public long getReloadTime() { return MolotovWeapon.RELOAD_TIME; }

	@Override
	public long getCooldown() { return MolotovWeapon.COOLDOWN; }

	@Override
	public int getClipSize() { return MolotovWeapon.CLIP_SIZE; }

	@Override
	public int getClipCapacity() { return getClipSize(); }

	@Override
	public int getStartClips() { return MolotovWeapon.START_CLIPS; }

	@Override
	public int getMaxClips() { return MolotovWeapon.MAX_CLIPS; }

	@Override
	public int getPrice() { return MolotovWeapon.PRICE; }

	@Override
	public int getAmmoPrice() { return MolotovWeapon.AMMO_PRICE; }

	@Override
	public ProjectileType getProjectile() { return ProjectileType.MOLOTOV; }

	@Override
	public String getProjectileName() { return MolotovWeapon.PROJECTILE_NAME; }

	@Override
	public boolean isChargedWeapon() { return true; }

	@Override
	public boolean isCharging() { return charging; }

	@Override
	public Pair<Integer> getDamageRange() { return BurningEffect.getDamageRange(); }

	@Override
	public double rollDamage(boolean critical) { return 0.0; }

	@Override
	public float getKnockback() { return 0.0f; }

	@Override
	public ArmConfig getArmConfig() { return ArmConfig.MOLOTOV; }

	@Override
	public Image getInventoryIcon() { return WType.MOLOTOV.getImage(); }

	@Override
	public WType getType() { return WType.MOLOTOV; }

	@Override
	public int getLevelRequirement() { return 8; }

	@Override
	public Metrics getWeaponMetric() { return Metrics.MOLOTOV; }

	@Override
	public String getName() { return WType.MOLOTOV.getName(); }

	@Override
	public String getDescription() { return WType.MOLOTOV.getDescription(); }
}
