package piotrrr.thesis.bots.simplebot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

import piotrrr.thesis.common.CommFun;
import piotrrr.thesis.common.entities.EntityType;
import piotrrr.thesis.tools.Dbg;
import soc.qase.ai.waypoint.Waypoint;
import soc.qase.ai.waypoint.WaypointItem;
import soc.qase.ai.waypoint.WaypointMap;
import soc.qase.state.Entity;
import soc.qase.tools.vecmath.Vector3f;

/**
 * The simple knowledge base being built basing on the WaypointMap
 * and being updated basing on seen entities list.
 * @author Piotr Gwizda³a
 */
class SimpleKB {
	
	/**
	 * The map containing Knowledge base entries organized by 
	 * entity types.
	 */
	HashMap<EntityType, LinkedList<KBEntry>> kb;
	
	LinkedList<KBEntry> pickupBlacklist;
	
	static final int BLACKLIST_MAX_SIZE = 10;
	
	private SimpleKB() {
		kb = new HashMap<EntityType, LinkedList<KBEntry>>();
		pickupBlacklist = new LinkedList<KBEntry>();
	}
	
	/**
	 * Creates the KB basing on the given map.
	 * @param map the WaypointMap, which's WaypointItems are used to
	 * collect data on items on the map.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static SimpleKB createKB(WaypointMap map) {
		SimpleKB ret = new SimpleKB();
		Vector wps = map.getItemNodes();
		if (wps == null || wps.size() == 0) return ret;
		for (Object o : wps) {
			WaypointItem wi = (WaypointItem) o;
			EntityType et = EntityType.getEntityType(wi.getCategory(), wi.getType(), wi.getSubType());
			ret.addToKB(et, new KBEntry(wi.getNode(), et, 0));
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
	 * Updates the knowledge base:
	 * 1. If entity is not active
	 * 1.1. if we expected it to be there (ert < current)
	 * we set its ert to current + respawn time (pesimistic).
	 * 1.2. if we didn't expect it to be there, we continue.
	 * 2. If the entity is active
	 * 2.1. if we expected it to be there (ert < current), we continue.
	 * 2.2. if we didn't expect it to be there, we set ert to current-1.
	 * @param entities
	 * @param currentFrame
	 * @see KBEntry
	 */
	@SuppressWarnings("unchecked")
	void updateKB(Vector entities, long currentFrame, String botName) {
		//FIXME: more optimal searching
		Vector<Entity> ents = (Vector<Entity>) entities;
		//for all entities that are seen around
		LinkedList<KBEntry> toRemove = new LinkedList<KBEntry>();
		for (Entity e : ents) {
			EntityType et = EntityType.getEntityType(e);
			LinkedList<KBEntry> l = kb.get(et); //we get the subset of the given ent type
			
			
			if (et.equals(EntityType.PLAYER) && e.getName().equals(botName)) {
//				Dbg.prn("Tried to add himself to KB ;)");
				continue;
			}
			
			boolean updated = false;
			if (l != null) {
				for (KBEntry kbe : l) { //we search for the ent already in KB
					if (kbe.wp.getPosition().equals(e.getOrigin().toVector3f())) {
						//if it is exactly the same ent, we update its info
						boolean isExpected = (kbe.ert <= currentFrame);
						if (kbe.isRespawningEntity) { //if it is respawning ent, update it
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
						updated = true;
						break;
					}
				}
				l.removeAll(toRemove);
			}
			

			if (!updated) { //if we didn't find this ent in KB, it is probably new no-respawning entity
				addToKB(et, new KBEntry(new Waypoint(e.getOrigin()), et, currentFrame-1, false));
			}
		}
//		Dbg.prn("KB size: "+getKBSize()+" blacklist: "+pickupBlacklist.size()+" deleted: "+toRemove.size());
	}
	
	/**
	 * Reads from database all known entries that are of specified
	 * EntityType and will should be active at specified frame.
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
			if (! en.et.equals(EntityType.UNKNOWN) && en.ert <= frameNumber && en.isRespawningEntity) {
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
	
	LinkedList<KBEntry> getActiveEntitiesWithinTheRange(Vector3f pos, int maxRange, long currentFrame) {
		LinkedList<KBEntry> ret = new LinkedList<KBEntry>();
		for (LinkedList<KBEntry> l : kb.values()) {
			for (KBEntry e : l) {
				if (	! e.et.equals(EntityType.UNKNOWN) &&
						e.ert <= currentFrame && 
						CommFun.getDistanceBetweenPositions(pos, e.wp.getPosition()) < maxRange &&
						! pickupBlacklist.contains(e)) {
					ret.add(e);
				}
			}
		}
		return ret;
	}
	
	void addToBlackList(KBEntry e) {
		pickupBlacklist.add(e);
		if (pickupBlacklist.size() > BLACKLIST_MAX_SIZE) pickupBlacklist.pop();
	}
	

}
