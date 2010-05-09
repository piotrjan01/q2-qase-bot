package piotrrr.thesis.bots.simplebot;

import piotrrr.thesis.common.GameObject;
import soc.qase.state.Entity;
import soc.qase.tools.vecmath.Vector3f;

public class EntityDoublePair implements Comparable<EntityDoublePair>, GameObject {
	
	Entity ent = null;
	
	double dbl = 0.0;
	
	public EntityDoublePair(Entity e, double dbl) {
		this.ent = e;
		this.dbl = dbl;
	}

	@Override
	public int compareTo(EntityDoublePair o) {
		if (this.dbl > o.dbl) return 1;
		if (this.dbl < o.dbl) return -1;
		return 0;
	}

	@Override
	public Vector3f getPosition() {
		return ent.getPosition();
	}

	@Override
	public String toDetailedString() {
		return "Number: "+dbl+"\n\nEntity:"+ent.toDetailedString();
	}

}
