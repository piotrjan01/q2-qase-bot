package piotrrr.thesis.common.jobs;

import piotrrr.thesis.bots.referencebot.ReferenceBot;

/**
 * This job reports bot's state changes.
 * @author Piotr Gwizda³a
 */
public class StateReporter extends Job {
	
	/**
	 * Remembers last state's name. Helps to find out state changes. 
	 */
	String lastStateName = "";
	
	ReferenceBot bot;
	
	public boolean stateHasChanged = false;
	
	public StateReporter(ReferenceBot bot) {
		super(bot);
		this.bot = bot;
		lastStateName = bot.getCurrentStateName();
	}
	
	@Override
	public void run() {
		String stateName = bot.getCurrentStateName();
		if (stateName.equals(lastStateName)) {
			stateHasChanged = false;
			return;
		}
		
		stateHasChanged = true;
		
		bot.say("State changed: "+lastStateName+" -> "+stateName);
		lastStateName = stateName;
	}

}
