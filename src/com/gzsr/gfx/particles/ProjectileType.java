package com.gzsr.gfx.particles;

import org.newdawn.slick.Color;

public enum ProjectileType {
	// Player projectiles.
	NAIL(new Color(0xB6B6B6), 2.0f, 8.0f, 16.0f, 1_000L),
	TASER(new Color(0xE6FF4B), 2.0f, 4.0f, 6.0f, 1_000L),
	HANDGUN(new Color(0xF2F28C), 2.0f, 3.0f, 10.0f, 1_500L),
	SMG(new Color(0xF2F28C), 2.5f, 3.0f, 10.0f, 1_500L),
	ASSAULT(new Color(0xF2F2BF), 2.5f, 3.0f, 12.0f, 2_500L),
	SHOTGUN(new Color(0xF2F28C), 1.0f, 4.0f, 4.0f, 800L),
	RIFLE(new Color(0xF2F2BC), 4.0f, 3.0f, 12.0f, 3_000L),
	ARROW(new Color(0xA58138), 3.0f, 30.0f, 4.0f, 2_000L),
	BOLT(new Color(0xA58138), 3.5f, 30.0f, 4.0f, 2_500L),
	FLAMETHROWER(new Color(0xEDA642), 0.25f, 4.0f, 4.0f, 1_200L),
	GRENADE(new Color(0x4DAD1A), 0.4f, 16.0f, 16.0f, 1_000L),
	MISSILE(new Color(0x4DAD1A), 2.0f, 64.0f, 16.0f, 4_000L),
	MOLOTOV(new Color(0x8F563B), 0.25f, 16.0f, 16.0f, 1_000L), // Lifespan set manually according to throw strength.
	CLAYMORE(new Color(0x4D661A), 0.0f, 8.0f, 4.0f, 0L),
	SHRAPNEL(new Color(0xF2F28C), 1.5f, 4.0f, 4.0f, 800L),
	LASERNODE(new Color(0x313C4F), 0.0f, 8.0f, 8.0f, -1L),
	TURRET(new Color(0x7A221A), 0.0f, 16.0f, 16.0f, 0L),
	
	// Enemy projectiles.
	BILE(new Color(0x8BCE5E), 0.25f, 3.0f, 3.0f, 750);
	
	private Color color;
	public Color getColor() { return color; }
	
	private float velocity;
	public float getVelocity() { return velocity; }
	
	private float width;
	public float getWidth() { return width; }
	
	private float height;
	public float getHeight() { return height; }
	
	private long lifespan;
	public long getLifespan() { return lifespan; }
	
	ProjectileType(Color color_, float velocity_, float width_, float height_, long lifespan_) {
		this.color = color_;
		this.velocity = velocity_;
		this.width = width_;
		this.height = height_;
		this.lifespan = lifespan_;
	}
}
