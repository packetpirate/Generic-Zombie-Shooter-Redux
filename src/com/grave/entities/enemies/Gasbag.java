package com.grave.entities.enemies;

import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;

import com.grave.AssetManager;
import com.grave.entities.Player;
import com.grave.math.Calculate;
import com.grave.math.Dice;
import com.grave.misc.Pair;
import com.grave.objects.crafting.Resources;
import com.grave.objects.items.Powerups;
import com.grave.objects.weapons.DamageType;
import com.grave.objects.weapons.Explosion;
import com.grave.states.GameState;
import com.grave.status.PoisonEffect;
import com.grave.status.Status;

public class Gasbag extends Enemy {
	public static final int FIRST_WAVE = 8;
	private static final int SPAWN_COST = 5;
	private static final float SPEED = 0.15f;
	private static final float ATTACK_DIST = 100.0f;
	private static final float EXPLODE_RADIUS = 128.0f;
	private static final long POISON_DURATION = 5000L;
	private static final float POISON_KNOCKBACK = 5.0f;

	private static final Dice HEALTH = new Dice(3, 8);
	private static final int HEALTH_MOD = 8;

	public static final ResourceTable RESOURCES = new ResourceTable()
			.addResource(Resources.METAL, 0.025f)
			.addResource(Resources.CLOTH, 0.1f)
			.addResource(Resources.GLASS, 0.025f)
			.addResource(Resources.POWER, 0.05f);

	public static final LootTable LOOT = new LootTable()
			.addItem(Powerups.Type.HEALTH, 0.20f)
			.addItem(Powerups.Type.AMMO, 0.40f)
			.addItem(Powerups.Type.EXTRA_LIFE, 0.025f)
			.addItem(Powerups.Type.CRIT_CHANCE, 0.05f)
			.addItem(Powerups.Type.EXP_MULTIPLIER, 0.05f)
			.addItem(Powerups.Type.NIGHT_VISION, 0.05f)
			.addItem(Powerups.Type.UNLIMITED_AMMO, 0.025f);

	private Sound explode;
	private boolean exploded;

	public Gasbag(Pair<Float> position_) {
		super(EnemyType.GASBAG, position_);
		this.health = Gasbag.HEALTH.roll(Gasbag.HEALTH_MOD);
		this.speed = Gasbag.SPEED;
		this.explode = AssetManager.getManager().getSound("poison_cloud");
		this.exploded = false;

		this.damageImmunities.add(DamageType.CORROSIVE);
		this.statusHandler.addImmunity(Status.POISON);
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(isAlive(cTime)) {
			Player player = Player.getPlayer();
			// Need to make sure to update the status effects first.
			statusHandler.update((GameState)gs, cTime, delta);

			updateFlash(cTime);
			theta = Calculate.Hypotenuse(position, player.getPosition());
			if(!nearPlayer(Gasbag.ATTACK_DIST)) {
				animation.getCurrentAnimation().update(cTime);
				if(player.isAlive() && !touchingPlayer()) move((GameState)gs, delta);
			} else explode((GameState)gs, cTime);
		}

		postDamageTexts();
	}

	private void explode(GameState gs, long cTime) {
		PoisonEffect pe = new PoisonEffect(Gasbag.POISON_DURATION, cTime);
		Explosion poison = new Explosion(Explosion.Type.POISON, "GZS_PoisonExplosion",
										 new Pair<Float>(position.x, position.y), pe,
										 0.0, false, Gasbag.POISON_KNOCKBACK,
										 Gasbag.EXPLODE_RADIUS, cTime);
		gs.getLevel().addEntity(poison.getTag(), poison);

		explode.play(1.0f, AssetManager.getManager().getSoundVolume());
		health = 0.0;
		exploded = true;
	}

	@Override
	public boolean isAlive(long cTime) {
		return (!exploded && !dead());
	}

	@Override
	public void move(GameState gs, int delta) {
		if(!statusHandler.hasStatus(Status.PARALYSIS)) {
			velocity.x = (float)Math.cos(theta) * Gasbag.SPEED * delta;
			velocity.y = (float)Math.sin(theta) * Gasbag.SPEED * delta;

			avoidObstacles(gs, delta);

			if(!moveBlocked) {
				position.x += velocity.x;
				position.y += velocity.y;
			}

			moveBlocked = false;

			bounds.setCenterX(position.x);
			bounds.setCenterY(position.y);
		}
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
	public void resetSpeed() { speed = Gasbag.SPEED; }

	@Override
	public long getAttackDelay() { return 0L; }

	public static int appearsOnWave() { return Gasbag.FIRST_WAVE; }

	public static int getSpawnCost() { return Gasbag.SPAWN_COST; }

	@Override
	public String getName() {
		return "Gasbag";
	}

	@Override
	public String getDescription() {
		return "Gasbag";
	}

	@Override
	public String print() {
		return String.format("%s at (%.2f, %.2f) - %.2f health - Exploded? %s",
							 getName(), position.x, position.y, health, (exploded ? "Yes" : "No"));
	}

	@Override
	public ResourceTable getResourceTable() { return Gasbag.RESOURCES; }

	@Override
	public LootTable getLootTable() { return Gasbag.LOOT; }
}
