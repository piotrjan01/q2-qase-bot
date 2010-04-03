package piotrrr.thesis.misc.jobs;

import piotrrr.thesis.bot.botbase.BotBase;
import piotrrr.thesis.bot.simplebot.SimpleBot;

/**
 * Dummy job, that repeats a phrase with given period.
 * @author Piotr Gwizda³a
 */
public class StateReporter extends Job {
	
	/**
	 * Remembers last state's name. Helps to find out state changes. 
	 */
	String lastStateName = "";
	
	public StateReporter(BotBase bot) {
		super(bot);
		lastStateName = ((SimpleBot)bot).getCurrentStateName();
	}
	
	@Override
	public void run() {
		String stateName = ((SimpleBot)bot).getCurrentStateName();
		if (stateName.equals(lastStateName)) return;
		bot.say("State changed: "+lastStateName+" -> "+stateName);
		lastStateName = stateName;
	}

}
