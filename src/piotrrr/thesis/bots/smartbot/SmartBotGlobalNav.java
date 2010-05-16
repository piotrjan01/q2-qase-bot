package piotrrr.thesis.bots.smartbot;

import java.util.TreeSet;
import java.util.Vector;

import piotrrr.thesis.bots.wpmapbot.MapBotBase;
import piotrrr.thesis.common.CommFun;
import piotrrr.thesis.common.entities.EntityDoublePair;
import piotrrr.thesis.common.entities.EntityType;
import piotrrr.thesis.common.entities.EntityTypeDoublePair;
import piotrrr.thesis.common.navigation.GlobalNav;
import piotrrr.thesis.common.navigation.NavPlan;
import soc.qase.ai.waypoint.Waypoint;
import soc.qase.state.Entity;
import soc.qase.tools.vecmath.Vector3f;

/**
 * The global navigation module of the MapBotBase.
 * @author Piotr Gwizda�a
 * @see MapBotBase
 */
public class SmartBotGlobalNav extends GlobalNav {
	
	public static final double PLAN_TIME_PER_DIST = 0.1;
	
	public static final int maximalDistance = 200;
	
	/**
	 * Returns the new plan that the bot should follow
	 * @param bot the bot for which the plan is being established
	 * @param oldPlan the bot's old plan
	 * @return the new plan for the bot (can be the same as the oldPlan)
	 */
	@SuppressWarnings("null")
	static NavPlan establishNewPlan(SmartBot bot, NavPlan oldPlan) {
	
		/**
		 * When do we change the plan?
		 * + when we don't have plan
		 * + when we accomplish the old one
		 * - when the state was changed (fitness or firepower)
		 * + when the bot is stuck
		 * + when the decision times out....?
		 * - when the enemy appears
		 * + when the bot has good pickup opportunity
		 * + when it has died
		 */
		
		boolean changePlan = false;
		
		String talk = "";
		
		if (oldPlan == null) {
			changePlan = true;
			talk = "plan change: no plan!";
		}
		else if (oldPlan.done) {
			changePlan = true;
			bot.kb.addToBlackList(oldPlan.dest);
			talk = "plan change: old plan is done!";
		}
		//if the bot is stuck
		else if (bot.stuckDetector.isStuck) {
			processFailedPlan(bot, false);
			return getSpontaneousAntiStuckPlan(bot);
		}
		//if we timed out with the plan
		else if (oldPlan.deadline <= bot.getFrameNumber()) { 
			changePlan = true;
			processFailedPlan(bot, true);
		}
		
		if (talk != "" ) bot.dtalk.addToLog(talk);
		
		/**
		 * What do we do when we decide to change the plan?
		 * + if we see something that we can pick up in spontaneous decision - we pick it
		 * + if we don't do spontaneous pickup, we get the entities from KB basing on bot's state and
		 * choose one of them and create the plan to reach it.
		 */
		
		NavPlan plan = null; // the plan to return
		
		//If we didn't want to change the plan, we may still do it if there is some good spontaneous plan.
		if (! changePlan && oldPlan != null && ! oldPlan.isSpontaneos) plan = getSpontaneousPlan(bot);
		if (plan != null) return plan;
		
		//If no spontaneous plans available, we continue with old one...
		if (! changePlan) return oldPlan;
		
		
		TreeSet<EntityDoublePair> ranking = new TreeSet<EntityDoublePair>();
		//FIXME:
		EntityTypeDoublePair [] ents = null; //bot.fsm.getDesiredEntities();
		for (EntityTypeDoublePair etdp : ents) {
			Vector<Entity> items  = bot.kb.getActiveEntitiesByType(etdp.t, bot.getFrameNumber());
			for (Entity item : items) {
				double distance = getDistanceFollowingMap(bot, bot.getBotPosition(), item.getObjectPosition());
				if (distance == Double.MAX_VALUE) continue;
				double rank = 10000*etdp.d / distance; //the weight divided by distance
				ranking.add(new EntityDoublePair(item, rank));
			}
		}
		
		
		if (ranking.size() == 0 || bot.stuckDetector.isStuck) {
			Entity wp = bot.kb.getRandomItem();
			double distance = getDistanceFollowingMap(bot, bot.getBotPosition(), wp.getObjectPosition());
			bot.dtalk.addToLog("ranking size = 0, going for random item");
			plan = new NavPlan(wp, bot.getFrameNumber()+(int)(PLAN_TIME_PER_DIST*distance));
		}
		else {
			double distance = getDistanceFollowingMap(bot, bot.getBotPosition(), ranking.last().ent.getObjectPosition());
			bot.dtalk.addToLog("got new plan: rank: "+((int)ranking.last().dbl)+" et: "+EntityType.getEntityType(ranking.last().ent)+" dist: "+distance+" timeout: "+PLAN_TIME_PER_DIST*distance);
			plan = new NavPlan(ranking.last().ent, bot.getFrameNumber()+(int)(PLAN_TIME_PER_DIST*distance));
		}
		
		plan.path = bot.kb.findShortestPath(bot.getBotPosition(), plan.dest.getObjectPosition());
		
		return plan;
	}
	
