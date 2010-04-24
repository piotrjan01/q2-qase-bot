package piotrrr.thesis.bots.simplebot;

import java.util.LinkedList;
import java.util.Random;
import java.util.TreeSet;

import piotrrr.thesis.common.CommFun;
import piotrrr.thesis.common.entities.EntityTypeDoublePair;
import piotrrr.thesis.common.navigation.NavPlan;
import soc.qase.ai.waypoint.Waypoint;
import soc.qase.tools.vecmath.Vector3f;

/**
 * The global navigation module of the SimpleBot.
 * @author Piotr Gwizda³a
 * @see SimpleBot
 */
class GlobalNav {
	
	public static final long PLAN_TIME_PER_DIST = 5;
	
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
			bot.dtalk.addToLog("plan change: bot is stuck");
		}
		else if (oldPlan.deadline <= bot.getFrameNumber()) { 
			changePlan = true;
			bot.dtalk.addToLog("plan change: timeout");
		}
		
		if (! changePlan) return oldPlan;
		
		
		
		/**
		 * What do we do when we decide to change the plan?
		 * + if there was a parent plan - we continue with it
		 * - if we see something that we can pick up in spontaneous decision - we pick it
		 * - if we don't do spontaneous pickup, we get the entities from KB basing on bot's state and
		 * choose one of them and create the plan to reach it.
		 */
		
		if (oldPlan != null && oldPlan.parentPlan != null) return oldPlan.parentPlan;
		
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
		
		NavPlan plan;
		if (ranking.size() == 0 || bot.stuckDetector.isStuck) {
			Waypoint wp = getSomeDistantWaypoint(bot);
			double distance = getDistanceFollowingMap(bot, bot.getBotPosition(), wp.getPosition());
			int wpInd = bot.map.indexOf(wp);
			bot.dtalk.addToLog("moving to distant wp: "+wpInd);
			plan = new NavPlan(wp, bot.getFrameNumber()+(int)(PLAN_TIME_PER_DIST*distance));
		}
		else {
			int wpInd = bot.map.indexOf(ranking.last().kbe.wp);
			double distance = getDistanceFollowingMap(bot, bot.getBotPosition(), ranking.last().kbe.wp.getPosition());
			bot.dtalk.addToLog("got new plan: rank: "+((int)ranking.last().dbl)+" et: "+ranking.last().kbe.et.name()+"@"+wpInd);
			plan = new NavPlan(ranking.last().kbe.wp, bot.getFrameNumber()+(int)(PLAN_TIME_PER_DIST*distance));
		}
		
		plan.path = bot.map.findShortestPath(bot.getBotPosition(), plan.dest.getPosition());
		
		return plan;
	}
	
	static NavPlan getSpontaneousPlan(SimpleBot bot, NavPlan parentPlan) {
		NavPlan newPlan = null;
		
		
		int maximalDistance = 200;
		float distance = 0.f;
		Vector3f playerPos = new Vector3f(bot.getWorld().getPlayer().getPlayerMove().getOrigin());
		/*
		TreeSet<EntityRecord> ers = new TreeSet<EntityRecord>();
		
		for (Entity e : allReadyToPickupItems) {
			distance = CommFun.getDistanceBetweenPositions(playerPos, new Vector3f(e.getOrigin()));
			if ( ! e.isPlayerEntity() && isVisible(e) && distance < maximalDistance &&
					areOnTheSameHeight(new Vector3f(e.getOrigin()), playerPos) 
					&& ! spontaneousBlackList.contains(e.getNumber()))
			ers.add(new EntityRecord(distance, e));
		}	
		if (ers.isEmpty()) return null;
		if (debugSD) addToLog("==>"+ers.first().entity.getType()+"<== allEnts:"+allReadyToPickupItems.size());
		spontaneousBlackList.add(ers.first().entity.getNumber());
		if (spontaneousBlackList.size()>10) spontaneousBlackList.remove(0);
		return new NavigationDecision(new Waypoint(ers.first().entity.getOrigin()), 15, parent);	
		
		newPlan.parentPlan = parentPlan;*/
		return newPlan;
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
