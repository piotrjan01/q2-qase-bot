package piotrrr.thesis.bots.simplebot;

import java.util.LinkedList;
import java.util.Random;
import java.util.TreeSet;
import java.util.Vector;

import piotrrr.thesis.common.CommFun;
import piotrrr.thesis.common.entities.EntityType;
import piotrrr.thesis.common.entities.EntityTypeDoublePair;
import piotrrr.thesis.common.navigation.NavPlan;
import soc.qase.ai.waypoint.Waypoint;
import soc.qase.ai.waypoint.WaypointItem;
import soc.qase.state.Entity;
import soc.qase.tools.vecmath.Vector3f;

/**
 * The global navigation module of the SimpleBot.
 * @author Piotr Gwizda³a
 * @see SimpleBot
 */
public class GlobalNav {
	
	public static final double PLAN_TIME_PER_DIST = 0.08;
	
	public static final int maximalDistance = 200;
	
	/**
	 * Returns the new plan that the bot should follow
	 * @param bot the bot for which the plan is being established
	 * @param oldPlan the bot's old plan
	 * @return the new plan for the bot (can be the same as the oldPlan)
	 */
	static NavPlan establishNewPlan(SimpleBot bot, NavPlan oldPlan) {
	
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
		
		boolean changePlan = false;
		
		String talk = "";
		
		if (oldPlan == null) {
			changePlan = true;
			talk = "plan change: no plan!";
		}
		else if (oldPlan.done) {
			changePlan = true;
			//FIXME ?? blacklist ?? sprawdzic co wyklucza lepiej: respawn time czy blacklist?
			oldPlan.dest.setRespawnFrame(bot.getFrameNumber()+600); //60 seconds
			talk = "plan change: old plan is done!";
		}
		else if (bot.stateReporter.stateHasChanged) {
			changePlan = true;
			talk = "plan change: state changed";
		}
		else if (bot.stuckDetector.isStuck) {
			changePlan = true;
			talk = "plan change: bot is stuck <-----------------------------";
			bot.dtalk.addToLog(talk);
			return getSpontaneousAntiStuckPlan(bot);
		}
		else if (oldPlan.deadline <= bot.getFrameNumber()) { 
			changePlan = true;
			talk = "plan change: timeout";
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
		
		
		TreeSet<WPItemDoublePair> ranking = new TreeSet<WPItemDoublePair>();
		EntityTypeDoublePair [] ents = bot.fsm.getDesiredEntities();
		for (EntityTypeDoublePair etdp : ents) {
			Vector<WaypointItem> items  = bot.kb.getActiveEntitiesByType(etdp.t, bot.getFrameNumber());
			for (WaypointItem item : items) {
				double distance = getDistanceFollowingMap(bot, bot.getBotPosition(), item.getNode().getPosition());
				if (distance == Double.MAX_VALUE) continue;
				double rank = 10000*etdp.d / distance; //the weight divided by distance
				ranking.add(new WPItemDoublePair(item, rank));
			}
		}
		
		
		if (ranking.size() == 0 || bot.stuckDetector.isStuck) {
			Waypoint wp = getSomeDistantWaypoint(bot);
			double distance = getDistanceFollowingMap(bot, bot.getBotPosition(), wp.getPosition());
			int wpInd = bot.kb.map.indexOf(wp);
//			bot.dtalk.addToLog("moving to distant wp: "+wpInd);
			plan = new NavPlan(new WaypointItem(wp, new Entity()), bot.getFrameNumber()+(int)(PLAN_TIME_PER_DIST*distance));
//			plan = new NavPlan(new KBEntry(wp, EntityType.UNKNOWN, 0, false), bot.getFrameNumber()+(int)(PLAN_TIME_PER_DIST*distance));
		}
		else {
			//when wpInf is -1 it means that the kbe is added basing on observation, not from the map.
			int wpInd = bot.kb.map.indexOf(ranking.last().wpi.getNode());
			double distance = getDistanceFollowingMap(bot, bot.getBotPosition(), ranking.last().wpi.getNode().getPosition());
//			bot.dtalk.addToLog("got new plan: rank: "+((int)ranking.last().dbl)+" et: "+ranking.last().kbe.et.name()+"@"+wpInd+" dist: "+distance+" timeout: "+PLAN_TIME_PER_DIST*distance);
			plan = new NavPlan(ranking.last().wpi, bot.getFrameNumber()+(int)(PLAN_TIME_PER_DIST*distance));
		}
		
		plan.path = bot.kb.findShortestPath(bot.getBotPosition(), plan.dest.getNode().getPosition());
		
		return plan;
	}
	
	/**
	 * Returns the best available spontaneous plan for the given bot. Can be null. 
	 * @param bot the bot for whom we search for the plan
	 * @return the navigation plan with just wan waypoint that is close to the bot - so called spontaneous plan.
	 */
	static NavPlan getSpontaneousPlan(SimpleBot bot) {
		NavPlan newPlan = null;
		
		
		
		Vector<WaypointItem> entries = bot.kb.getActiveEntitiesWithinTheRange(bot.getBotPosition(), maximalDistance, bot.getFrameNumber());
		if (entries.size() == 0) return null;
		
		WaypointItem chosen = null;
		for (WaypointItem ent : entries) {
			if (! CommFun.areOnTheSameHeight(bot.getBotPosition(), ent.getNode().getPosition())) continue;
			if (! bot.getBsp().isVisible(bot.getBotPosition(), ent.getNode().getPosition())) continue;
			if (chosen != null) {
				float distOld = CommFun.getDistanceBetweenPositions(chosen.getNode().getPosition(), bot.getBotPosition());
				float distNew = CommFun.getDistanceBetweenPositions(ent.getNode().getPosition(), bot.getBotPosition());
				if (distNew < distOld) chosen = ent;
			}
			else chosen = ent;
		}
		
		if (chosen == null) return null;
		
//		if ( ! CommFun.areOnTheSameHeight(chosen.wp.getPosition(), bot.getBotPosition())) Dbg.err("not the same height!!!");
//		else Dbg.prn("bot h: "+bot.getBotPosition().z+" target h: "+chosen.wp.getPosition().z);
		
		bot.kb.addToBlackList(chosen.getNode());
		
		newPlan = new NavPlan(chosen, bot.getFrameNumber()+(int)(PLAN_TIME_PER_DIST*maximalDistance));
		newPlan.path = new Waypoint[1];
		newPlan.path[0] = chosen.getNode();
		newPlan.isSpontaneos = true;
		
		int wpInd = bot.kb.map.indexOf(chosen.getNode());
		double distance = CommFun.getDistanceBetweenPositions(bot.getBotPosition(), chosen.getNode().getPosition());
//		bot.dtalk.addToLog("got new spontaneous plan: et: "+chosen.et.name()+"@"+wpInd+" dist: "+distance+" timeout: "+distance*PLAN_TIME_PER_DIST);
		
		return newPlan;
	}
	
	/**
	 * If the bot is stuck it may need the spontaneous plan in some random direction in order to 
	 * move it out from the stuck position, get again close to it's known waypoints and be able to
	 * find a new plan.
	 * @param bot
	 * @return the random spontaneous decision.
	 */
	static NavPlan getSpontaneousAntiStuckPlan(SimpleBot bot) {
		WaypointItem random = bot.kb.getRandomItem();
		int timeout = (int)(80*PLAN_TIME_PER_DIST);
		NavPlan ret = new NavPlan(random, bot.getFrameNumber()+timeout);
		ret.path = new Waypoint[1];
		ret.path[0] = random.getNode();
		ret.isSpontaneos = true;
		int wpInd = bot.kb.map.indexOf(random.getNode());
		double distance = CommFun.getDistanceBetweenPositions(bot.getBotPosition(), random.getNode().getPosition());
		bot.dtalk.addToLog("got new anti-stuck spontaneous plan: @"+wpInd+" dist: "+distance+" timeout: "+timeout);
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
	static double getDistanceFollowingMap(SimpleBot bot, Vector3f from, Vector3f to) {
		double distance = 0.0d;
		Waypoint [] path = bot.kb.map.findShortestPath(from, to);
		if (path == null) {
//			Dbg.err("Path is null at counting distance on map.");
			return Double.MAX_VALUE;
		}
		Vector3f pos = from;
		for (Waypoint wp : path) {
			distance += CommFun.getDistanceBetweenPositions(pos, wp.getPosition());
			pos = wp.getPosition();
		}
		return distance;
	}
	
	/**
	 * Returns some distant, random waypoint on the map. Used when really don't know what to do and
	 * needs to go somewhere to find out new entities.
	 * @return
	 */
	static private Waypoint getSomeDistantWaypoint(SimpleBot bot) {
		//the minimal distance considered to be 'distant' waypoint.
		int minDistance = 300;
		Random rand = new Random();
		int waypointCount = bot.kb.map.getAllNodes().length;
		int wpNum = rand.nextInt();
		wpNum = Math.abs(wpNum) % waypointCount;
		Waypoint ret = bot.kb.map.getNode(wpNum);
		if (getDistanceFollowingMap(bot, bot.getBotPosition(), ret.getPosition()) < minDistance) 
				return getSomeDistantWaypoint(bot);
		else return ret;
	}	

}
