package com.gzsr.states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.FontUtils;

import com.gzsr.AchievementManager;
import com.gzsr.AssetManager;
import com.gzsr.Controls;
import com.gzsr.Controls.Layout;
import com.gzsr.Globals;
import com.gzsr.MusicPlayer;
import com.gzsr.controllers.AchievementController;
import com.gzsr.controllers.Scorekeeper;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.EnemyController;
import com.gzsr.gfx.Camera;
import com.gzsr.gfx.ui.StatusMessages;
import com.gzsr.gfx.ui.hud.Console;
import com.gzsr.gfx.ui.hud.EscapeMenu;
import com.gzsr.gfx.ui.hud.HUD;
import com.gzsr.objects.crafting.RecipeController;
import com.gzsr.status.Status;
import com.gzsr.status.StatusEffect;
import com.gzsr.tmx.TMap;
import com.gzsr.tmx.TParser;
import com.gzsr.world.Level;

public class GameState extends BasicGameState implements InputListener {
	public static final int ID = 1;

	private static final Color PAUSE_OVERLAY = new Color(0x331F006F);
	private static final long SAVE_DELAY = 30_000L;

	private AssetManager assets;
	private long time, accu, consoleTimer;
	public long getTime() { return time; }

	private Console console;
	private HUD hud;
	public HUD getHUD() { return hud; }

	private boolean gameStarted, paused, consoleOpen;
	public boolean isConsoleOpen() { return consoleOpen; }

	private long lastSave;

	private EscapeMenu escapeMenu;

	private Level level;
	public Level getLevel() { return level; }

	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		assets = AssetManager.getManager();

		gc.setMouseCursor(assets.getImage("GZS_Crosshair2").getScaledCopy(0.5f), 16, 16);

		escapeMenu = new EscapeMenu();

		// TODO: Remove this initial map creation once a proper level loader is created.
		TMap map = TParser.load("res/maps/GWB.tmx");
		map.constructMap();
		level = new Level(map);

		reset(gc);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		if(gc.hasFocus()) {
			accu = Math.min((accu + delta), (Globals.STEP_TIME * Globals.MAX_STEPS));

			while(accu >= Globals.STEP_TIME) {
				if(escapeMenu.isOpen()) {
					escapeMenu.update(gc, game, this);
				} else if(!paused && !consoleOpen) {
					time += Globals.STEP_TIME; // Don't want to update time while paused; otherwise, game objects and events could despawn / occur while paused.

					Player player = Player.getPlayer();

					level.update(this, time, delta);

					Controls controls = Controls.getInstance();

					if(!player.isAlive() && (player.getAttributes().getInt("lives") <= 0)) {
						// If the player has died, transition state.
						controls.resetAll();

						AchievementManager.save();
						Globals.gameOver = true;
						Globals.inGame = false;
						game.enterState(GameOverState.ID,
										new FadeOutTransition(Color.black, 250),
										new FadeInTransition(Color.black, 100));
					}

					if(player.isAlive()) {
						if(controls.isPressed(Controls.Layout.TALENTS_SCREEN)) {
							// Open the training screen.
							controls.resetAll();
							game.enterState(TalentsState.ID,
											new FadeOutTransition(Color.black, 250),
											new FadeInTransition(Color.black, 100));
						} else if(controls.isPressed(Controls.Layout.SHOP_SCREEN)) {
							// Open the weapon shopping screen.
							controls.resetAll();
							game.enterState(ShopState.ID,
											new FadeOutTransition(Color.black, 250),
											new FadeInTransition(Color.black, 100));
						} else if(controls.isPressed(Controls.Layout.CRAFT_SCREEN)) {
							// Open the crafting window.
							controls.resetAll();
							game.enterState(CraftingState.ID,
											new FadeOutTransition(Color.black, 250),
											new FadeInTransition(Color.black, 100));
						}
					}

					StatusMessages.getInstance().update(this, time, delta);

					Camera.getCamera().update(time);
					MusicPlayer.getInstance().update(false);
					hud.update(player, time);

					AchievementController.getInstance().update(this, time, delta);

					long sinceLastSave = (time - lastSave);
					if(sinceLastSave >= SAVE_DELAY) {
						AchievementManager.save();
						lastSave = time;
					}
				} else if(consoleOpen) {
					consoleTimer += delta;
					console.update(this, consoleTimer, Globals.STEP_TIME);
				}

				Controls.Layout.clearReleased();
				accu -= Globals.STEP_TIME;
			}
		}
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		g.clear();

		Camera camera = Camera.getCamera();
		camera.translate(g);

		level.render(g, time);

		StatusMessages.getInstance().render(g, time);

		hud.render(g, this, time);

