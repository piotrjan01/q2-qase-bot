package piotrrr.thesis.knowledge;

import java.util.HashMap;
import java.util.Vector;

import piotrrr.thesis.misc.entities.EntityType;
import soc.qase.ai.waypoint.WaypointItem;
import soc.qase.ai.waypoint.WaypointMap;

public class SimpleKB {
	
	HashMap<EntityType, KBEntry> kb;
	
	private SimpleKB() {
		kb = new HashMap<EntityType, KBEntry>();
	}
	
	@SuppressWarnings("unchecked")
	public static SimpleKB createKB(WaypointMap map) {
		SimpleKB ret = new SimpleKB();
		Vector wps = map.getItemNodes();
		if (wps == null || wps.size() == 0) return ret;
		for (Object o : wps) {
			WaypointItem wi = (WaypointItem) o;
			EntityType et = EntityType.getEntityType(wi.getCategory(), wi.getType(), wi.getSubType());
			ret.kb.put(et, new KBEntry(wi.getNode(), et, 0));
		}
		return ret;
	}

}
