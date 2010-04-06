package piotrrr.thesis.common.entities;

import soc.qase.state.Entity;

/**
 * Wraps the Entity class from QASE with some additional functionality.
 * @author Piotr Gwizda³a
 * 
 * TODO: unused class?
 * 
 */
public class EntityWrapper {

	/**
	 * The entity from QASE.
	 */
	Entity entity;

	public EntityWrapper(Entity e) {
		entity = e;
	}
	
	

	/**
	 * Returns string representation of entity.
	 */
	@Override
	public String toString() {
		return "cat = " + entity.getCategory() + "\ntype = " + entity.getType()
				+ "\nsubt = " + entity.getSubType();
	}
	
	/**
	 * Convenience method. 
	 * @return The EntityType of this entity.
	 * @see EntityType
	 */
	public EntityType getType() {
		return EntityType.getEntityType(entity);
	}


}
