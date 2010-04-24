package piotrrr.thesis.bots.simplebot;

import piotrrr.thesis.common.entities.EntityType;
import soc.qase.ai.waypoint.Waypoint;

/**
 * The class that encapsulates: Waypoint, EntityType, estimated respawn time.
 * @author Piotr Gwizda³a
 */
class KBEntry {

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
	 * This field is set to true if this item is respawning with some period. If set to false, the item once
	 * picked up will never respawn again.
	 */
	boolean isRespawningEntity;
	
	/**
	 * Creates the KB Entry.
	 * @param wp - waypoint
	 * @param et - entity type
	 * @param ert - estimated respawn time
	 */
	
	KBEntry(Waypoint wp, EntityType et, long ert) {
		this.wp = wp;
		this.et = et;
		this.ert = ert;
		this.isRespawningEntity = true;
	}
	
	KBEntry(Waypoint wp, EntityType et, long ert, boolean respawningEntity) {
		this.wp = wp;
		this.et = et;
		this.ert = ert;
		this.isRespawningEntity = respawningEntity;
	}
	
}
