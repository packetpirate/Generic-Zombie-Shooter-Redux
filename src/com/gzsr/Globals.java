package com.gzsr;

import java.util.Random;

public class Globals {
	// Contains global constants.
	public static final String VERSION = "1.1.0";
	
	public static final long SLEEP_MS = 20L;
	public static final long NANO_TO_MS = 1_000_000L;
	
	public static final int TARGET_FPS = 60;
	public static final int STEP_TIME = 1000 / TARGET_FPS;
	public static final int MAX_STEPS = 4;
	
	public static final boolean SHOW_COLLIDERS = false; // Disabled in player releases.
	public static final boolean ENABLE_CONSOLE = true; // TODO: Disable for player releases.
	
	public static int WIDTH = 1024;
	public static int HEIGHT = 768;
	
	public static Random rand = new Random();
	
	public static boolean gameOver = false;
	
	private static int entityNum = 0;
	public static int generateEntityID() { return entityNum++; }
	public static void resetEntityNum() { entityNum = 0; }
}
