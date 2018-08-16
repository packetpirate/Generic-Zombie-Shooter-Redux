package com.gzsr.entities.enemies.bosses;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.controllers.AchievementController;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.EnemyType;
import com.gzsr.entities.enemies.LootTable;
import com.gzsr.math.Calculate;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.Powerups;
import com.gzsr.objects.weapons.DamageType;
import com.gzsr.states.GameState;
import com.gzsr.status.Status;

public class Zombat extends Boss {
	private static final int FIRST_WAVE = 20;
	private static final int SPAWN_COST = 12;
	private static final long ATTACK_DELAY = 1_000L;
	private static final float SPEED = 0.15f;
	private static final float ATTACK_DIST = 250.0f;
	
	private static final Dice HEALTH = new Dice(30, 10);
	private static final int HEALTH_MOD = 1_200;
	
	private static final Dice DAMAGE = new Dice(1, 4);
	private static final int DAMAGE_MOD = 2;
	
	private static final Color BLOOD_COLOR = new Color(0xAA0000);
	
	public static final LootTable LOOT = new LootTable()
			.addItem(Powerups.Type.HEALTH, 1.0f)
			.addItem(Powerups.Type.AMMO, 0.75f)
			.addItem(Powerups.Type.EXTRA_LIFE, 0.15f)
			.addItem(Powerups.Type.CRIT_CHANCE, 0.25f)
			.addItem(Powerups.Type.NIGHT_VISION, 0.25f);
	
	private boolean siphoningBlood;
	
	public Zombat(Pair<Float> position_) {
		super(EnemyType.ZOMBAT_SWARM, position_);
		
		this.health = Zombat.HEALTH.roll(Zombat.HEALTH_MOD);
		
		this.statusHandler.addImmunity(Status.PARALYSIS);
		this.statusHandler.addImmunity(Status.POISON);
		
		siphoningBlood = false;
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(isAlive(cTime)) {
			Player player = Player.getPlayer();
			
			// Need to make sure to update the status effects first.
			statusHandler.update((GameState)gs, cTime, delta);
			
			updateFlash(cTime);
			theta = Calculate.Hypotenuse(position, player.getPosition());
			
			animation.getCurrentAnimation().update(cTime);
			if(!nearPlayer(Zombat.ATTACK_DIST)) {
				siphoningBlood = false;
				if(player.isAlive() && !touchingPlayer()) move((GameState)gs, delta);
			} else siphoningBlood = player.isAlive(); // Only start siphoning if player is alive, obviously...
			
			long elapsed = (cTime - lastAttack);
			if(player.isAlive() && siphoningBlood && (elapsed >= Zombat.ATTACK_DELAY)) {
				double damageTaken = player.takeDamage(getDamage(), cTime);
				if(damageTaken > 0.0) health += damageTaken;
				
				lastAttack = cTime;
			}
		}
		
		postDamageTexts();
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		// Render the blood stream being siphoned from the player.
		if(siphoningBlood) {
			Player player = Player.getPlayer();
			
			float x = position.x + ((float)Math.cos(theta) * 5.0f);
			float y = position.y + ((float)Math.sin(theta) * 5.0f);
			
			g.setColor(BLOOD_COLOR);
			g.setLineWidth(3.0f);
			g.drawLine(x, y, player.getPosition().x, player.getPosition().y);
			g.setLineWidth(1.0f);
		}
		
		super.render(g, cTime);
	}
	
	@Override
	public void blockMovement() {
		// Do nothing... can't block a Zombat with laser barriers.
	}

	@Override
	public void move(GameState gs, int delta) {
		velocity.x = (float)Math.cos(theta) * Zombat.SPEED * delta;
		velocity.y = (float)Math.sin(theta) * Zombat.SPEED * delta;

		avoidObstacles(gs, delta);
		
		position.x += velocity.x;
		position.y += velocity.y;
		
		bounds.setCenterX(position.x);
		bounds.setCenterY(position.y);
	}
	
	@Override
	public float getCohesionDistance() {
		return (Math.min(type.getFrameWidth(), type.getFrameHeight()) * 2);
	}
	
	@Override
	public float getSeparationDistance() {
		return Math.min(type.getFrameWidth(), type.getFrameHeight());
	}
	
	@Override
	public void takeDamage(DamageType type, double amnt, float knockback, long cTime, int delta) {
		takeDamage(type, amnt, knockback, (float)(theta + Math.PI), cTime, delta, true);
	}
	
	@Override
	public void takeDamage(DamageType type, double amnt, float knockback, float knockbackTheta, long cTime, int delta, boolean flash) {
		takeDamage(type, amnt, knockback, knockbackTheta, cTime, delta, flash, false);
	}
	
	@Override
	public void takeDamage(DamageType type, double amnt, float knockback, float knockbackTheta, long cTime, int delta, boolean flash, boolean isCritical) {
		if(!dead() && !damageImmunities.contains(type)) {
			health -= amnt;
			
			createDamageText(amnt, 32.0f, knockbackTheta, cTime, isCritical);
			
			if(flash) {
				hit = true;
				hitTime = cTime;
			}
		}
	}
	
	@Override
	public void onDeath(GameState gs, long cTime) {
		AchievementController.getInstance().postMetric("zombatsKilled");
	}

	@Override
	public double getDamage() { return Zombat.DAMAGE.roll(Zombat.DAMAGE_MOD); }
	
	@Override
	public long getAttackDelay() { return Zombat.ATTACK_DELAY; }
	
	@Override
	public float getSpeed() {
		return Zombat.SPEED;
	}
	
	public static int appearsOnWave() {
		return FIRST_WAVE;
	}

	public static int getSpawnCost() {
		return Zombat.SPAWN_COST;
	}
	
	@Override
	public String getName() {
		return "Zombat";
	}
	
	@Override
	public String getDescription() {
		return "Zombat";
	}
	
	@Override
	public String print() {
		return String.format("%s at (%.2f, %.2f) - %.2f health",
							 getName(), position.x, position.y, health);
	}
	
	@Override
	public LootTable getLootTable() {
		return Zombat.LOOT;
	}
}