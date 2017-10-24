package com.gzsr.objects.weapons;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Graphics;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.entities.enemies.EnemyController;
import com.gzsr.gfx.Animation;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;
import com.gzsr.status.StatusEffect;

public class Explosion implements Entity {
	private Animation anim;
	private Pair<Float> position;
	private StatusEffect status;
	private long statusDuration;
	private double damage;
	private float radius;
	private boolean started;
	
	private List<Entity> entitiesAffected; // TODO: figure out a better way to handle this -_-
	
	public Explosion(String animName_, Pair<Float> position_, double damage_, float radius_) {
		this(animName_, position_, null, 0L, damage_, radius_);
	}
	
	public Explosion(String animName_, Pair<Float> position_, StatusEffect status_, long statusDuration_, double damage_, float radius_) {
		this.anim = AssetManager.getManager().getAnimation(animName_);
		this.position = position_;
		this.status = status_;
		this.statusDuration = statusDuration_;
		this.damage = damage_;
		this.radius = radius_;
		this.started = false;
		
		this.entitiesAffected = new ArrayList<Entity>();
	}
	
	@Override
	public void update(GameState gs, long cTime) {
		if(!started) {
			anim.restart(cTime);
			started = true; 
		}
		
		// Update the animation.
		if(anim != null) anim.update(cTime);
		
		if(isActive(cTime)) {
			// TODO: In future, will have to also check for collision with player structures (turret, barrier, etc).
			// Check for collision with player.
			checkCollision(Globals.player);
			
			// Check for collisions with enemies.
			EnemyController ec = (EnemyController)gs.getEntity("enemyController");
			ec.getAliveEnemies().stream().forEach(e -> checkCollision(e));
		}
	}

	@Override
	public void render(Graphics g, long cTime) {
		if(started) {
			if((anim != null) && anim.isActive(cTime)) anim.render(g, position, new Pair<Float>(radius, radius));
		}
	}
	
	public boolean isActive(long cTime) { return anim.isActive(cTime); }
	
	/**
	 * Checks for collision with given entity and deals damage if a collision is detected.
	 * @param e The entity to check for a collision against.
	 * @return Whether or not the entity has collided with the explosion.
	 */
	public boolean checkCollision(Entity e) {
		// If this entity has been damaged by this explosion already, ignore this collision.
		if(!entitiesAffected.contains(e)) {
			// If damage is taken, calculate damage based on distance from source.
			if(e instanceof Player) {
				float dist = Calculate.Distance(position, Globals.player.getPosition());
				if(dist <= radius) {
					Globals.player.takeDamage(damage * (1.0f - (dist / radius)));
					if(status != null) Globals.player.addStatus(status, statusDuration);
					entitiesAffected.add(Globals.player);
					return true;
				} else return false;
			} else if(e instanceof Enemy) {
				// TODO: Add support for adding status effect to an enemy.
				Enemy en = (Enemy)e;
				float dist = Calculate.Distance(position, en.getPosition());
				if(dist <= radius) {
					en.takeDamage(damage * (1.0f - (dist / radius)));
					entitiesAffected.add(en);
					return true;
				} else return false;
			}
		}
		
		return false;
	}
}