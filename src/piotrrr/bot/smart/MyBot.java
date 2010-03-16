package piotrrr.bot.smart;

import java.util.LinkedList;

import piotrrr.bot.common.jobs.Job;

import soc.qase.bot.ObserverBot;
import soc.qase.state.World;
/**
 * 
 * @author Piotr Gwizda³a
 *
 */
public class MyBot extends ObserverBot {
	
	/**
	 * Stores all the jobs of the bot, that he runs every frame.
	 */
	private LinkedList<Job> botJobs = new LinkedList<Job>();
	
	/**
	 * Remembers the number of the current frame. Used to measure time.
	 */
	private long frameNumber = 0;
	
	protected World world;

	public MyBot(String botName, String skinName) {
		super(botName, skinName);
	}

	@Override
	public void runAI(World world) {
		this.world = world;
		updateFrameNumber();
		runBotJobs();
		try {
			botLogic();
		}
		catch (Exception e) {
			say("Runtime exception!");
			say(e.toString());
			e.printStackTrace();
			disconnect();
		}
	}	
	
	public void botLogic() {
		
	}
	
	public void consoleCommand(String cmd) {
		this.sendConsoleCommand(cmd);
	}
	
	public void say(String txt) {
		this.sendConsoleCommand("\""+txt+"\"");
	}
	
	/**
	 * Updates the number of the frame.
	 */
	private void updateFrameNumber() {
		if (frameNumber < Long.MAX_VALUE) frameNumber++;
		else frameNumber = 0;
	}
	
	/**
	 * Runs bot jobs stored in botJobs list.
	 */
	private void runBotJobs() {
		for (Job j : botJobs) j.run();
	}
	
	public long getFrameNumber() {
		return frameNumber;
	}
	
	public boolean addBotJob(Job j) {
		return botJobs.add(j);
	}

}
