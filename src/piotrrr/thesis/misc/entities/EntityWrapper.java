package piotrrr.thesis.misc.entities;

import soc.qase.state.Entity;

public class EntityWrapper {

	Entity entity;

	String explicitType;
	
	/**
	 * Some extra field, which can store additional information that can be
	 * useful to associate it with the entity.
	 */
	Object extra;

	public EntityWrapper(Entity e) {
		entity = e;
		explicitType = "";
	}

	public EntityWrapper(String explicitType) {
		this.explicitType = explicitType;
		entity = null;
	}
	
	public EntityWrapper(Entity e, Object f) {
		this(e);
		extra = f;
	}
	
	public EntityWrapper(String e, Object f) {
		this(e);
		extra = f;
	}

	@Override
	public String toString() {
		if (entity == null)
			return "Entity of explicit type: " + explicitType;
		return "cat = " + entity.getCategory() + "\ntype = " + entity.getType()
				+ "\nsubt = " + entity.getSubType();
	}

	public boolean isWeapon() {
		if (entity != null && entity.getCategory().equals(Entity.CAT_WEAPONS))
			return true;
		if (explicitType.equals(Entity.CAT_WEAPONS)) return true;
		return false;
	}

	public boolean isHealth() {
		if (entity != null
				&& (entity.getType().equals(Entity.TYPE_HEALTH)
						|| entity.getType().equals(Entity.TYPE_INVULNERABILITY) || entity
						.getType().equals(Entity.TYPE_MEGAHEALTH)))
			return true;
		if (explicitType.equals(Entity.TYPE_HEALTH)) return true;
		return false;
	}

	public boolean isArmor() {
		if (entity != null && (entity.getType().equals(Entity.TYPE_ARMOR)))
			return true;
		if (explicitType.equals(Entity.TYPE_ARMOR)) return true;
		return false;
	}

	public boolean isAmmo() {
		if (entity != null
				&& (entity.getType().equals(Entity.TYPE_AMMO) || entity
						.getType().equals(Entity.TYPE_AMMOPACK)))
			return true;
		if (explicitType.equals(Entity.TYPE_AMMO)) return true;
		return false;
	}

	public boolean isEnemy() {
		if (entity != null && entity.getCategory().equals(Entity.CAT_PLAYERS))
			return true;
		if (explicitType.equals(Entity.CAT_PLAYERS)) return true;
		return false;
	}

}
