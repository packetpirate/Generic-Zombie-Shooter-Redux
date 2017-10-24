package com.gzsr.status;

import com.gzsr.entities.Player;

public class SpeedEffect extends StatusEffect {
	public static final double EFFECT = 2.0;

	public SpeedEffect(Status status_, long duration_, long created_) {
		super(status_, duration_, created_);
	}

	@Override
	public void update(Player player, long cTime) {
		// No need for update logic here.
	}

	@Override
	public void onDestroy(Player player, long cTime) {
		player.setAttribute("spdMult", 1.0);
	}
}