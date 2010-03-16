package piotrrr.bot.common.jobs;

import piotrrr.bot.smart.MyBot;

/**
 * Dummy job, that repeats a phrase with given period.
 * @author Piotr Gwizda³a
 */
public class DebugTalk extends Job {
	
	long lastFrame;
	
	int period;

	public DebugTalk(MyBot bot, int period) {
		super(bot);
		this.period = period;
		lastFrame = bot.getFrameNumber();
	}
	
	@Override
	public void run() {
		if (bot.getFrameNumber() - lastFrame < period ) return;
		lastFrame = bot.getFrameNumber();
		bot.consoleCommand("say \"I'm alive!\"");
	}

}
