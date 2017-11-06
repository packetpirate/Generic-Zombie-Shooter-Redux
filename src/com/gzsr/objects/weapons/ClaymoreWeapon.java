package com.gzsr.objects.weapons;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.misc.Pair;
import com.gzsr.status.Status;

public class ClaymoreWeapon extends Weapon {
	private static final long COOLDOWN = 0L;
	private static final int CLIP_SIZE = 1;
	private static final int START_CLIPS = 4;
	private static final long RELOAD_TIME = 1000L;
	private static final String ICON_NAME = "GZS_ClaymoreWeapon";
	private static final String FIRE_SOUND = "landmine_armed";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	public ClaymoreWeapon() {
		super();
		
		AssetManager assets = AssetManager.getManager();
		this.fireSound = assets.getSound(ClaymoreWeapon.FIRE_SOUND);
		this.reloadSound = assets.getSound(ClaymoreWeapon.RELOAD_SOUND);
	}

	@Override
	public void fire(Player player, Pair<Float> position, float theta, long cTime) {
		Color color = getProjectile().getColor();
		float velocity = getProjectile().getVelocity();
		float width = getProjectile().getWidth();
		float height = getProjectile().getHeight();
		long lifespan = getProjectile().getLifespan();
		Particle particle = new Particle("GZS_Claymore", color, position, velocity, theta,
										 0.0f, new Pair<Float>(width, height), 
										 lifespan, cTime);
		Claymore clay = new Claymore(particle);
		projectiles.add(clay);
		
		if(!player.hasStatus(Status.UNLIMITED_AMMO)) ammoInClip--;
		lastFired = cTime;
		
		fireSound.play();
	}

	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < ClaymoreWeapon.RELOAD_TIME) && reloading);
	}

	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)ClaymoreWeapon.RELOAD_TIME);
	}

	@Override
	public Image getInventoryIcon() {
		return AssetManager.getManager().getImage(ClaymoreWeapon.ICON_NAME);
	}

	@Override
	public int getClipSize() {
		return ClaymoreWeapon.CLIP_SIZE;
	}

	@Override
	protected int getStartClips() {
		return ClaymoreWeapon.START_CLIPS;
	}

	@Override
	public long getCooldown() {
		return ClaymoreWeapon.COOLDOWN;
	}
	
	@Override
	public List<Projectile> getProjectiles() {
		List<Projectile> allProjectiles = new ArrayList<Projectile>();
		
		allProjectiles.addAll(projectiles);
		for(Projectile p : projectiles) {
			Claymore clay = (Claymore) p;
			allProjectiles.addAll(clay.getShrapnel());
		}
		
		return allProjectiles;
	}

	@Override
	public ProjectileType getProjectile() {
		return ProjectileType.CLAYMORE;
	}
}