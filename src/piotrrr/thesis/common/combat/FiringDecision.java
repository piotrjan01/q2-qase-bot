package piotrrr.thesis.common.combat;

import piotrrr.thesis.bots.simplebot.EnemyInfo;

public class FiringDecision {
	
	public EnemyInfo enemyInfo;
	
	public int gunIndex;
	
	public FiringDecision(EnemyInfo enemy, int gunIndex) {
		this.enemyInfo = enemy;
		this.gunIndex = gunIndex;
	}
	
}
