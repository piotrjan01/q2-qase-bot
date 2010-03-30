package piotrrr.bot.misc.jobs;

import piotrrr.bot.misc.GenericBot;

/**
 * Used to call some methods, when the application is interrupted by 
 * for instance CTR+C
 * @author Piotr Gwizda³a
 */
public class ShutdownJob extends Job {

	public ShutdownJob(GenericBot bot) {
		super(bot);
	}
	
	@Override
	public void run() {
		bot.disconnect();
	}

}
