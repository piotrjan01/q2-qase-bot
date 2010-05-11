package piotrrr.thesis.bots.simplebot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import piotrrr.thesis.common.CommFun;
import piotrrr.thesis.common.entities.EntityType;
import piotrrr.thesis.common.navigation.EdgeFailure;
import piotrrr.thesis.tools.Dbg;
import soc.qase.ai.waypoint.Waypoint;
import soc.qase.ai.waypoint.WaypointMap;
import soc.qase.state.Entity;
import soc.qase.tools.vecmath.Vector3f;

/**
 * @author Piotr Gwizda³a
 */
public class WorldKB {
	
	/**
	 * Maximum amount of attempts to pickup an item, before
	 * adding it to pickupFailures list.
	 */
	public static final int MAX_PICKUP_FAILURE_COUNT = 1;
	
	/**
	 * Maximum amount of failures while trying to move from one node
	 * to another that is tolerated before the edge from the map is deleted.
	 */
	public static final int MAX_WP_FAILURE_COUNT = 1;
	
	/**
	 * Maximum amount of entities in the world. Taken from QASE source, expected
	 * to work ok.
	 */
	public static final int ENTS_MAX_COUNT = 1024;
	
	/**
	 * Counts pickup failures for each entity in the world.
	 */
	int [] pickupFailures = new int [ENTS_MAX_COUNT];
	
	/**
	 * Counts waypoints edge failures.
	 */
	int [][] waypointEdgesFailures;
	

	/**
	 * Bot that owns this KB
	 */
	SimpleBot bot = null;
	
	/**
	 * The map that is used to navigate.
	 */
	WaypointMap map = null;
	
	/**
	 * The black list of pickup things. If the bot decides to pickup something, 
	 * he shouldn't repeat this decision for some time, in case the thing can not be
	 * picked up. 
	 */
	LinkedList<Entity> targetBlacklist;
	
	/**
	 * Stores information on the enemies
	 */
	HashMap<Integer, EnemyInfo> enemyInformation = new HashMap<Integer, EnemyInfo>();
	
	/**
	 * The maximum size of pickupBlaclist
	 */
	static final int TARGET_BLACKLIST_MAX_SIZE = 15;
	
	private WorldKB(WaypointMap map, SimpleBot bot) {
		targetBlacklist = new LinkedList<Entity>();
		this.map = map;
		this.bot = bot;
		waypointEdgesFailures = new int [map.getAllNodes().length][map.getAllNodes().length];
	}
	
	/**
	 * Adds all the entities that are on the map to KB.
	 * @param map - the map
	 * @return the knowledge base
	 */
	@SuppressWarnings("unchecked")
	static WorldKB createKB(String mapPath, SimpleBot bot) {
		WaypointMap map = WaypointMap.loadMap(mapPath);
		assert map != null;
		Dbg.prn("Map path: "+mapPath);
		WorldKB ret = new WorldKB(map, bot);
		return ret;
	}
	
	/**
	 * Returns the number of Entities being stored in KB.
	 * @return
	 */
	int getKBSize() {
		return getAllItems().size();
	}

	/**
	 * Returns all active entities of the specified type
	 * @param et entity type
	 * @param frameNumber the frame number at which those entities should be active.
	 * @return
	 */
	public Vector<Entity> getActiveEntitiesByType(EntityType et, long frameNumber) {
		Vector<Entity> ret = new Vector<Entity>();
		for (Object o : bot.getWorld().getEntities(true)) {
			Entity e = (Entity)o;
			EntityType itType = EntityType.getEntityType(e);
			if ( ! et.equals(itType) ) continue;
			if (e.isPlayerEntity() && e.getName().equals(bot.getBotName())) continue;
			if (targetBlacklist.contains(e)) continue;
			if (getPickupFailureCount(e) > MAX_PICKUP_FAILURE_COUNT) continue;
			ret.add(e);
		}
		return ret;
	}
	
	
	/**
	 * @param pos position of reference
	 * @param maxRange the maximal range from position of reference
	 * @param currentFrame the frame at which the entity should be active
	 * @return the list of entities that are active and within specified range from given position
	 */
	public Vector<Entity> getActiveEntitiesWithinTheRange(Vector3f pos, int maxRange, long currentFrame) {			
		Vector<Entity> ret = new Vector<Entity>();
		for (Object o : bot.getWorld().getEntities(true)) {
			Entity e = (Entity)o;
			if ( ! isPickableType(e)) continue;
			if (targetBlacklist.contains(e)) continue;
			if (getPickupFailureCount(e) > MAX_PICKUP_FAILURE_COUNT) continue;
			double dist = CommFun.getDistanceBetweenPositions(pos, e.getPosition());
			if (dist > maxRange) continue;
			ret.add(e);
		}
		return ret;
		
	}
	
	/**
	 * @return all the items known to be in the world (active and inactive).
	 */
	public Vector<Entity> getAllItems() {
		Vector<Entity> items = new Vector<Entity>();
		items.addAll(bot.getWorld().getWeapons(false));
		items.addAll(bot.getWorld().getItems(false));
		return items;
	}
	
	/**
	 * @return all the visible waypoints from current bot's position.
	 */
	public Vector<Waypoint> getAllVisibleWaypoints() {
		Vector<Waypoint> ret = new Vector<Waypoint>();
		Waypoint [] wps = map.getAllNodes();
		for (Waypoint wp : wps) {
			if (bot.getBsp().isVisible(bot.getBotPosition(), wp.getPosition())) {
				ret.add(wp);
			}
		}
		return ret;
	}
	
