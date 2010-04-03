package piotrrr.thesis.knowledge;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

import piotrrr.thesis.misc.entities.EntityType;
import soc.qase.ai.waypoint.WaypointItem;
import soc.qase.ai.waypoint.WaypointMap;

public class SimpleKB {
	
	HashMap<EntityType, LinkedList<KBEntry>> kb;
	
	private SimpleKB() {
		kb = new HashMap<EntityType, LinkedList<KBEntry>>();
	}
	
	@SuppressWarnings("unchecked")
	public static SimpleKB createKB(WaypointMap map) {
		SimpleKB ret = new SimpleKB();
		Vector wps = map.getItemNodes();
		if (wps == null || wps.size() == 0) return ret;
		for (Object o : wps) {
			WaypointItem wi = (WaypointItem) o;
			EntityType et = EntityType.getEntityType(wi.getCategory(), wi.getType(), wi.getSubType());
			LinkedList<KBEntry> list = ret.kb.get(et);
			if (list == null) {
				list = new LinkedList<KBEntry>();
				ret.kb.put(et, list);
			}
			else {
				list.add(new KBEntry(wi.getNode(), et, 0));
			}
		}
		return ret;
	}
	
	public int getKBSize() {
		int size = 0;
		for (LinkedList<KBEntry> l : kb.values()) {
			size += l.size();
		}
		return size;
	}

}
