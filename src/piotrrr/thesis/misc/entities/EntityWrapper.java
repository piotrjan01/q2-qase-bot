package piotrrr.thesis.misc.entities;

import soc.qase.state.Entity;

public class EntityWrapper {

	Entity entity;

	public EntityWrapper(Entity e) {
		entity = e;
	}
	
	

	@Override
	public String toString() {
		return "cat = " + entity.getCategory() + "\ntype = " + entity.getType()
				+ "\nsubt = " + entity.getSubType();
	}
	
	/**
	 * Convenience method.
	 * @return
	 */
	public EntityType getType() {
		return EntityType.getEntityType(entity);
	}


}
