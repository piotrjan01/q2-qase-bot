package piotrrr.bot.smartbot;

import piotrrr.bot.base.BotBase;
import piotrrr.bot.misc.jobs.DebugTalk;

public class SmartBot extends BotBase {

	public SmartBot(String botName, String skinName) {
		super(botName, skinName);
		addBotJob(new DebugTalk(this, 100));
	}

	@Override
	public void botLogic() {
		super.botLogic();
	}

}
