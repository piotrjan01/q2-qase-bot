package piotrrr.thesis.common.navigation;

import soc.qase.ai.waypoint.Waypoint;

/**
 * This class encapsulates the path that the bot has planned to move along.
 * @author Piotr Gwizda³a
 */
public class NavPlan {

	/**
	 * The destination waypoint
	 */
	public Waypoint dest;
	
	/**
	 * The array that represents the path chosen. The first element is the source
	 * the last element is the destination.
	 */
	public Waypoint [] path;
	
	public NavPlan(Waypoint dest) {
		this.dest = dest;
	}
	
}
