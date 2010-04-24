package piotrrr.thesis.common.jobs;

import piotrrr.thesis.bots.botbase.BotBase;
import piotrrr.thesis.bots.simplebot.SimpleBot;
import piotrrr.thesis.tools.Dbg;

/**
 * Dummy job, that repeats some debug info with given period.
 * @author Piotr Gwizda³a
 */
public class DebugTalk extends Job {
	
	long lastFrame;
	
	int period;
	
	String toSay = "";

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
		
		String say = toSay+"  H="+h+" A="+a+" St="+st;
		bot.say(say);
		toSay = "";
		
	}
	
	public void addToLog(String s) {
		Dbg.prn("BOT SAYS: "+s);
		toSay += s+" :: ";
	}

}
