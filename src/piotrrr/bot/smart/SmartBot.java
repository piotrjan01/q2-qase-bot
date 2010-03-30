package piotrrr.bot.smart;

import piotrrr.bot.misc.GenericBot;
import piotrrr.bot.misc.jobs.DebugTalk;

public class SmartBot extends GenericBot {

	public SmartBot(String botName, String skinName) {
		super(botName, skinName);
		addBotJob(new DebugTalk(this, 100));
	}

	@Override
	public void botLogic() {
		super.botLogic();
	}

}
