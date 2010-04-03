package piotrrr.thesis.knowledge;

import piotrrr.thesis.misc.entities.EntityType;
import soc.qase.ai.waypoint.Waypoint;

/**
 * Waypoint, EntityType, estimated respawn time.
 * @author Piotr Gwizda³a
 */
public class KBEntry {

	Waypoint wp;
	EntityType et;
	double ert;
	
	public KBEntry(Waypoint wp, EntityType et, double ert) {
		this.wp = wp;
		this.et = et;
		this.ert = ert;
	}
	
}
