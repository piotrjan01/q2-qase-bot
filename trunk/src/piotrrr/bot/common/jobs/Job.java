package piotrrr.bot.misc.jobs;

import piotrrr.bot.misc.MyBot;

/**
 * The Job is a job, usually run periodically by the bot.
 * @author Piotr Gwizda³a
 *
 */
public abstract class Job extends Thread {
	
	/**
	 * The bot that will run the job.
	 */
	MyBot bot;
	
	public Job(MyBot bot) {
		this.bot = bot;
	}
	
	public void run() {
		
	}
	
}
