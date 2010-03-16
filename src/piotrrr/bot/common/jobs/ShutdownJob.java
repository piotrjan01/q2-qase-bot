package piotrrr.bot.common.jobs;

import piotrrr.bot.smart.MyBot;

/**
 * Used to call some methods, when the application is interrupted by 
 * for instance CTR+C
 * @author Piotr Gwizda³a
 */
public class ShutdownJob extends Job {

	public ShutdownJob(MyBot bot) {
		super(bot);
	}
	
	@Override
	public void run() {
		bot.disconnect();
	}

}
