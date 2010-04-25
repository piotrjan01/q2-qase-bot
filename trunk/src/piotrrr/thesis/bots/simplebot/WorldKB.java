package piotrrr.thesis.bots.simplebot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

import piotrrr.thesis.common.CommFun;
import piotrrr.thesis.common.entities.EntityType;
import soc.qase.ai.waypoint.Waypoint;
import soc.qase.ai.waypoint.WaypointItem;
import soc.qase.ai.waypoint.WaypointMap;
import soc.qase.state.Entity;
import soc.qase.tools.vecmath.Vector3f;

/**
 * @author Piotr Gwizda³a
 */
class WorldKB {
	
	/**
	 * The map containing the information on entities in the environment
	 */
	HashMap<EntityType, LinkedList<KBEntry>> kb;
	
	/**
	 * The black list of pickup things. If the bot decides to pickup something, 
	 * he shouldn't repeat this decision for some time, in case the thing can not be
	 * picked up. 
	 */
	LinkedList<KBEntry> pickupBlacklist;
	
	/**
	 * The maximum size of pickupBlaclist
	 */
	static final int PICKUP_BLACKLIST_MAX_SIZE = 10;
	
	private WorldKB() {
		kb = new HashMap<EntityType, LinkedList<KBEntry>>();
		pickupBlacklist = new LinkedList<KBEntry>();
	}
	
	/**
	 * Adds all the entities that are on the map to KB.
	 * @param map - the map
	 * @return the knowledge base
	 */
	@SuppressWarnings("unchecked")
	static WorldKB createKB(WaypointMap map) {
		WorldKB ret = new WorldKB();
		Vector wps = map.getItemNodes();
		if (wps == null || wps.size() == 0) return ret;
		for (Object o : wps) {
			WaypointItem wi = (WaypointItem) o;
			EntityType et = EntityType.getEntityType(wi.getCategory(), wi.getType(), wi.getSubType());
			ret.addToKB(et, new KBEntry(wi.getNode(), et, 0, true));
		}
		return ret;
	}
	
	/**
	 * Returns the number of Entities being stored in KB.
	 * @return
	 */
	int getKBSize() {
		int size = 0;
		for (LinkedList<KBEntry> l : kb.values()) {
			size += l.size();
		}
		return size;
	}

	
	/**
	 * Updates the knowledge base with observed entities:
	 * 
	 * 1. for each entity we try to find if it is in KB
	 * 2. if it is, we update the information about it there
	 * 3. if it's not, we add it as entity not from the map (observed)
	 * 
	 * @param entities the entities that are present in the world
	 * @param currentFrame current frame number - to measure the time
	 * @param botName the name of the bot
	 * @see KBEntry
	 */
	@SuppressWarnings("unchecked")
	void updateKB(Vector entities, long currentFrame, String botName) {
		Vector<Entity> ents = (Vector<Entity>) entities;
		//for all entities that are seen around
		for (Entity e : ents) {
			EntityType et = EntityType.getEntityType(e);
			LinkedList<KBEntry> l = kb.get(et); //we get the subset of the given ent type
			LinkedList<KBEntry> toRemove = new LinkedList<KBEntry>(); //the list that will contain entries eligible to be removed.
			
			if (et.equals(EntityType.PLAYER) && e.getName().equals(botName)) continue;

			boolean foundAndUpdated = false;
			if (l != null) {
				for (KBEntry kbe : l) { //we search for the ent already in KB
					if (kbe.wp.getPosition().equals(e.getOrigin().toVector3f())) {
						//if it is exactly the same ent, we update its info
						boolean isExpected = (kbe.ert <= currentFrame);
						if (kbe.isFromMap) { //if it is respawning ent, update it
							if (e.getActive() == false && isExpected) 
								kbe.ert = currentFrame+e.getRespawnTime();
							if (e.getActive() == true && ! isExpected)
								kbe.ert = currentFrame-1;
						}
						else { //if it is nonrespawning ent, delete it from KB if not active
							if (e.getActive() == false) {
								toRemove.add(kbe);
							}
						}
						foundAndUpdated = true;
						break;
					}
				}
				l.removeAll(toRemove);
			}
			if (!foundAndUpdated) { //if we didn't find this ent in KB, it is probably new no-respawning entity
				addToKB(et, new KBEntry(new Waypoint(e.getOrigin()), et, currentFrame-1, false));
			}
		}
	}
	
	/**
	 * Reads from database all known entries that are of specified
	 * EntityType and should be active at specified frame.
	 * @param et entity type that we are interested in
	 * @param frameNumber the frame at which the entity should be 
	 * active in the world.
	 * @return the list of KBEntries that are of specified type and
	 * should be active at specified frameNumber.
	 * TODO: is respawning added !!!
	 * 
	 * @see KBEntry
	 */
	LinkedList<KBEntry> getActiveEntitiesByType(EntityType et, long frameNumber) {
		LinkedList<KBEntry> ret = new LinkedList<KBEntry>();
		LinkedList<KBEntry> part = kb.get(et);
		if (part == null) return ret;
		for (KBEntry en : part) {
			if (! en.et.equals(EntityType.UNKNOWN) && en.ert <= frameNumber && en.isFromMap) {
				ret.add(en);
			}
		}
		return ret;
	}
	
	/**
	 * Adds given KBEntry to KB with given type.
	 * @param et type
	 * @param ent entry
	 */
	private void addToKB(EntityType et, KBEntry ent) {
		LinkedList<KBEntry> l = kb.get(et);
		if (l == null) {
			l = new LinkedList<KBEntry>();
			kb.put(et, l);
		}
		l.add(ent);
	}
	
	/**
	 * Returns the list of active KB entries that are within specified range from given position
	 * @param pos the position 
	 * @param maxRange maximum range from the position pos
	 * @param currentFrame current frame at which the entries should be active
	 * @return
	 */
	LinkedList<KBEntry> getActiveEntitiesWithinTheRange(Vector3f pos, int maxRange, long currentFrame) {
		LinkedList<KBEntry> ret = new LinkedList<KBEntry>();
		for (LinkedList<KBEntry> l : kb.values()) {
			for (KBEntry e : l) {
				if (	! e.et.equals(EntityType.UNKNOWN) &&
						e.ert <= currentFrame &&
						! pickupBlacklist.contains(e) && 
						CommFun.getDistanceBetweenPositions(pos, e.wp.getPosition()) < maxRange) {
					ret.add(e);
				}
			}
		}
		return ret;
	}
	
	/**
	 * Adds the given entry to black-list
	 * @param e
	 */
	void addToBlackList(KBEntry e) {
		pickupBlacklist.add(e);
		if (pickupBlacklist.size() > PICKUP_BLACKLIST_MAX_SIZE) pickupBlacklist.pop();
	}
	

}
