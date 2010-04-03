package piotrrr.misc.jobs;

import piotrrr.bot.botbase.BotBase;
import piotrrr.bot.simplebot.SimpleBot;

/**
 * Dummy job, that repeats a phrase with given period.
 * @author Piotr Gwizda³a
 */
public class DebugTalk extends Job {
	
	long lastFrame;
	
	int period;

	public DebugTalk(BotBase bot, int period) {
		super(bot);
		this.period = period;
		lastFrame = bot.getFrameNumber();
	}
	
	@Override
	public void run() {
		if (bot.getFrameNumber() - lastFrame < period ) return;
		lastFrame = bot.getFrameNumber();
		float h = bot.getBotHealth();
		float a = bot.getBotArmor();
		String st = ((SimpleBot)bot).getCurrentStateName();
		bot.say("I'm alive! H="+h+" A="+a+" St="+st);
		
	}

}
