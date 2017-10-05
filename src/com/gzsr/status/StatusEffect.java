package com.gzsr.status;

import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;

public abstract class StatusEffect {
	protected Status status;
	public Status getStatus() { return status; }
	public Image getIcon() { 
		return AssetManager.getManager()
						   .getImage(status.getIconName());
	}
	
	protected long duration;
	protected long created;
	public boolean isActive(long cTime) {
		long elapsed = cTime - created;
		return (elapsed <= duration);
	}
	public void refresh(long cTime) {
		this.created = cTime;
	}
	public float getPercentageTimeLeft(long cTime) {
		long elapsed = cTime - created;
		return (1.0f - ((float)elapsed / (float)duration));
	}
	
	public StatusEffect(Status status_, long duration_, long created_) {
		this.status = status_;
		this.duration = duration_;
		this.created = created_;
	}
	
	public abstract void update(Player player, long cTime);
	public abstract void onDestroy(Player player, long cTime);
}
