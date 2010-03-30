package piotrrr.bot.misc;

import soc.qase.state.Entity;

public class EntityWrapper {

	Entity entity;

	String explicitType;
	
	/**
	 * Some additional floating number that can be used on various occasions.
	 * For example as preference when State Machine is telling which 
	 * entity types are needed. 
	 */
	double fNum;

	public EntityWrapper(Entity e) {
		entity = e;
		explicitType = "";
	}

	public EntityWrapper(String explicitType) {
		this.explicitType = explicitType;
		entity = null;
	}
	
	public EntityWrapper(Entity e, double f) {
		this(e);
		fNum = f;
	}
	
	public EntityWrapper(String e, double f) {
		this(e);
		fNum = f;
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
