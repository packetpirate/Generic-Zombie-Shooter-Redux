package com.grave.objects.items;

import com.grave.AssetManager;
import com.grave.entities.Player;
import com.grave.gfx.ui.StatusMessages;
import com.grave.misc.Pair;
import com.grave.status.ExpMultiplierEffect;
import com.grave.status.Status;

public class ExpMultiplierItem extends Item {
	private static final long DURATION = 10_000L;
	private static final long EFFECT_DURATION = 10_000L;
	
	public ExpMultiplierItem(Pair<Float> pos, long cTime) {
		super(pos, cTime);
		
		iconName = Status.EXP_MULTIPLIER.getIconName();
		duration = ExpMultiplierItem.DURATION;
		pickup = AssetManager.getManager().getSound("powerup2");
	}
	
	@Override
	public void apply(Player player, long cTime) {
		ExpMultiplierEffect effect = new ExpMultiplierEffect(ExpMultiplierItem.EFFECT_DURATION, cTime);
		player.getAttributes().set("expMult", 2.0);
		player.getStatusHandler().addStatus(effect, cTime);
		duration = 0L;
		pickup.play(1.0f, AssetManager.getManager().getSoundVolume());
		
		String message = "2x Experience!";
		StatusMessages.getInstance().addMessage(message, player, Player.ABOVE_1, cTime, 2_000L);
	}
	
	@Override
	public int getCost() {
		return 0;
	}
	
	@Override
	public String getName() {
		return "Experience Multiplier";
	}

	@Override
	public String getDescription() {
		return "Gives double experience for a short time.";
	}
}
