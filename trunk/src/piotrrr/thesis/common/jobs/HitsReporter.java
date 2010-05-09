package piotrrr.thesis.common.jobs;

import piotrrr.thesis.bots.botbase.BotBase;
import piotrrr.thesis.bots.simplebot.SimpleBot;
import piotrrr.thesis.tools.Dbg;
import soc.qase.state.Entity;

/**
 * This job reports bot's state changes.
 * @author Piotr Gwizda³a
 */
public class HitsReporter extends Job {
	
	float lastHealth;
	
	float lastArmor;
	
	public boolean stateHasChanged = false;
	
	public HitsReporter(BotBase bot) {
		super(bot);
		lastHealth = ((SimpleBot)bot).getBotHealth();
		lastArmor = ((SimpleBot)bot).getBotArmor();
	}
	
	@Override
	public void run() {
		float h = ((SimpleBot)bot).getBotHealth();
		float a = ((SimpleBot)bot).getBotArmor();
		
		if (h < lastHealth || a < lastArmor) {
			Dbg.prn(bot.getBotName()+":\t==> BOT HIT: lost h: "+(lastHealth-h)+" lost a: "+(lastArmor-a));
//			Entity botEnt = null;
//			for (Object e : bot.getWorld().getEntities(false)) {
//				botEnt = (Entity)e;
//				if ( ! botEnt.isPlayerEntity()) continue;
//				Dbg.prn(botEnt.getName()+":\t"+botEnt.getEffects().getEffectsString());
//			}
		}
		
		lastHealth = h;
		lastArmor = a;
	}

}
