package piotrrr.thesis.misc.entities;

import soc.qase.state.Entity;

public class EntityDoublePair extends EntityWrapper {

	public double dnum = 0d;
	
	public EntityDoublePair(Entity e) {
		super(e);
		// TODO Auto-generated constructor stub
	}
	
	public EntityDoublePair(Entity e, double num) {
		super(e);
		dnum = num;
	}

}
