package com.gzsr.achievements.milestone;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gzsr.achievements.Achievement;
import com.gzsr.controllers.AchievementController;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class MilestoneAchievement extends Achievement {
	private Map<Long, Pair<Integer>> milestones;
	
	public MilestoneAchievement(String name_, String description_, String icon_) {
		super(name_, description_, icon_);
		
		milestones = new HashMap<Long, Pair<Integer>>();
	}
	
	@Override
	public void update(AchievementController controller, GameState gs, long cTime) {
		List<Long> metrics = controller.getMetrics();
		
		if(!metrics.isEmpty()) {
			for(long metric : metrics) {
				if(milestones.containsKey(metric)) {
					milestones.get(metric).x += 1;
				}
			}
		}
		
		if(isEarned()) onComplete(controller, cTime);
	}
	
	public MilestoneAchievement addMilestone(long metric, int target) {
		milestones.put(metric, new Pair<Integer>(0, target));
		return this;
	}
	
	@Override
	public boolean isEarned() {
		if(complete) return true;
		if(milestones.isEmpty()) return false;
		
		boolean earned = true;
		
		for(Pair<Integer> pair : milestones.values()) {
			earned = (earned && (pair.x >= pair.y));
		}
		
		return earned;
	}
}
