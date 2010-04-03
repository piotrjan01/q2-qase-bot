package piotrrr.thesis.bots.simplebot.navigation.global;

import piotrrr.thesis.bots.simplebot.SimpleBot;
import piotrrr.thesis.common.navigation.NavPlan;

/**
 * The global navigation module of the SimpleBot.
 * @author Piotr Gwizda³a
 * @see SimpleBot
 */
public class GlobalNav {
	
	public static NavPlan establishNewPlan(SimpleBot bot, NavPlan oldPlan) {
	
		/**
		 * When do we change the plan?
		 * + when we don't have plan
		 * + when we accomplish the old one
		 * + when the state was changed
		 * - when the bot is stuck
		 * + when the decision times out....?
		 * - when the enemy appears
		 * - when the bot has good pickup opportunity
		 * - when it has died
		 */
		
		boolean changePlan = false;
		
		if (oldPlan == null || oldPlan.done || bot.stateChanged) changePlan = true;
		
		if (oldPlan.deadline <= bot.getFrameNumber()) changePlan = true;
		
		if (! changePlan) return oldPlan;
		
		/**
		 * What do we do when we decide to change the plan?
		 * - if there was a parent plan - we continue with it
		 * - we get the entities from KB basing on bot's state and
		 * choose one of them and create the plan to reach it.
		 */
		
		if (oldPlan.parentPlan != null) return oldPlan.parentPlan;
		
		
		
		return null;
	}

}
