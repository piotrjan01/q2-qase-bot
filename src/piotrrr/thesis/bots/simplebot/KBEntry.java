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
	 * picked up will never respawn again. The entities that are on the map should be the ones that respawn.
	 */
	boolean isFromMap;
	
	/**
	 * Creates the KB Entry.
	 * @param wp - waypoint
	 * @param et - entity type
	 * @param ert - estimated respawn time
	 * @param isFromMap - is it from bot's map?
	 */
	KBEntry(Waypoint wp, EntityType et, long ert, boolean isFromMap) {
		this.wp = wp;
		this.et = et;
		this.ert = ert;
		this.isFromMap = isFromMap;
	}
	
	@Override
	public boolean equals(Object obj) {
		KBEntry other = (KBEntry)obj;
		if (wp.getPosition().equals(other.wp.getPosition()) &&
				et.equals(other.et)) return true;
		return false;
	}
	
}
