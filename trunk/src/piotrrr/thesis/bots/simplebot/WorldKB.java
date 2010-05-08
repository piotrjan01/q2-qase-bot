package piotrrr.thesis.bots.simplebot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
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
 * @author Piotr Gwizda³a
 */
public class WorldKB {
	

	/**
	 * The map that is used to navigate.
	 */
	WaypointMap map = null;
	
	Vector<WaypointItem> items = new Vector<WaypointItem>();
	
	/**
	 * The black list of pickup things. If the bot decides to pickup something, 
	 * he shouldn't repeat this decision for some time, in case the thing can not be
	 * picked up. 
	 */
	LinkedList<Waypoint> targetBlacklist;
	
	/**
	 * The maximum size of pickupBlaclist
	 */
	static final int TARGET_BLACKLIST_MAX_SIZE = 10;
	
	private WorldKB(WaypointMap map) {
		targetBlacklist = new LinkedList<Waypoint>();
		this.map = map;
	}
	
	/**
	 * Adds all the entities that are on the map to KB.
	 * @param map - the map
	 * @return the knowledge base
	 */
	@SuppressWarnings("unchecked")
	static WorldKB createKB(String mapPath) {
		WaypointMap map = WaypointMap.loadMap(mapPath);
		Dbg.prn("Map path: "+mapPath);
		WorldKB ret = new WorldKB(map);
		Vector wps = map.getItemNodes();
		if (wps == null || wps.size() == 0) return ret;
		for (Object o : wps) {
			WaypointItem wi = (WaypointItem) o;
			ret.items.add(wi);
		}
		return ret;
	}
	
	/**
	 * Returns the number of Entities being stored in KB.
	 * @return
	 */
	int getKBSize() {
		return items.size();
	}

	
	/**
	 * Updates the knowledge base with observed entities
	 * 
	 * @param entities the entities that are present in the world
	 * @param currentFrame current frame number - to measure the time
	 * @param botName the name of the bot
	 * @see KBEntry
	 */
	@SuppressWarnings("unchecked")
	void updateKB(Vector entities, long currentFrame, String botName) {
		Dbg.prn("KB: ents size: "+entities.size());
		for (Object o : entities) {
			Entity e = (Entity)o;
			//don't bother with yourself :)
			if (e.isPlayerEntity() && e.getName() == botName) continue;
			//we just bother with items and weapons
			if (! e.getCategory().equalsIgnoreCase(Entity.CAT_WEAPONS) &&
					! e.getCategory().equalsIgnoreCase(Entity.CAT_ITEMS)) {
//				Dbg.prn("KB: not interesting category: "+e.toDetailedString());
				continue;
			}
			
			WaypointItem kbi = findInItems(e);
			//a new item in the world - we assume it doesn't respawn
			if (kbi == null) {
//				Dbg.prn("KB: item not in KB:\n "+e.toDetailedString());
				//we establish respawn frame for case of active and inactive entity
				long respawnFrame = currentFrame-1;
				if (e.getActive() == false) respawnFrame += e.getRespawnTime();
				//we add the new entity to items KB
				kbi = new WaypointItem(new Waypoint(e.getOrigin()), e);
				kbi.setFromMapGeneration(false);
				kbi.setRespawnFrame(respawnFrame);
				items.add(kbi);
			}
			//the item is already in the KB, we update it's info
			else {
//				Dbg.prn("KB: item already in KB: "+e.toDetailedString());
				//if it's active
				if (e.getActive() == true) {
					//it's there, so we make sure we have it in our KB as active
					kbi.setRespawnFrame(0); 	
				}
				//if it's not active
				else {
					//If we expected it...
					if (kbi.getRespawnFrame() <= currentFrame) {
						//we increment respawn fail counter...
						kbi.setRespawnFrame(currentFrame+e.getRespawnTime());
						kbi.incRespawnFailCounter();
						//if it failed too much, we delete it..
						if (kbi.getRespawnFailCounter() > WaypointItem.maximalRespawnFailCount)
							items.remove(kbi);
					}
					
				}
			}
			
		}
		
		return;
	}
	
	private WaypointItem findInItems(Entity e) {
		for (WaypointItem i : items) {
			if (i.getNode().getPosition().equals(e.getOrigin().toVector3f())) return i;
		}
		return null;
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
	public Vector<WaypointItem> getActiveEntitiesByType(EntityType et, long frameNumber) {
		
		Vector<WaypointItem> ret = new Vector<WaypointItem>();
		for (WaypointItem it : items) {
			EntityType itType = EntityType.getEntityType(it);
			if ( ! et.equals(itType) ||
				it.getRespawnFrame() > frameNumber	|| 
				targetBlacklist.contains(it.getNode())) continue;
			ret.add(it);
		}
		return ret;
	}
	
	
	/**
	 * Returns the list of active KB entries that are within specified range from given position
	 * @param pos the position 
	 * @param maxRange maximum range from the position pos
	 * @param currentFrame current frame at which the entries should be active
	 * @return
	 */
	public Vector<WaypointItem> getActiveEntitiesWithinTheRange(Vector3f pos, int maxRange, long currentFrame) {
		Vector<WaypointItem> ret = new Vector<WaypointItem>();
		for (WaypointItem it : items) {
			double dist = CommFun.getDistanceBetweenPositions(pos, it.getNode().getPosition());
			if ( dist > maxRange ||
				it.getRespawnFrame() > currentFrame	|| 
				targetBlacklist.contains(it.getNode())) continue;
			ret.add(it);
		}
		return ret;
	}
	
	public Vector<WaypointItem> getAllItems() {
		return items;
	}
	
	public Vector<Waypoint> getAllVisibleWaypoints(SimpleBot bot) {
		Vector<Waypoint> ret = new Vector<Waypoint>();
		Waypoint [] wps = map.getAllNodes();
		for (Waypoint wp : wps) {
			if (bot.getBsp().isVisible(bot.getBotPosition(), wp.getPosition())) {
				ret.add(wp);
			}
		}
		return ret;
	}
	
	public Vector<WaypointItem> getAllVisibleEntities(SimpleBot bot) {
		Vector<WaypointItem> ret = new Vector<WaypointItem>();
		for (WaypointItem wp : items) {
			if (bot.getBsp().isVisible(bot.getBotPosition(), wp.getPosition())) {
				ret.add(wp);
			}
		}
		return ret;
	}
	
	
	/**
	 * Adds the given entry to black-list
	 * @param e
	 */
	void addToBlackList(Waypoint wp) {
		targetBlacklist.add(wp);
		if (targetBlacklist.size() > TARGET_BLACKLIST_MAX_SIZE) targetBlacklist.pop();
		return;
	}
	
	public Waypoint [] findShortestPath(Vector3f from, Vector3f to) {
		return map.findShortestPath(from, to);
	}
	
	WaypointItem getRandomItem() {
		Random r = new Random();
		int ind = ((r.nextInt() % items.size()) + items.size()) % items.size();
		return items.elementAt(ind);
	}
	

}
