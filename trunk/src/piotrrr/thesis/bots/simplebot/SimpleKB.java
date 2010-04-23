package piotrrr.thesis.bots.simplebot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

import piotrrr.thesis.common.entities.EntityType;
import soc.qase.ai.waypoint.WaypointItem;
import soc.qase.ai.waypoint.WaypointMap;
import soc.qase.state.Entity;

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
	
	private SimpleKB() {
		kb = new HashMap<EntityType, LinkedList<KBEntry>>();
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
	void updateKB(Vector entities, long currentFrame) {
		//FIXME: more optimal searching
		Vector<Entity> ents = (Vector<Entity>) entities;
		for (Entity e : ents) {
			EntityType et = EntityType.getEntityType(e);
			LinkedList<KBEntry> l = kb.get(et);
			if (l == null) {
//				Dbg.prn("This entity is not in KB:");
//				Dbg.prn("nr: "+e.getNumber()+" type: "+et.toString());
				continue;
			}
			for (KBEntry kbe : l) {
				//If it is the same entity and KBEntry
				if (kbe.wp.getPosition().equals(e.getOrigin().toVector3f())) {
					boolean isExpected = (kbe.ert <= currentFrame);
					if (e.getActive() == false && isExpected) 
						kbe.ert = currentFrame+e.getRespawnTime();
					if (e.getActive() == true && ! isExpected)
						kbe.ert = currentFrame-1;
					break;
				}
			}
		}
	}
	
	/**
	 * Reads from database all known entries that are of specified
	 * EntityType and will should be active at specified frame.
	 * @param et entity type that we are interested in
	 * @param frameNumber the frame at which the entity should be 
	 * active in the world.
	 * @return the list of KBEntries that are of specified type and
	 * should be active at specified frameNumber.
	 * 
	 * @see KBEntry
	 */
	LinkedList<KBEntry> getActiveEntitiesByType(EntityType et, long frameNumber) {
		LinkedList<KBEntry> ret = new LinkedList<KBEntry>();
		LinkedList<KBEntry> part = kb.get(et);
		if (part == null) return ret;
		for (KBEntry en : part) {
			if (en.ert <= frameNumber) {
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
	

}
