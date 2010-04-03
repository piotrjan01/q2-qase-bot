package piotrrr.bot.misc.fsm.needs;

/**
 * FIXME: u¿ycie par, rozdzielenie na encje po¿¹dane i unikane
 */

import java.util.LinkedList;

import piotrrr.bot.base.BotBase;
import piotrrr.bot.misc.EntityWrapper;

public class NeedsFSM {
	
	static final float GOOD_WELLNESS = 60f;
	static final float BAD_WELLNESS = 40f;
	static final float HEALTH_WEIGHT = 0.6f;
	static final float ARMOR_WEIGHT = 0.4f;
	static final float GOOD_FIRE_POWER = 0.0035f;
	static final float BAD_FIRE_POWER = GOOD_FIRE_POWER*0.8f;
	
	BotBase bot;
	
	NeedsState state;
	
	public NeedsFSM(BotBase bot) {
		this.bot = bot;
		this.state = new HealingState(bot);
	}
	
	public EntityWrapper [] getDesiredEntities() {
		state = state.getNextState();
		return state.getDesiredEntities();
	}
	
	public String getCurrentStateName() {
		return state.getClass().toString();
	}
	
	
	static float getBotWellness(BotBase bot) {
		return HEALTH_WEIGHT*bot.getBotHealth()+ARMOR_WEIGHT*bot.getBotArmor();
	}
	
	/**
	 * Returns the bot's firepower according to the scores table of each weapon.
	 * @return the number between 0 and 1.
	 */
	static float getBotFirePower(BotBase bot) {
		/**
		int BLASTER = 7, SHOTGUN = 8,
		SUPER_SHOTGUN = 9, MACHINEGUN = 10, CHAINGUN = 11, GRENADES = 12,
		GRENADE_LAUNCHER = 13, ROCKET_LAUNCHER = 14, HYPERBLASTER = 15,
		RAILGUN = 16, BFG10K = 17, SHELLS = 18, BULLETS = 19, CELLS = 20,
		ROCKETS = 21, SLUGS = 22;
		**/
		int [] importanceTable = { 0, 0, 0, 0, 0, 0, 0,
				0, //blaster - isn't counted
				10, //shotgun - good for short and medium dist.
				15, //ss - v. good for short dist.
				15, //mgun - the bot rulez with it
				30, //chgun - as well here
				0, // granades - the bot sux with them.
				5, //g launcher - kind of sux still.
				30, //r launcher - is ok for slow moving enemies.
				30, //hyperblaster - ok
				35, //railgun - Rules
				15 //bfgk - energy cells
		};
		float maxImportance = 0;
		for (int i : importanceTable) maxImportance += i;

		float result = 0.0f;
		LinkedList<Integer> guns = bot.getIndexesOfOwnedGunsWithAmmo();
		for (int i : guns) {
				result += (importanceTable[i]*bot.getAmmunitionState(i));
		}
		return result / maxImportance;
		
	}

}