	/**
	 * Returns the best available spontaneous plan for the given bot. Can be null. 
	 * @param bot the bot for whom we search for the plan
	 * @return the navigation plan with just wan waypoint that is close to the bot - so called spontaneous plan.
	 */
	static NavPlan getSpontaneousPlan(SmartBot bot) {
		NavPlan newPlan = null;
		
		Vector<Entity> entries = bot.kb.getActiveEntitiesWithinTheRange(bot.getBotPosition(), maximalDistance, bot.getFrameNumber());
		if (entries.size() == 0) return null;
		
		Entity chosen = null;
		for (Entity ent : entries) {
			if (! CommFun.areOnTheSameHeight(bot.getBotPosition(), ent.getObjectPosition())) continue;
			if (! bot.getBsp().isVisible(bot.getBotPosition(), ent.getObjectPosition())) continue;
			if (chosen != null) {
				float distOld = CommFun.getDistanceBetweenPositions(chosen.getObjectPosition(), bot.getBotPosition());
				float distNew = CommFun.getDistanceBetweenPositions(ent.getObjectPosition(), bot.getBotPosition());
				if (distNew < distOld) chosen = ent;
			}
			else chosen = ent;
		}
		
		if (chosen == null) return null;
		
//		if ( ! CommFun.areOnTheSameHeight(chosen.wp.getPosition(), bot.getBotPosition())) Dbg.err("not the same height!!!");
//		else Dbg.prn("bot h: "+bot.getBotPosition().z+" target h: "+chosen.wp.getPosition().z);
		
		bot.kb.addToBlackList(chosen);
		
		newPlan = new NavPlan(chosen, bot.getFrameNumber()+(int)(PLAN_TIME_PER_DIST*maximalDistance));
		newPlan.path = new Waypoint[1];
		newPlan.path[0] = new Waypoint(chosen.getObjectPosition());
		newPlan.isSpontaneos = true;
		
		double distance = CommFun.getDistanceBetweenPositions(bot.getBotPosition(), chosen.getObjectPosition());
		bot.dtalk.addToLog("got new spontaneous plan: et: "+chosen.toString()+" dist: "+distance+" timeout: "+distance*PLAN_TIME_PER_DIST);
		
		return newPlan;
	}
	
