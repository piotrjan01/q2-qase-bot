package piotrrr.thesis.bots.simplebot;

import java.util.Vector;

import piotrrr.thesis.common.CommFun;
import piotrrr.thesis.common.combat.FiringDecision;
import soc.qase.state.Entity;
import soc.qase.tools.vecmath.Vector3f;

public class SimpleCombatModule {
	
	@SuppressWarnings("unchecked")
	static FiringDecision getFiringDecision(SimpleBot bot) {
		Vector3f playerPos = bot.getBotPosition();
		Vector<Entity> enems = bot.getWorld().getOpponents(true);
		Entity chosen = null;
		for (Entity e : enems) {
			if ( ! bot.getBsp().isVisible(playerPos, e.getOrigin().toVector3f())) continue;
			if (chosen != null && 
				CommFun.getDistanceBetweenPositions(playerPos, chosen.getOrigin().toVector3f()) <= 
				CommFun.getDistanceBetweenPositions(playerPos, e.getOrigin().toVector3f())) continue;
			chosen = e;
		}
		if (chosen == null) return null;
		float distance = CommFun.getDistanceBetweenPositions(playerPos, chosen.getOrigin().toVector3f());
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
		float shortDistanceLimit = 100f; 
		
		int [] ammoTable = { 0, 0, 0, 0, 0, 0, 0,
				7, //blaster - ammo for blaster is just blaster himself
				18, //shotgun
				18, //ss
				19, //mgun
				19, //chgun
				12, // granades - ammo for granates are granates themselves
				12, //g launcher
				21, //r launcher
				20, //hyperblaster - energy cells
				22, //railgun - slugs
				20 //bfgk - energy cells
		};
		
		int [] shortDistOrder = { 16, 15, 9, 11, 10, 8, 7 };
		int [] longDistOrder = { 16, 15, 11, 14, 10, 9, 8, 7 };
		
		int [] order = null;
		
		if (distance > shortDistanceLimit) order = longDistOrder;
		else order = shortDistOrder;
		
		for (int i : order) {
			if (bot.botHasItem(i) && bot.botHasItem(ammoTable[i]) ) {
				return i;
			}
		}
		return 7;
		
	}

}
