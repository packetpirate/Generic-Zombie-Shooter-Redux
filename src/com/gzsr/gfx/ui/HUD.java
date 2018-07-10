package com.gzsr.gfx.ui;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.util.FontUtils;

import com.gzsr.AssetManager;
import com.gzsr.Controls;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.EnemyController;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.Weapon;
import com.gzsr.states.GameState;
import com.gzsr.status.Status;
import com.gzsr.status.StatusEffect;

public class HUD {
	private static final long WEAPONS_DISPLAY_TIME = 2000L;
	
	private static final Color POISON_HEALTH = new Color(0x009900);
	private static final Color ARMOR_BAR = new Color(0x4286F4);
	
	private static final Pair<Float> HEALTH_ORIGIN = new Pair<Float>(10.0f, 10.0f);
	private static final Pair<Float> WEAPONS_ORIGIN = new Pair<Float>(10.0f, (Globals.HEIGHT - 64.0f));
	private static final Pair<Float> EXP_ORIGIN = new Pair<Float>(10.0f, 41.0f);
	private static final Pair<Float> STATUS_ORIGIN = new Pair<Float>(10.0f, 62.0f);
	
	private long lastWeaponSwitch;
	private boolean cycleWeapons;
	public void queueWeaponCycle() { cycleWeapons = true; }
	
	public HUD() throws SlickException {
		lastWeaponSwitch = 0L;
		cycleWeapons = false;
	}
	
	private boolean displayWeapons(long cTime) {
		long elapsed = cTime - lastWeaponSwitch;
		return (elapsed <= WEAPONS_DISPLAY_TIME);
	}
	
	public void update(Player player, long cTime) {
		if(cycleWeapons) {
			cycleWeapons = false;
			lastWeaponSwitch = cTime;
		}
	}
	
