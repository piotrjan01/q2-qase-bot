package piotrrr.thesis.misc.jobs;

import piotrrr.thesis.bots.botbase.BotBase;

/**
 * The Job is a job, usually run periodically by the bot.
 * @author Piotr Gwizda³a
 *
 */
public abstract class Job extends Thread {
	
	/**
	 * The bot that will run the job.
	 */
	BotBase bot;
	
	public Job(BotBase bot) {
		this.bot = bot;
	}
	
	public void run() {
		
	}
	
}