		StatusEffect flash = Player.getPlayer().getStatusHandler().getStatus(Status.FLASHBANG);
		if(flash != null) {
			// If the player has been blinded, draw a white flash over everything.
			float p = flash.getPercentageTimeLeft(time);
			Color color = new Color(1.0f, 1.0f, 1.0f, p);
			g.setColor(color);
			g.fillRect((camera.getOffset().x - Camera.MAX_OFFSET), (camera.getOffset().y - Camera.MAX_OFFSET),
					   (Globals.WIDTH + (Camera.MAX_OFFSET * 2)), (Globals.HEIGHT + (Camera.MAX_OFFSET * 2)));
		} else if(Camera.getCamera().displayVignette()) {
			g.setColor(Camera.VIGNETTE_COLOR);
			g.fillRect((camera.getOffset().x - Camera.MAX_OFFSET), (camera.getOffset().y - Camera.MAX_OFFSET),
					   (Globals.WIDTH + (Camera.MAX_OFFSET * 2)), (Globals.HEIGHT + (Camera.MAX_OFFSET * 2)));
		}

		if(paused) {
			g.setColor(PAUSE_OVERLAY);
			g.fillRect(camera.getOffset().x, camera.getOffset().y, Globals.WIDTH, Globals.HEIGHT);
			g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular"));
			g.setColor(Color.white);
			int w = g.getFont().getWidth("Paused");
			int h = g.getFont().getLineHeight();
			FontUtils.drawCenter(g.getFont(), "Paused",
								 (int)(camera.getOffset().x + (Globals.WIDTH / 2) - (w / 2)),
								 (int)(camera.getOffset().y + (Globals.HEIGHT / 2) - (h / 2)), w);
		}

		if(escapeMenu.isOpen()) {
			escapeMenu.render(g, time);
		} else if(consoleOpen) console.render(g, time);

		g.resetTransform();
	}

	public void reset(GameContainer gc) throws SlickException{
		time = 0L;
		accu = 0L;
		consoleTimer = 0L;

		Controls.getInstance().resetAll();

		Player player = Player.getPlayer();

		EnemyController.reset();
		EnemyController ec = EnemyController.getInstance();

		player.reset();

		level.reset();
		level.addEntity("player", player);
		level.addEntity("enemyController", ec);

		StatusMessages.getInstance().clear();

		gameStarted = false;
		paused = false;
		consoleOpen = false;

		lastSave = 0L;

		Camera.getCamera().reset();
		console = new Console(this, gc);
		escapeMenu.reset();

		hud = new HUD();

		ShopState.resetShop();
		RecipeController.resetRecipes();
		Scorekeeper.getInstance().reset();
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		Controls.getInstance().getMouse().setPosition(newx, newy);
	}

	@Override
	public void mousePressed(int button, int x, int y) {
		if(consoleOpen) console.mousePressed(this, button, x, y);
		if(button == Input.MOUSE_LEFT_BUTTON) Controls.getInstance().getMouse().setLeftDown(true);
		else if(button == Input.MOUSE_RIGHT_BUTTON) Controls.getInstance().getMouse().setRightDown(true);
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		if(button == Input.MOUSE_LEFT_BUTTON) Controls.getInstance().getMouse().setLeftDown(false);
		else if(button == Input.MOUSE_RIGHT_BUTTON) Controls.getInstance().getMouse().setRightDown(false);
	}

	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		Controls.getInstance().getMouse().setPosition(newx, newy);
	}

	@Override
	public void keyPressed(int key, char c) {
		if(consoleOpen) {
			console.keyPressed(key, c);
		} else {
			Controls.getInstance().press(key);
		}
	}

	@Override
	public void keyReleased(int key, char c) {
		EnemyController ec = EnemyController.getInstance();

		if(key == Input.KEY_ESCAPE) {
			if(!escapeMenu.isOpen()) escapeMenu.openMenu();
			else escapeMenu.escape();
		} else {
			Layout keyID = Controls.Layout.identify(key);
			if((keyID == Controls.Layout.OPEN_CONSOLE) && Globals.ENABLE_CONSOLE) {
				console.setPauseTime(time);
				consoleOpen = !consoleOpen;
			} else {
				if(consoleOpen) console.keyReleased(key, c);
				else {
					if(keyID == Controls.Layout.PAUSE_GAME) {
						if(!paused) MusicPlayer.getInstance().pause();
						else MusicPlayer.getInstance().resume();
						paused = !paused;
					} else if((keyID == Controls.Layout.NEXT_WAVE) && !paused && ec.isRestarting()) {
						ec.skipToNextWave();
					} else {
						Controls.getInstance().release(key);
					}
				}
			}
		}
	}

	@Override
	public void mouseWheelMoved(int change) {
		Player player = Player.getPlayer();
		if(player.getRangedWeapons().size() > 1) {
			player.weaponRotate((change > 0) ? 1 : -1);
			hud.getWeaponDisplay().queueWeaponCycle();
		}
	}

	@Override
	public void enter(GameContainer gc, StateBasedGame game) throws SlickException {
		Controls.getInstance().resetAll();

		if(!gameStarted) {
			gameStarted = true;
		}

		if(Globals.gameOver) {
			reset(gc);
			Globals.gameOver = false;
		}
	}

	@Override
	public int getID() {
		return GameState.ID;
	}
}
