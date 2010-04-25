package piotrrr.thesis.common.combat;

import soc.qase.state.Entity;

public class FiringDecision {
	
	public Entity enemy;
	
	public int gunIndex;
	
	public FiringDecision(Entity enemy, int gunIndex) {
		this.enemy = enemy;
		this.gunIndex = gunIndex;
	}
	
}
