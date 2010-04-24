package piotrrr.thesis.common.navigation;

import piotrrr.thesis.bots.simplebot.KBEntry;
import soc.qase.ai.waypoint.Waypoint;

/**
 * This class encapsulates the path that the bot has planned to move along.
 * @author Piotr Gwizda³a
 */
public class NavPlan {

	/**
	 * The destination waypoint
	 */
	public KBEntry dest;
	
	/**
	 * Tells whether the plan is done.
	 */
	public boolean done = false;
	
	public long deadline = 0;
	
	public int pathIndex = 0;
	
	public boolean isSpontaneos = false;
	
	/**
	 * The array that represents the path chosen. The first element is the source
	 * the last element is the destination.
	 */
	public Waypoint [] path;
	
	public NavPlan(KBEntry dest, long deadline) {
		this.dest = dest;
		this.deadline = deadline;
	}
	
}