	/**
	 * If the bot is stuck it may need the spontaneous plan in some random direction in order to 
	 * move it out from the stuck position, get again close to it's known waypoints and be able to
	 * find a new plan.
	 * @param bot
	 * @return the random spontaneous decision.
	 */
	static NavPlan getSpontaneousAntiStuckPlan(SmartBot bot) {
		Entity re = bot.kb.getRandomItem();
		bot.kb.decPickupFailure(re);
		int timeout = (int)(80*PLAN_TIME_PER_DIST);
		NavPlan ret = new NavPlan(re, bot.getFrameNumber()+timeout);
		ret.path = new Waypoint[1];
		ret.path[0] = new Waypoint(re.getObjectPosition());
		ret.isSpontaneos = true;
//		int wpInd = bot.kb.map.indexOf(random.getNode());
		double distance = CommFun.getDistanceBetweenPositions(bot.getBotPosition(), re.getObjectPosition());
		bot.dtalk.addToLog("got new anti-stuck spontaneous plan: dist: "+distance+" timeout: "+timeout);
		return ret;
	}
	
	/**
	 * Returns the distance between given positions following the map
	 * @param bot the bot for whom the distance is calculated
	 * @param from initial position
	 * @param to final position
	 * @return distance between from and to following the shortest path on the map. 
	 * Double.MAX_VALUE is returned in case there is no path.
	 */
	static double getDistanceFollowingMap(SmartBot bot, Vector3f from, Vector3f to) {
		double distance = 0.0d;
		Waypoint [] path = bot.kb.map.findShortestPath(from, to);
		if (path == null) {
//			Dbg.err("Path is null at counting distance on map.");
			return Double.MAX_VALUE;
		}
		Vector3f pos = from;
		for (Waypoint wp : path) {
			distance += CommFun.getDistanceBetweenPositions(pos, wp.getObjectPosition());
			pos = wp.getObjectPosition();
		}
		return distance;
	}
	
	
	/**
	 * Processes the failed plan. The remembers if it failed to reach
	 * for the specific entity or if it failed to go from one waypoint
	 * to another. This information will be used later to remove some 
	 * entities from KB or some edges from waypoint map.
	 * @param bot the bot that has failed the plan
	 * @param checkIfTheBotIsFarFromThePath if true, we mark the failure
	 * of the edge on the map only if the bot is far from the waypoints
	 * the edge is supposed to connect.
	 */
	static void processFailedPlan(MapBotBase bot, boolean checkIfTheBotIsFarFromThePath) {
		NavPlan oldPlan = bot.plan;
		String talk = "plan change: bot is stuck <--------------------- \n";
		//if we are at the end of the path:
		if (oldPlan != null && 
			oldPlan.path != null && 
			(oldPlan.isSpontaneos || oldPlan.pathIndex >= oldPlan.path.length-1)) {
			//we mark that this item failed to be picked
			bot.kb.failedToPickup(oldPlan.dest);
			talk += " at the end of the path";
		}
		//if we were in the middle of the path:
		else {
			talk += " in the middle of the path";
			//if there is no opponent visible (we are not stuck because of him)
			if (oldPlan != null && oldPlan.path != null && ! bot.isOpponentVisible() && 
					oldPlan.pathIndex > 0 && 
					oldPlan.pathIndex < oldPlan.path.length) {
				Waypoint src = oldPlan.path[oldPlan.pathIndex-1];
				Waypoint dst = oldPlan.path[oldPlan.pathIndex];
				if ( checkIfTheBotIsFarFromThePath && 
					CommFun.getDistanceBetweenPositions(bot.getBotPosition(), src.getObjectPosition()) > 200 &&
					CommFun.getDistanceBetweenPositions(bot.getBotPosition(), dst.getObjectPosition()) > 200)
				//we mark that the edge between those waypoints have failed
				bot.kb.waypointEdgesFailures[bot.kb.map.indexOf(src)][bot.kb.map.indexOf(dst)]++;
				else if ( ! checkIfTheBotIsFarFromThePath)
					bot.kb.waypointEdgesFailures[bot.kb.map.indexOf(src)][bot.kb.map.indexOf(dst)]++;
			}
		}
		bot.dtalk.addToLog(talk);
	}
	
}
