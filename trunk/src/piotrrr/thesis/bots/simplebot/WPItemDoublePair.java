package piotrrr.thesis.bots.simplebot;

import soc.qase.ai.waypoint.WaypointItem;

public class WPItemDoublePair implements Comparable<WPItemDoublePair> {
	
	WaypointItem wpi = null;
	
	double dbl = 0.0;
	
	public WPItemDoublePair(WaypointItem kbe, double dbl) {
		this.wpi = kbe;
		this.dbl = dbl;
	}

	@Override
	public int compareTo(WPItemDoublePair o) {
		if (this.dbl > o.dbl) return 1;
		if (this.dbl < o.dbl) return -1;
		return 0;
	}

}
