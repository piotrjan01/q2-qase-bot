package piotrrr.thesis.bots.smartbot;

import piotrrr.thesis.bots.botbase.BotBase;
import piotrrr.thesis.common.jobs.GeneralDebugTalk;

/**
 * This is one of the versions of the bots.
 * @author Piotr Gwizda³a
 */
public class SmartBot extends BotBase {

	public SmartBot(String botName, String skinName) {
		super(botName, skinName);
		addBotJob(new GeneralDebugTalk(this, 100));
	}

	@Override
	public void botLogic() {
		super.botLogic();
	}

}
