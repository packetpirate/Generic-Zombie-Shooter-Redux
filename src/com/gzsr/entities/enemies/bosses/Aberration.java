package com.gzsr.entities.enemies.bosses;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.gzsr.Globals;
import com.gzsr.entities.enemies.EnemyType;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class Aberration extends Boss {
	private static final float HEALTH = 10_000.0f;
	private static final float SPEED = 0.10f;
	private static final float DPS = 20.0f;
	private static final float BILE_DAMAGE = 1.0f;
	private static final float BILE_DEVIATION = (float)(Math.PI / 9);
	private static final long BILE_DELAY = 25L;
	private static final int BILE_PER_TICK = 5;
	private static final float ATTACK_DIST = 200.0f;
	
	private List<Projectile> bile;
	private long lastBile;
	
	public Aberration(Pair<Float> position_) {
		super(EnemyType.ABERRATION, position_);
		this.health = Aberration.HEALTH;
		
		this.bile = new ArrayList<Projectile>();
		this.lastBile = 0L;
	}
	
	@Override
	public void update(GameState gs, long cTime, int delta) {
		if(!dead()) {
			theta = Calculate.Hypotenuse(position, Globals.player.getPosition());
			if(!nearPlayer(Aberration.ATTACK_DIST)) {
				animation.update(cTime);
				move(delta);
			} else vomit(cTime);
		}
		
		// Update bile projectiles.
		Iterator<Projectile> it = bile.iterator();
		while(it.hasNext()) {
			Projectile p = it.next();
			if(p.isAlive(cTime)) {
				p.update(gs, cTime, delta);
				if(Globals.player.checkCollision(p)) {
					Globals.player.takeDamage(p.getDamage());
					it.remove();
				}
			} else it.remove(); // need iterator instead of stream so we can remove if they're dead :/
		}
	}
	
	private void vomit(long cTime) {
		if(cTime >= (lastBile + Aberration.BILE_DELAY)) {
			for(int i = 0; i < Aberration.BILE_PER_TICK; i++) {
				Color color = ProjectileType.BILE.getColor();
				float velocity = ProjectileType.BILE.getVelocity();
				float width = ProjectileType.BILE.getWidth();
				float height = ProjectileType.BILE.getHeight();
				long lifespan = ProjectileType.BILE.getLifespan() * 2;
				float angle = (theta + (float)(Math.PI / 2)) + getBileDeviation();
				float angularVel = ((Globals.rand.nextInt(3) - 1) * 0.001f) * Globals.rand.nextFloat();
				Particle particle = new Particle("GZS_AcidParticle2", color, position, velocity, angle,
												 angularVel, new Pair<Float>(width, height), 
												 lifespan, cTime);
				Projectile projectile = new Projectile(particle, Aberration.BILE_DAMAGE);
				bile.add(projectile);
			}
			lastBile = cTime;
		}
	}
	
	private float getBileDeviation() {
		int rl = Globals.rand.nextInt(3) - 1;
		return ((Globals.rand.nextFloat() * Aberration.BILE_DEVIATION) * rl);
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		// Only render the Aberration until it dies.
		if(!dead()) animation.render(g, position, theta);
		// Even if Aberration is dead, render its particles until they all die.
		if(!bile.isEmpty()) bile.stream().filter(p -> p.isAlive(cTime)).forEach(p -> p.render(g, cTime));
		
		if(Globals.SHOW_COLLIDERS) {
			g.setColor(Color.red);
			g.draw(bounds);
		}
	}

	@Override
	public boolean isAlive(long cTime) {
		return (!dead() || !bile.isEmpty());
	}
	
	private boolean dead() {
		return (health <= 0);
	}

	@Override
	public void move(int delta) {
		theta = Calculate.Hypotenuse(position, Globals.player.getPosition());
		
		if(!moveBlocked) {
			position.x += (float)Math.cos(theta) * Aberration.SPEED * delta;
			position.y += (float)Math.sin(theta) * Aberration.SPEED * delta;
		}
		
		moveBlocked = false;
		
		bounds.setCenterX(position.x);
		bounds.setCenterY(position.y);
	}

	@Override
	public void takeDamage(double amnt) {
		health -= amnt;
	}

	@Override
	public double getDamage() {
		return Aberration.DPS;
	}

}