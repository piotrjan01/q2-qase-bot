package piotrrr.thesis.knowledge;

import piotrrr.thesis.misc.entities.EntityType;
import soc.qase.ai.waypoint.Waypoint;

/**
 * The class that encapsulates: Waypoint, EntityType, estimated respawn time.
 * @author Piotr Gwizda³a
 */
public class KBEntry {

	/**
	 * Waypoint type of the entry
	 */
	Waypoint wp;
	
	/**
	 * Entity type of the entry
	 */
	EntityType et;
	
	/**
	 * Estimated respawn time. This tells at which frame the entry should 
	 * reappear in the world.
	 *
	 */
	long ert;
	
	/**
	 * Creates the KB Entry.
	 * @param wp - waypoint
	 * @param et - entity type
	 * @param ert - estimated respawn time
	 */
	public KBEntry(Waypoint wp, EntityType et, long ert) {
		this.wp = wp;
		this.et = et;
		this.ert = ert;
	}
	
}
