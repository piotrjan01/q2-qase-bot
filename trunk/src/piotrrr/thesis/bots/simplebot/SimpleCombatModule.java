package piotrrr.thesis.bots.simplebot;


import piotrrr.thesis.bots.tuning.CombatConfig;
import piotrrr.thesis.common.CommFun;
import piotrrr.thesis.common.combat.FiringDecision;
import soc.qase.tools.vecmath.Vector3f;

public class SimpleCombatModule {
	
	static FiringDecision getFiringDecision(SimpleBot bot) {
		Vector3f playerPos = bot.getBotPosition();
		EnemyInfo chosen = null;
		float chosenRisk = Float.MAX_VALUE;
		for (EnemyInfo ei : bot.kb.enemyInformation.values()) {
			
			if ( ei.getBestVisibleEnemyPart(bot) == null ) {
				bot.dtalk.addToLog("no good visible part");
				continue;
			}
					
			float risk = CommFun.getDistanceBetweenPositions(playerPos, ei.getPosition());
			if (risk < chosenRisk) {
				chosen = ei;
				chosenRisk = risk;
			}
		}
		if (chosen == null)	return null;
		float distance = CommFun.getDistanceBetweenPositions(playerPos, chosen.getPosition());
		return new FiringDecision(chosen, chooseWeapon(bot, distance));
	}
	
	
	
	/**
	 * Chooses weapons considering the distance to the enemy.
	 */
	static int chooseWeapon(SimpleBot bot, float distance) {
		/**
		int BLASTER = 7, SHOTGUN = 8,
		SUPER_SHOTGUN = 9, MACHINEGUN = 10, CHAINGUN = 11, GRENADES = 12,
		GRENADE_LAUNCHER = 13, ROCKET_LAUNCHER = 14, HYPERBLASTER = 15,
		RAILGUN = 16, BFG10K = 17, SHELLS = 18, BULLETS = 19, CELLS = 20,
		ROCKETS = 21, SLUGS = 22;
		**/
		
		if (bot.forcedweapon != 0) return bot.forcedweapon;
		
		int maxWeight = -1;
		int gunInd = 7;
		for (int i=7; i<18; i++) {
			if ( ! bot.botHasItem(i) || ! bot.botHasItem(CombatConfig.ammoTable[i])) continue;
			if (distance < bot.cConfig.MAX_SHORT_DISTANCE_4_WP_CHOICE.value() && CombatConfig.isBannedForShortDistance(i)) continue;
			if (distance > bot.cConfig.MIN_LONG_DISTANCE.value() && CombatConfig.isBannedForLongDistance(i)) continue;
			int weight = bot.cConfig.getWeaponWeightByInvIndex(i);
			if (weight > maxWeight) {
				maxWeight = weight;
				gunInd = i;
			}
		}
		
		return gunInd;
		
	}

}
