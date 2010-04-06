package piotrrr.thesis.common.jobs;

import piotrrr.thesis.bots.botbase.BotBase;
import piotrrr.thesis.bots.simplebot.SimpleBot;

/**
 * This job reports bot's state changes.
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
		if (stateName.equals(lastStateName)) {
			((SimpleBot)bot).setStateChanged(false);
			return;
		}
		
		((SimpleBot)bot).setStateChanged(true);
		
		bot.say("State changed: "+lastStateName+" -> "+stateName);
		lastStateName = stateName;
	}

}
