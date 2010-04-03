package piotrrr.thesis.misc.fsm.needs;

import piotrrr.thesis.bots.botbase.BotBase;
import piotrrr.thesis.misc.entities.EntityType;
import piotrrr.thesis.misc.entities.EntityTypeDoublePair;

public class HealingState implements State {
	
	BotBase bot;
	
	
	
	public HealingState(BotBase bot) {
		this.bot = bot;
	}

	@Override
	public EntityTypeDoublePair []  getDesiredEntities() {
		EntityTypeDoublePair []  ret = { 
				new EntityTypeDoublePair(EntityType.HEALTH, 0.9),
				new EntityTypeDoublePair(EntityType.ARMOR, 0.6),
				new EntityTypeDoublePair(EntityType.WEAPON, 0.2),
				new EntityTypeDoublePair(EntityType.AMMO, 0.2)
		};
		return ret;
	}

	@Override
	public State getNextState() {
		float wellness = NeedsFSM.getBotWellness(bot);
		float firepower = NeedsFSM.getBotFirePower(bot);
		if (wellness >= NeedsFSM.GOOD_WELLNESS 
				&& firepower >= NeedsFSM.GOOD_FIRE_POWER) return new FightingState(bot);
		if (wellness >= NeedsFSM.GOOD_WELLNESS) return new ArmingState(bot);
		return this;
	}
	
}
