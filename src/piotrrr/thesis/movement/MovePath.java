package piotrrr.thesis.movement;

import soc.qase.ai.waypoint.Waypoint;

public class MovePath {

	public Waypoint dest;
	
	public Waypoint [] path;
	
	public boolean done;
	
	public MovePath(Waypoint dest) {
		this.dest = dest;
		this.done = false;
	}
	
}
