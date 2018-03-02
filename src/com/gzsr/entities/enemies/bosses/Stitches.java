package com.gzsr.entities.enemies.bosses;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.enemies.EnemyType;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class Stitches extends Boss {
	private static final float HEALTH = 15_000.0f;
	private static final float SPEED = 0.10f;
	private static final float DPS = 20.0f;
	private static final float ATTACK_DIST = 300.0f;
	private static final float RELEASE_DIST = 100.0f;
	private static final float HOOK_SPEED = 0.4f;
	private static final long HOOK_COOLDOWN = 5_000L;
	
	private Particle hook;
	private boolean hooked;
	private long lastHook;
	
	public Stitches(Pair<Float> position_) {
		super(EnemyType.STITCHES, position_);
		this.health = Stitches.HEALTH;
		
		hook = null;
		hooked = false;
		lastHook = -Stitches.HOOK_COOLDOWN;
	}
	
	@Override
	public void update(GameState gs, long cTime, int delta) {
		if(isAlive(cTime)) {
			theta = Calculate.Hypotenuse(position, Globals.player.getPosition());
			
			if((hook != null) && !hooked) {
				float distToPlayer = Calculate.Distance(hook.getPosition(), Globals.player.getPosition());
				if(distToPlayer > Stitches.ATTACK_DIST) {
					// If the hook missed the player, get rid of it.
					hook = null;
					hooked = false;
					lastHook = cTime;
				} else {
					// Move the hook toward the player until it collides or misses.
					hook.update(gs, cTime, delta);
					if(hook.checkCollision(Globals.player)) hooked = true;
				}
			}
			
			long sinceLastHook = (cTime - lastHook);
			if((hook == null) && (sinceLastHook >= Stitches.HOOK_COOLDOWN) && nearPlayer(Stitches.ATTACK_DIST)) {
				// Throw the hook.
				Pair<Float> hookPos = new Pair<Float>(position.x, position.y);
				float direction = Calculate.Hypotenuse(position, Globals.player.getPosition()) + (float)(Math.PI / 2);
				hook = new Particle("GZS_Hook", Color.gray, hookPos, Stitches.HOOK_SPEED, direction, 0.0f, new Pair<Float>(16.0f, 16.0f), -1L, cTime);
				AssetManager.getManager().getSound("throw2").play();
			} else if(hooked) {
				// If we're close enough to the player now, release the hook.
				if(nearPlayer(Stitches.RELEASE_DIST)) {
					hook = null;
					hooked = false;
					lastHook = cTime;
				} else {
					// Otherwise, reel the player in.
					float xOff = (float)(Math.cos(theta) * -(Stitches.HOOK_SPEED * 2));
					float yOff = (float)(Math.sin(theta) * -(Stitches.HOOK_SPEED * 2));
					Globals.player.move(xOff, yOff);
					hook.setPosition(new Pair<Float>(Globals.player.getPosition().x, Globals.player.getPosition().y));
				}
			} else if(!hooked && (hook == null)) {
				animation.update(cTime);
				move(delta);
			}
		} else {
			hook = null;
			hooked = false;
		}
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		if(hook != null) {
			// Draw the hook's tether.
			g.setColor(Color.black);
			g.setLineWidth(2.0f);
			g.drawLine(position.x, position.y, hook.getPosition().x, hook.getPosition().y);
			g.setLineWidth(1.0f);
			
			hook.render(g, cTime);
		}
		
		super.render(g, cTime);
	}

	@Override
	public boolean isAlive(long cTime) {
		return (health > 0);
	}

	@Override
	public void move(int delta) {
		if(!moveBlocked) {
			position.x += (float)Math.cos(theta) * Stitches.SPEED * delta;
			position.y += (float)Math.sin(theta) * Stitches.SPEED * delta;
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
		return Stitches.DPS;
	}

}