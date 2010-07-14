package piotrrr.thesis.misc.fsm.needs;

import piotrrr.thesis.bots.botbase.BotBase;
import piotrrr.thesis.misc.entities.EntityType;
import piotrrr.thesis.misc.entities.EntityTypeDoublePair;

/**
 * This state should be used by bot when it searches from guns and ammo.
 * @author Piotr Gwizda�a
 */
public class ArmingState extends State {

	BotBase bot;

	public ArmingState(BotBase bot) {
		this.bot = bot;
	}

	@Override
	public EntityTypeDoublePair[] getDesiredEntities() {
		EntityTypeDoublePair[] ret = { 
				new EntityTypeDoublePair(EntityType.WEAPON, 0.9),
				new EntityTypeDoublePair(EntityType.AMMO, 0.7),
				new EntityTypeDoublePair(EntityType.HEALTH, 0.4),
				new EntityTypeDoublePair(EntityType.ARMOR, 0.4)
		};
		return ret;
	}

	@Override
	public State getNextState() {
		float wellness = NeedsFSM.getBotWellness(bot);
		float firepower = NeedsFSM.getBotFirePower(bot);
		if (wellness <= NeedsFSM.BAD_WELLNESS) return new HealingState(bot);
		if (firepower >= NeedsFSM.GOOD_FIRE_POWER) return new FightingState(bot);
		return this;
	}

}