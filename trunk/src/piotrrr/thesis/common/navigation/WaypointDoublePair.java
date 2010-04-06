package piotrrr.thesis.common.navigation;

import soc.qase.ai.waypoint.Waypoint;

/**
 * Encapsulates the Waypoint class with 
 * double precision floating number
 * @author Piotr Gwizda³a
 * @see Waypoint
 */
public class WaypointDoublePair implements Comparable<WaypointDoublePair> {
	
	Waypoint wp = null;
	
	double dbl = 0.0;
	
	/**
	 * Default constructor
	 * @param wp - Waypoint to be set
	 * @param db - the corresponding double value
	 */
	public WaypointDoublePair(Waypoint wp, double db) {
		this.wp = wp;
		this.dbl = db;
	}

	@Override
	public int compareTo(WaypointDoublePair o) {
		if (this.dbl > o.dbl) return 1;
		if (this.dbl < o.dbl) return -1;
		return 0;
	}

}
