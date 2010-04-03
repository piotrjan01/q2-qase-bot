package piotrrr.thesis.bot.smartbot;

import piotrrr.thesis.bot.botbase.BotBase;
import piotrrr.thesis.misc.jobs.DebugTalk;

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
