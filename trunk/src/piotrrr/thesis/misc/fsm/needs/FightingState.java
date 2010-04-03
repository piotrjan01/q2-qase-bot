package piotrrr.thesis.misc.fsm.needs;


import piotrrr.thesis.bots.botbase.BotBase;
import piotrrr.thesis.misc.entities.EntityType;
import piotrrr.thesis.misc.entities.EntityTypeDoublePair;

public class FightingState implements State {
	
	BotBase bot;
	
	public FightingState(BotBase bot) {
		this.bot = bot;
	}

	@Override
	public EntityTypeDoublePair []  getDesiredEntities() {
		EntityTypeDoublePair []   ret = {
				new EntityTypeDoublePair(EntityType.PLAYER, 1.0),
				new EntityTypeDoublePair(EntityType.HEALTH, 0.4),
				new EntityTypeDoublePair(EntityType.ARMOR, 0.4),
				new EntityTypeDoublePair(EntityType.WEAPON, 0.4),
				new EntityTypeDoublePair(EntityType.AMMO, 0.4),
		};
		return ret;
	}

	@Override
	public State getNextState() {
		float wellness = NeedsFSM.getBotWellness(bot);
		float firepower = NeedsFSM.getBotFirePower(bot);
		if (wellness <= NeedsFSM.BAD_WELLNESS) return new HealingState(bot);
		if (firepower <= NeedsFSM.BAD_FIRE_POWER) return new ArmingState(bot);
		return this;
	}
	
}
