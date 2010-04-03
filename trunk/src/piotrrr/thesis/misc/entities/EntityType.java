package piotrrr.thesis.misc.entities;

import soc.qase.state.Entity;

public enum EntityType {
	WEAPON, HEALTH, ARMOR, PLAYER, AMMO;
	
	
	public static EntityType getEntityType(Entity entity) {
		if (entity.getCategory().equals(Entity.CAT_WEAPONS)) return EntityType.WEAPON;
		
		if (entity.getType().equals(Entity.TYPE_HEALTH)
				|| entity.getType().equals(Entity.TYPE_INVULNERABILITY) || entity
				.getType().equals(Entity.TYPE_MEGAHEALTH)) return EntityType.HEALTH;
		
		if (entity.getType().equals(Entity.TYPE_ARMOR)) return EntityType.ARMOR;
		
		if ((entity.getType().equals(Entity.TYPE_AMMO) || entity
						.getType().equals(Entity.TYPE_AMMOPACK))) return EntityType.AMMO;
		
		if (entity.getCategory().equals(Entity.CAT_PLAYERS)) return EntityType.PLAYER;
		
		return null;
	}
	
}