	public void render(Graphics g, GameState gs, long cTime) {
		AssetManager assets = AssetManager.getManager();
		Player player = Player.getPlayer();
		{ // Render the health bar.
			double currentHealth = player.getAttributes().getDouble("health");
			double maxHealth = player.getAttributes().getDouble("maxHealth");
			double percentage = currentHealth / maxHealth;
			
			g.setColor(Color.black);
			g.fillRect(HEALTH_ORIGIN.x, HEALTH_ORIGIN.y, 156.0f, 26.0f);
			g.setColor(Color.lightGray);
			g.drawRect(HEALTH_ORIGIN.x, HEALTH_ORIGIN.y, 156.0f, 26.0f);
			
			g.setColor(Player.getPlayer().hasStatus(Status.POISON) ? HUD.POISON_HEALTH : Color.red);
			g.fillRect((HEALTH_ORIGIN.x + 3.0f), 
					   (HEALTH_ORIGIN.y + 3.0f), 
					   ((float)percentage * 150.0f), 20.0f);
			
			// Render the armor bar on top of the health bar.
			double currentArmor = player.getAttributes().getDouble("armor");
			double maxArmor = player.getAttributes().getDouble("maxArmor");
			double armorPercentage = currentArmor / maxArmor;
			
			g.setColor(ARMOR_BAR);
			g.fillRect((HEALTH_ORIGIN.x + 3.0f), 
					   (HEALTH_ORIGIN.y + 3.0f), 
					   ((float)armorPercentage * 150.0f), 20.0f);
			
			// Render the border around the health / armor bars.
			g.setColor(Color.lightGray);
			g.drawRect((HEALTH_ORIGIN.x + 3.0f), 
					   (HEALTH_ORIGIN.y + 3.0f), 
					   ((float)Math.max(percentage, armorPercentage) * 150.0f), 20.0f);
			
			UnicodeFont f = AssetManager.getManager().getFont("PressStart2P-Regular_small");
			String healthText = String.format("HP: %d / %d", (int)currentHealth, (int)maxHealth);
			FontUtils.drawCenter(f, healthText, (int)HEALTH_ORIGIN.x.floatValue(), (int)(HEALTH_ORIGIN.y.floatValue() + f.getLineHeight()), 156);
		} // End health bar rendering.
		
		{ // Draw the Player.getPlayer()'s lives next to the health bar.
			float startX = HEALTH_ORIGIN.x + 156.0f;
			float startY = HEALTH_ORIGIN.y + 5.0f;
			Image img = AssetManager.getManager().getImage("GZS_Life");
			for(int i = 0; i < Player.getPlayer().getAttributes().getInt("lives"); i++) {
				g.drawImage(img, (startX + (i * img.getWidth()) + (i * 3.0f) + 5.0f), startY);
			}
		} // End of drawing Player.getPlayer()'s lives.
		
		{ // Begin experience bar rendering.
			float currentExp = (float)Player.getPlayer().getAttributes().getInt("experience");
			float expToLevel = (float)Player.getPlayer().getAttributes().getInt("expToLevel");
			float percentage = currentExp / expToLevel;
			
			g.setColor(Color.black);
			g.fillRect(EXP_ORIGIN.x, EXP_ORIGIN.y, 156.0f, 16.0f);
			g.setColor(Color.lightGray);
			g.drawRect(EXP_ORIGIN.x, EXP_ORIGIN.y, 156.0f, 16.0f);
			
			if(percentage != 0.0f) {
				g.setColor(Color.green);
				g.fillRect((EXP_ORIGIN.x + 3.0f), (EXP_ORIGIN.y + 3.0f), 
						   (percentage * 150.0f), 10.0f);
				g.setColor(Color.lightGray);
				g.drawRect((EXP_ORIGIN.x + 3.0f), (EXP_ORIGIN.y + 3.0f), 
						   (percentage * 150.0f), 10.0f);
			}
		} // End experience bar rendering.
		
		if(displayWeapons(cTime)) {
			// Render the three weapons (or the Player.getPlayer()'s active weapons - 1) on top of the current weapon.
			List<Weapon> weapons = Player.getPlayer().getWeapons();
			int wi = Player.getPlayer().getWeaponIndex() - 1;
			float startY = WEAPONS_ORIGIN.y;
			for(int i = 0; i < Math.min(3, Player.getPlayer().getWeapons().size()); i++) {
				Weapon w = weapons.get(Math.floorMod((wi + (i + 1)), weapons.size()));
				float cy = (startY - (i * 54.0f));
				
				g.setColor(new Color(0.7f, 0.7f, 0.7f));
				g.fillRect(WEAPONS_ORIGIN.x, cy, 54.0f, 54.0f);
				g.setColor(Color.black);
				g.drawRect(WEAPONS_ORIGIN.x, cy, 54.0f, 54.0f);
				
				g.setColor(Color.gray);
				g.fillRect((WEAPONS_ORIGIN.x + 3.0f), (cy + 3.0f), 48.0f, 48.0f);
				g.setColor(Color.black);
				g.drawRect((WEAPONS_ORIGIN.x + 3.0f), (cy + 3.0f), 48.0f, 48.0f);
				
				g.drawImage(w.getInventoryIcon(), (WEAPONS_ORIGIN.x + 3.0f), (cy + 3.0f));
			}
		}
		
		{ // Render the weapons loadout.
			g.setColor(new Color(0.7f, 0.7f, 0.7f));
			g.fillRect(WEAPONS_ORIGIN.x, WEAPONS_ORIGIN.y, 150.0f, 54.0f);
			g.setColor(Color.black);
			g.drawRect(WEAPONS_ORIGIN.x, WEAPONS_ORIGIN.y, 150.0f, 54.0f);
			
			g.setColor(Color.gray);
			g.fillRect((WEAPONS_ORIGIN.x + 3.0f), (WEAPONS_ORIGIN.y + 3.0f), 48.0f, 48.0f);
			g.setColor(Color.black);
			g.drawRect((WEAPONS_ORIGIN.x + 3.0f), (WEAPONS_ORIGIN.y + 3.0f), 48.0f, 48.0f);
			
			if(!Player.getPlayer().getWeapons().isEmpty()) {
				g.drawImage(Player.getPlayer().getCurrentWeapon().getInventoryIcon(),
							(WEAPONS_ORIGIN.x + 3.0f), (WEAPONS_ORIGIN.y + 3.0f));
				
				// Render the reloading bar, if the Player.getPlayer() is reloading.
				if(Player.getPlayer().getCurrentWeapon().isReloading(cTime)) {
					float percentage = 1.0f - (float)Player.getPlayer().getCurrentWeapon().getReloadTime(cTime);
					float height = percentage * 48.0f;
					float y = (WEAPONS_ORIGIN.y + 3.0f + (48.0f - height));
					
					g.setColor(new Color(1.0f, 1.0f, 1.0f, 0.5f));
					g.fillRect((WEAPONS_ORIGIN.x + 3.0f), y, 48.0f, height);
				}
			}
			
			if(Player.getPlayer().hasStatus(Status.UNLIMITED_AMMO)) {
				Image unlimitedAmmo = assets.getImage("GZS_UnlimitedAmmo");
				float x = WEAPONS_ORIGIN.x + 100.0f - (unlimitedAmmo.getWidth() / 2);
				float y = WEAPONS_ORIGIN.y + 27.0f - (unlimitedAmmo.getHeight() / 2);
				g.drawImage(unlimitedAmmo, x, y);
			} else {
				UnicodeFont f = AssetManager.getManager().getFont("PressStart2P-Regular_small");
				Weapon w = Player.getPlayer().getCurrentWeapon();
				String ammoText = String.format("%d / %d",
						((w != null) ? w.getClipAmmo() : 0),
						((w != null) ? w.getInventoryAmmo() : 0));
				FontUtils.drawCenter(f, ammoText, (int)(WEAPONS_ORIGIN.x + 54.0f), (int)(WEAPONS_ORIGIN.y + ((54.0f - f.getLineHeight()) / 2)), 93, Color.black);
			}
		} // End weapons loadout rendering.
		
		{ // Begin Status Effects rendering.
			float xPlus = 0.0f;
			List<StatusEffect> statusEffects = Player.getPlayer().getStatuses();
			for(StatusEffect status : statusEffects) {
				// Render each individual status underneath the health and experience bars.
				Image img = status.getIcon();
				float percentageTimeLeft = status.getPercentageTimeLeft(cTime);
				g.drawImage(img, (STATUS_ORIGIN.x + xPlus), STATUS_ORIGIN.y, 
							new Color(1.0f, 1.0f, 1.0f, percentageTimeLeft));
				xPlus += img.getWidth() + 5.0f;
			}
		} // End Status Effects rendering.
		
		{ // Begin Wave Counter rendering.
			EnemyController ec = (EnemyController)gs.getEntity("enemyController");
			UnicodeFont f = AssetManager.getManager().getFont("PressStart2P-Regular");
			
			if(ec.isRestarting()) {
				// Render the countdown to the next wave.
				int time = ec.timeToNextWave(cTime);
				String text = String.format("Next Wave: %d", time);
				int w = f.getWidth(text);
				
				g.setColor(Color.white);
				FontUtils.drawCenter(f, text, (Globals.WIDTH - 20 - w), 20, w);
			} else {
				// Render the wave counter.
				String text = String.format("Wave: %d", ec.getWave());
				int w = f.getWidth(text);
				
				g.setColor(Color.white);
				FontUtils.drawCenter(f, text, (Globals.WIDTH - 20 - w), 20, w);
			}
		} // End Wave Counter rendering.
		
		{ // Begin Drawing Player Money
			g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular"));
			String money = String.format("$%s", NumberFormat.getInstance(Locale.US).format(Player.getPlayer().getAttributes().getInt("money")));
			float w = g.getFont().getWidth(money);
			float h = g.getFont().getLineHeight();
			float x = ((Globals.WIDTH / 2) - (w / 2));
			float y = (Globals.HEIGHT - h - 20.0f);
			FontUtils.drawCenter(g.getFont(), money, (int)x, (int)y, (int)w, Color.white);
		} // End Player Money Drawing
		
		{ // Draw Shop and Training Screen Icons
			g.setColor(Color.white);
			g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular"));
			
			// Draw the character portrait on the far right.
			float w1 = g.getFont().getWidth(Controls.Layout.TRAIN_SCREEN.getDisplay());
			float h = g.getFont().getLineHeight();
			g.drawString(Controls.Layout.TRAIN_SCREEN.getDisplay(), (Globals.WIDTH - w1 - 20.0f), (Globals.HEIGHT - h - 20.0f));
			
			Image character = AssetManager.getManager().getImage("GZS_Joe-Portrait");
			character.draw((Globals.WIDTH - (w1 + 20.0f) - ((character.getWidth() / 2) + 10.0f)), (Globals.HEIGHT - ((character.getHeight() / 2) + 20.0f)), 0.5f);
			
			// Draw the backpack to the left of that.
			float w2 = g.getFont().getWidth(Controls.Layout.SHOP_SCREEN.getDisplay());
			g.drawString(Controls.Layout.SHOP_SCREEN.getDisplay(), (Globals.WIDTH - (w1 + w2 + (character.getWidth() / 2) + 40.0f)), (Globals.HEIGHT - h - 20.0f));
			
			Image cash = AssetManager.getManager().getImage("GZS_Cash");
			cash.draw((Globals.WIDTH - (w1 + w2 + (character.getWidth() / 2) + 35.0f) - ((cash.getWidth() / 2) + 20.0f)), (Globals.HEIGHT - ((cash.getHeight() / 2) + 15.0f)), 0.5f);
		} // End drawing shop and training icons.
		
		// If Player.getPlayer() is respawning, draw the countdown.
		if(Player.getPlayer().isRespawning()) {
			g.setColor(Color.white);
			g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular"));
			long timeToRespawn = Player.getPlayer().getTimeToRespawn(cTime);
			String respawnText = "Respawn in " + (timeToRespawn / 1000L) + "...";
			float w = g.getFont().getWidth(respawnText);
			float h = g.getFont().getLineHeight();
			FontUtils.drawCenter(g.getFont(), respawnText, (int)((Globals.WIDTH / 2) - (w / 2)), (int)((Globals.HEIGHT / 2) - (h / 2)), (int)w);
		}
	}
}
