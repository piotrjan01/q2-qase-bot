package piotrrr.bot.smart;

import piotrrr.bot.common.jobs.DebugTalk;

public class SmartBot extends MyBot {

	public SmartBot(String botName, String skinName) {
		super(botName, skinName);
		addBotJob(new DebugTalk(this, 100));
	}

	@Override
	public void botLogic() {
		super.botLogic();
	}

}
