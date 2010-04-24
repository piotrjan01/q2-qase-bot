package piotrrr.thesis.bots.simplebot;

import java.util.LinkedList;
import java.util.Random;
import java.util.TreeSet;

import piotrrr.thesis.common.CommFun;
import piotrrr.thesis.common.entities.EntityType;
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
	
	public static final double PLAN_TIME_PER_DIST = 0.08;
	
	static NavPlan establishNewPlan(SimpleBot bot, NavPlan oldPlan) {
	
		/**
		 * When do we change the plan?
		 * + when we don't have plan
		 * + when we accomplish the old one
		 * + when the state was changed
		 * + when the bot is stuck
		 * + when the decision times out....?
		 * - when the enemy appears
		 * - when the bot has good pickup opportunity
		 * - when it has died
		 */
		
		boolean changePlan = false;
		
		if (oldPlan == null) {
			changePlan = true;
			bot.dtalk.addToLog("plan change: no plan!");
		}
		else if (oldPlan.done) {
			changePlan = true;
			bot.dtalk.addToLog("plan change: old plan is done!");
		}
		else if (bot.stateReporter.stateHasChanged) {
			changePlan = true;
			bot.dtalk.addToLog("plan change: state changed");
		}
		else if (bot.stuckDetector.isStuck) {
			changePlan = true;
			bot.dtalk.addToLog("plan change: bot is stuck <-----------------------------");
			return getSpontaneousAntiStuckPlan(bot);
		}
		else if (oldPlan.deadline <= bot.getFrameNumber()) { 
			changePlan = true;
			bot.dtalk.addToLog("plan change: timeout");
		}
		
		/**
		 * What do we do when we decide to change the plan?
		 * + if there was a parent plan - we continue with it
		 * - if we see something that we can pick up in spontaneous decision - we pick it
		 * - if we don't do spontaneous pickup, we get the entities from KB basing on bot's state and
		 * choose one of them and create the plan to reach it.
		 */
		
		NavPlan plan = null; // the plan to return
		
		if (! changePlan && oldPlan != null && ! oldPlan.isSpontaneos) plan = getSpontaneousPlan(bot, oldPlan);
		if (plan != null) return plan;
		
		if (! changePlan) return oldPlan;
		
		
		if (oldPlan != null && oldPlan.parentPlan != null) {
			bot.dtalk.addToLog("coming back to parent plan...");
			plan = oldPlan.parentPlan;
			plan.path = bot.map.findShortestPath(bot.getBotPosition(), plan.dest.getPosition());
			plan.pathIndex = 0;
			return oldPlan.parentPlan;
		}
		
		
		
		TreeSet<KBEntryDoublePair> ranking = new TreeSet<KBEntryDoublePair>();
		EntityTypeDoublePair [] ents = bot.fsm.getDesiredEntities();
		for (EntityTypeDoublePair etdp : ents) {
			LinkedList<KBEntry> entries  = bot.kb.getActiveEntitiesByType(etdp.t, bot.getFrameNumber());
			for (KBEntry entry : entries) {
				double distance = getDistanceFollowingMap(bot, bot.getBotPosition(), entry.wp.getPosition());
				if (distance == Double.MAX_VALUE) continue;
				double rank = 10000*etdp.d / distance; //the weight divided by distance
				ranking.add(new KBEntryDoublePair(entry, rank));
			}
		}
		
		
		if (ranking.size() == 0 || bot.stuckDetector.isStuck) {
			Waypoint wp = getSomeDistantWaypoint(bot);
			double distance = getDistanceFollowingMap(bot, bot.getBotPosition(), wp.getPosition());
			int wpInd = bot.map.indexOf(wp);
			bot.dtalk.addToLog("moving to distant wp: "+wpInd);
			plan = new NavPlan(wp, bot.getFrameNumber()+(int)(PLAN_TIME_PER_DIST*distance));
		}
		else {
			//when wpInf is -1 it means that the kbe is added basing on observation, not from the map.
			int wpInd = bot.map.indexOf(ranking.last().kbe.wp);
			double distance = getDistanceFollowingMap(bot, bot.getBotPosition(), ranking.last().kbe.wp.getPosition());
			bot.dtalk.addToLog("got new plan: rank: "+((int)ranking.last().dbl)+" et: "+ranking.last().kbe.et.name()+"@"+wpInd+" dist: "+distance+" timeout: "+PLAN_TIME_PER_DIST*distance);
			plan = new NavPlan(ranking.last().kbe.wp, bot.getFrameNumber()+(int)(PLAN_TIME_PER_DIST*distance));
		}
		
		plan.path = bot.map.findShortestPath(bot.getBotPosition(), plan.dest.getPosition());
		
		return plan;
	}
	
	static NavPlan getSpontaneousPlan(SimpleBot bot, NavPlan parentPlan) {
		NavPlan newPlan = null;
		
		//TODO:
		int maximalDistance = 200;
		
		LinkedList<KBEntry> entries = bot.kb.getActiveEntitiesWithinTheRange(bot.getBotPosition(), maximalDistance, bot.getFrameNumber());
		if (entries.size() == 0) return null;
		
		KBEntry chosen = null;
		for (KBEntry ent : entries) {
			if (ent.et.equals(EntityType.PLAYER)) continue;
			if (! CommFun.areOnTheSameHeight(bot.getBotPosition(), ent.wp.getPosition())) continue;
			if (! bot.getBsp().isVisible(bot.getBotPosition(), ent.wp.getPosition())) continue;
			if (chosen != null) {
				float distOld = CommFun.getDistanceBetweenPositions(chosen.wp.getPosition(), bot.getBotPosition());
				float distNew = CommFun.getDistanceBetweenPositions(ent.wp.getPosition(), bot.getBotPosition());
				if (distNew < distOld) chosen = ent;
			}
			else chosen = ent;
		}
		
		if (chosen == null) return null;
		
//		if ( ! CommFun.areOnTheSameHeight(chosen.wp.getPosition(), bot.getBotPosition())) Dbg.err("not the same height!!!");
//		else Dbg.prn("bot h: "+bot.getBotPosition().z+" target h: "+chosen.wp.getPosition().z);
		
		bot.kb.addToBlackList(chosen);
		
		newPlan = new NavPlan(chosen.wp, bot.getFrameNumber()+(int)(PLAN_TIME_PER_DIST*maximalDistance));
		newPlan.parentPlan = parentPlan;
		newPlan.path = new Waypoint[1];
		newPlan.path[0] = chosen.wp;
		newPlan.isSpontaneos = true;
		
		int wpInd = bot.map.indexOf(chosen.wp);
		double distance = CommFun.getDistanceBetweenPositions(bot.getBotPosition(), chosen.wp.getPosition());
		bot.dtalk.addToLog("got new spontaneous plan: et: "+chosen.et.name()+"@"+wpInd+" dist: "+distance+" timeout: "+distance*PLAN_TIME_PER_DIST);
		
		return newPlan;
	}
	
	static NavPlan getSpontaneousAntiStuckPlan(SimpleBot bot) {
		Waypoint random = getSomeDistantWaypoint(bot);
		int timeout = (int)(80*PLAN_TIME_PER_DIST);
		NavPlan ret = new NavPlan(random, bot.getFrameNumber()+timeout);
		ret.path = new Waypoint[1];
		ret.path[0] = random;
		ret.isSpontaneos = true;
		int wpInd = bot.map.indexOf(random);
		double distance = CommFun.getDistanceBetweenPositions(bot.getBotPosition(), random.getPosition());
		bot.dtalk.addToLog("got new anti-stuck spontaneous plan: @"+wpInd+" dist: "+distance+" timeout: "+timeout);
		return ret;
	}
	
	static double getDistanceFollowingMap(SimpleBot bot, Vector3f from, Vector3f to) {
		double distance = 0.0d;
		Waypoint [] path = bot.map.findShortestPath(from, to);
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
		int waypointCount = bot.map.getAllNodes().length;
		int wpNum = rand.nextInt();
		wpNum = Math.abs(wpNum) % waypointCount;
		Waypoint ret = bot.map.getNode(wpNum);
		if (getDistanceFollowingMap(bot, bot.getBotPosition(), ret.getPosition()) < minDistance) 
				return getSomeDistantWaypoint(bot);
		else return ret;
	}
	

}
