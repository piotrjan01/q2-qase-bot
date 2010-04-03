package piotrrr.thesis.movement;

import soc.qase.ai.waypoint.Waypoint;

public class MovePlan {

	public Waypoint dest;
	
	public Waypoint [] path;
	
	public boolean done;
	
	public MovePlan(Waypoint dest) {
		this.dest = dest;
		this.done = false;
	}
	
}
