package com.gzsr.objects.items;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;
import com.gzsr.misc.Pair;

public class Armor extends Item {
	private static final String ICONNAME = "GZS_Armor";
	private static final long DURATION = 10_000L;
	private static final int COST = 1_000;
	
	private Type armorType;
	
	public enum Type {
		NORMAL(50.0), 
		REINFORCED(100.0);
		
		private double amnt;
		public double getArmorValue() { return amnt; }
		
		Type(double amnt_) {
			this.amnt = amnt_;
		}
	}
	
	public Armor(Type armorType_, Pair<Float> pos, long cTime) {
		super(pos, cTime);
		
		armorType = armorType_;
		
		iconName = Armor.ICONNAME;
		duration = Armor.DURATION;
		pickup = AssetManager.getManager().getSound("powerup2");
	}

	@Override
	public void apply(Player player, long cTime) {
		player.addArmor(armorType.getArmorValue());
		duration = 0L;
		pickup.play(1.0f, AssetManager.getManager().getSoundVolume());
	}

	@Override
	public int getCost() {
		return Armor.COST;
	}
	
	@Override
	public String getName() {
		return (((armorType == Type.REINFORCED) ? "Reinforced " : "") + "Leather Armor");
	}

	@Override
	public String getDescription() {
		return "Thick leather armor that will protect you from getting bitten, but not much else...";
	}
}
