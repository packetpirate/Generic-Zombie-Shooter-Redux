package com.gzsr.objects.weapons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.Globals;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public abstract class Weapon implements Entity {
	protected Sound fireSound;
	protected Sound reloadSound;
	
	protected Dice damage;
	protected List<Projectile> projectiles;
	
	protected int ammoInClip;
	protected int ammoInInventory;
	protected boolean equipped;
	protected long lastFired;
	protected boolean reloading;
	protected long reloadStart;
	
	public Weapon() {
		this.fireSound = null;
		this.reloadSound = null;
		
		this.damage = null; // Must be set by each individual weapon.
		this.projectiles = new ArrayList<Projectile>();
		
		this.ammoInClip = getClipSize();
		this.ammoInInventory = (getStartClips() - 1) * getClipSize();
		this.equipped = false;
		this.lastFired = -getCooldown();
		this.reloading = false;
		this.reloadStart = 0L;
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		// Basically just checking to see if the reload time has elapsed.
		if(!isReloading(cTime)) reloading = false;
		
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
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		// Render all projectiles.
		projectiles.stream()
				   .filter(p -> p.isAlive(cTime))
				   .forEach(p -> p.render(g, cTime));
	}
	
	public boolean canFire(long cTime) {
		if(reloading) return false;
		boolean clipNotEmpty = ammoInClip > 0;
		boolean ammoLeft = ammoInInventory > 0;
		boolean cool = (cTime - lastFired) >= getCooldown();
		
		if(!clipNotEmpty) return false;
		
		return (Player.getPlayer().isAlive() && equipped && (clipNotEmpty || ammoLeft) && cool);
	}
	
	public abstract void fire(Player player, Pair<Float> position, float theta, long cTime);
	
	public void reload(long cTime) {
		if(ammoInInventory > 0) {
			reloading = true;
			reloadStart = cTime;
			
			int takeFromInv = getClipSize() - ammoInClip;
			int taken = Math.min(takeFromInv, ammoInInventory);
			ammoInInventory -= taken;
			ammoInClip += taken;
			
			if(reloadSound != null) reloadSound.play();
		}
	}
	
	public boolean isChargedWeapon() {
		return false;
	}

	public void equip() {
		equipped = true;
	}
	
	public void weaponChanged() {
		// To be overridden.
		equipped = false;
	}
	
	public abstract int getPrice();
	public abstract int getAmmoPrice();
	public int getMaxAmmoPrice() {
		int maxAmmo = getMaxClips() * getClipSize();
		int currentAmmo = ammoInClip + ammoInInventory;
		int difference = maxAmmo - currentAmmo;
		float pricePerAmmo = getAmmoPrice() / getClipSize();
		
		return Math.round(pricePerAmmo * difference);
	}
	public abstract Pair<Integer> getDamage();
	protected boolean isCritical() { return (Globals.rand.nextFloat() <= Player.getPlayer().getAttributes().getFloat("critChance")); }
	public abstract float getKnockback();
	public abstract boolean isReloading(long cTime);
	public abstract double getReloadTime(long cTime);
	public abstract long getReloadTime();
	public abstract Image getInventoryIcon();
	public abstract int getClipSize();
	public int getClipAmmo() { return ammoInClip; }
	public abstract int getStartClips();
	public abstract int getMaxClips();
	public boolean clipsMaxedOut() {
		int totalAmmo = ammoInClip + ammoInInventory;
		int maxAmmo = getMaxClips() * getClipSize();
		return (totalAmmo >= maxAmmo); 
	}
	public int getInventoryAmmo() { return ammoInInventory; }
	public void addInventoryAmmo(int amnt) {
		if(!clipsMaxedOut()) {
			int totalAmmo = ammoInClip + ammoInInventory;
			boolean noOverflow = (totalAmmo + amnt) <= (getClipSize() * getMaxClips()); 
			ammoInInventory += (noOverflow ? amnt : ((getClipSize() * getMaxClips()) - totalAmmo));
		}
	}
	public void maxOutAmmo() { ammoInInventory = (getMaxClips() * getClipSize()) - ammoInClip; }
	public abstract long getCooldown();
	public List<Projectile> getProjectiles() { return projectiles; }
	public abstract ProjectileType getProjectile();
}