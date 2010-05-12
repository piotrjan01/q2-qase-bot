package piotrrr.thesis.common.navigation;

import piotrrr.thesis.bots.wpmapbot.MapBotBase;

/**
 * The global navigation module of the MapBotBase.
 * @author Piotr Gwizda³a
 * @see MapBotBase
 */
public class GlobalNav {
	
	/**
	 * Returns the new plan that the bot should follow
	 * @param bot the bot for which the plan is being established
	 * @param oldPlan the bot's old plan
	 * @return the new plan for the bot (can be the same as the oldPlan)
	 */
	public NavPlan establishNewPlan(MapBotBase bot, NavPlan oldPlan) {
	
		/**
		 * When do we change the plan?
		 * + when we don't have plan
		 * + when we accomplish the old one
		 * + when the state was changed
		 * + when the bot is stuck
		 * + when the decision times out....?
		 * - when the enemy appears
		 * + when the bot has good pickup opportunity
		 * + when it has died
		 */
		
		
		return null;
	}
}
