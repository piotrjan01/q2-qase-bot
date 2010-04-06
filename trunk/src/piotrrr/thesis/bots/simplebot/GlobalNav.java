package piotrrr.thesis.bots.simplebot;

import java.util.LinkedList;
import java.util.TreeSet;

import piotrrr.thesis.common.CommFun;
import piotrrr.thesis.common.entities.EntityTypeDoublePair;
import piotrrr.thesis.common.navigation.NavPlan;
import piotrrr.thesis.tools.Dbg;
import soc.qase.ai.waypoint.Waypoint;
import soc.qase.tools.vecmath.Vector3f;

/**
 * The global navigation module of the SimpleBot.
 * @author Piotr Gwizda³a
 * @see SimpleBot
 */
class GlobalNav {
	
	public static final long PLAN_TIME = 150;
	
	
	static NavPlan establishNewPlan(SimpleBot bot, NavPlan oldPlan) {
	
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
		
		else if (oldPlan.deadline <= bot.getFrameNumber()) changePlan = true;
		
		if (! changePlan) return oldPlan;
		
		/**
		 * What do we do when we decide to change the plan?
		 * - if there was a parent plan - we continue with it
		 * 
		 * - if we don't do spontaneous pickup, we get the entities from KB basing on bot's state and
		 * choose one of them and create the plan to reach it.
		 */
		
		if (oldPlan != null && oldPlan.parentPlan != null) return oldPlan.parentPlan;
		
		TreeSet<KBEntryDoublePair> ranking = new TreeSet<KBEntryDoublePair>();
		EntityTypeDoublePair [] ents = bot.fsm.getDesiredEntities();
		for (EntityTypeDoublePair etdp : ents) {
			LinkedList<KBEntry> entries  = bot.kb.getActiveEntitiesByType(etdp.t, bot.getFrameNumber());
			for (KBEntry entry : entries) {
				double rank = getDistanceFollowingMap(bot, bot.getBotPosition(), entry.wp.getPosition());
				rank = etdp.d / rank; //the weight divided by distance
				ranking.add(new KBEntryDoublePair(entry, rank));
			}
		}
		
		NavPlan plan = new NavPlan(ranking.last().kbe.wp, bot.getFrameNumber()+PLAN_TIME);
		plan.path = bot.map.findShortestPath(bot.getBotPosition(), plan.dest.getPosition());
		
		return plan;
	}
	
	static double getDistanceFollowingMap(SimpleBot bot, Vector3f from, Vector3f to) {
		double distance = 0.0;
		Waypoint [] path = bot.map.findShortestPath(from, to);
		if (path == null) {
			Dbg.err("Path is null at counting distance on map.");
			return Double.MAX_VALUE;
		}
		Vector3f pos = from;
		for (Waypoint wp : path) {
			distance += CommFun.getDistanceBetweenPositions(pos, wp.getPosition());
			pos = wp.getPosition();
		}
		return distance;
	}
	

}