	/**
	 * @return all visible entities for the bot.
	 */
	public Vector<Entity> getAllVisibleEntities() {
		Vector<Entity> ret = new Vector<Entity>();
		for (Object o : bot.getWorld().getEntities(false)) {
			Entity e = (Entity)o;
			if ( ! bot.getBsp().isVisible(bot.getBotPosition(), e.getPosition())) continue;
			ret.add(e);
		}
		return ret;
	}
	
	
	/**
	 * Adds the given entry to black-list
	 * @param e
	 */
	void addToBlackList(Entity ent) {
		targetBlacklist.add(ent);
		if (targetBlacklist.size() > TARGET_BLACKLIST_MAX_SIZE) targetBlacklist.pop();
		return;
	}
	
	/**
	 * @param from the origin position
	 * @param to the destination position
	 * @return the shortest path using bot's map from origin to destination
	 */
	public Waypoint [] findShortestPath(Vector3f from, Vector3f to) {
		return map.findShortestPath(from, to);
	}
	
	/**
	 * @return a random item from the world (active or not)
	 */
	Entity getRandomItem() {
		Random r = new Random();
		Vector<Entity> items = getAllItems();
		int ind = ((r.nextInt() % items.size()) + items.size()) % items.size();
		return items.elementAt(ind);
	}
	
	/**
	 * Marks given entity as failed to be picked up. Increments the failure counter.
	 * @param e
	 */
	void failedToPickup(Entity e) {
		pickupFailures[e.getNumber()] ++ ;
	}
	
	/**
	 * Decrements the pickup failure counter for the given entity
	 * @param e
	 */
	void decPickupFailure(Entity e) {
		pickupFailures[e.getNumber()] -- ;
	}
	
	/**
	 * Returns the failure count for the specified item
	 * @param e
	 * @return
	 */
	private int getPickupFailureCount(Entity e) {
		return pickupFailures[e.getNumber()];
	}
	
	/**
	 * @return all entities that were failed to be picked up at least once
	 */
	public Vector<EntityDoublePair> getAllEntsWithPickupFailure() {
		Vector<EntityDoublePair> ret = new Vector<EntityDoublePair>();
		for (int i=0; i<ENTS_MAX_COUNT; i++) {
			if (pickupFailures[i] != 0) ret.add(new EntityDoublePair(bot.getWorld().getEntity(i), pickupFailures[i]));
		}
		return ret;
	}
	
	/**
	 * Returns all edges that have failed on the map.
	 * @return
	 */
	public Vector<EdgeFailure> getAllEdgeFailures() {
		Vector<EdgeFailure> ret = new Vector<EdgeFailure>();
		for (int i=0; i<waypointEdgesFailures.length; i++) {
			for (int j=0; j<waypointEdgesFailures[0].length; j++) {
				if (waypointEdgesFailures[i][j] != 0) ret.add(new EdgeFailure(map.getNode(i), map.getNode(j), waypointEdgesFailures[i][j]));
			}
		}
		return ret;
	}
	
	/**
	 * Removes all the failing edges from the map.
	 */
	public void removeFailingEdgesFromTheMap() {
		map.unlockMap();
		Vector<EdgeFailure> fails = getAllEdgeFailures();
		for (EdgeFailure f : fails) {
			if (f.failCount > MAX_WP_FAILURE_COUNT) {
				int src = map.indexOf(f.src);
				int dst = map.indexOf(f.dst);
				waypointEdgesFailures[src][dst] = 0;
				map.getEdgeMatrix();
				boolean result = f.src.removeEdge(f.dst);
				assert result == true;
			}
		}
		map.lockMap();
	}
	
	/**
	 * Checks whether the entity has a pickable type. 
	 * @param e
	 * @return
	 */
	private boolean isPickableType(Entity e) {
		if ( e.getCategory().equalsIgnoreCase(Entity.CAT_ITEMS) ||
				e.getCategory().equalsIgnoreCase(Entity.CAT_WEAPONS) 
		   ) return true;
		return false;
	}
	
	/**
	 * Updates the information on the enemies in the world
	 */
	public void updateEnemyInformation() {
		Vector<Integer> toDelete = new Vector<Integer>(); 
		Vector enems = bot.getWorld().getOpponents(true);
		for (Object o : enems) {
			Entity e = (Entity)o;
			if (enemyInformation.containsKey(e.getNumber())) {
				EnemyInfo ei = enemyInformation.get(e.getNumber());
				ei.updateEnemyInfo(e, bot.getFrameNumber());
			}
			else enemyInformation.put(e.getNumber(), new EnemyInfo(e, bot.getFrameNumber()));
		}
		for (EnemyInfo ei : enemyInformation.values()) {
			if ( ! ei.ent.getActive() || ei.isOutdated(bot.getFrameNumber())) 
				toDelete.add(ei.ent.getNumber());
		}
		for (Integer i : toDelete)
			enemyInformation.remove(i);
		
	}
	
	/**
	 * @return all known information about enemies
	 */
	public Vector<EnemyInfo> getAllEnemyInformation() {
		Vector<EnemyInfo> ret = new Vector<EnemyInfo>();
		ret.addAll(enemyInformation.values());
		return ret;
	}
	

}
