package piotrrr.bot.misc.jobs;

import piotrrr.bot.misc.GenericBot;

/**
 * The Job is a job, usually run periodically by the bot.
 * @author Piotr Gwizda³a
 *
 */
public abstract class Job extends Thread {
	
	/**
	 * The bot that will run the job.
	 */
	GenericBot bot;
	
	public Job(GenericBot bot) {
		this.bot = bot;
	}
	
	public void run() {
		
	}
	
}
