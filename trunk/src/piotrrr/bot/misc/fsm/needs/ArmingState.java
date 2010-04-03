package piotrrr.bot.misc.fsm.needs;

import piotrrr.bot.misc.EntityWrapper;
import piotrrr.bot.misc.BotBase;
import soc.qase.state.Entity;

public class ArmingState implements NeedsState {

	BotBase bot;

	public ArmingState(BotBase bot) {
		this.bot = bot;
	}

	@Override
	public EntityWrapper[] getDesiredEntities() {
		EntityWrapper[] ret = { 
				new EntityWrapper(Entity.CAT_WEAPONS, 0.9),
				new EntityWrapper(Entity.TYPE_AMMO, 0.7),
				new EntityWrapper(Entity.TYPE_HEALTH, 0.4),
				new EntityWrapper(Entity.TYPE_ARMOR, 0.4)
		};
		return ret;
	}

	@Override
	public NeedsState getNextState() {
		float wellness = NeedsFSM.getBotWellness(bot);
		float firepower = NeedsFSM.getBotFirePower(bot);
		if (wellness <= NeedsFSM.BAD_WELLNESS) return new HealingState(bot);
		if (firepower >= NeedsFSM.GOOD_FIRE_POWER) return new FightingState(bot);
		return this;
	}

}