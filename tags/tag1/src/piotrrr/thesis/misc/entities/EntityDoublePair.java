package piotrrr.thesis.misc.entities;

import soc.qase.state.Entity;

/**
 * Extends EntityWrapper with additional field: double precision number.
 * @author Piotr Gwizda³a
 */
public class EntityDoublePair extends EntityWrapper {

	/**
	 * Floating point number of double precision.
	 */
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
