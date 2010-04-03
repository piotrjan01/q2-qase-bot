package piotrrr.thesis.common.jobs;

import java.util.Vector;

import piotrrr.thesis.bots.botbase.BotBase;

/**
 * The job that implements bot's perception and reaction do basic commands given
 * by defined commander.
 * 
 * Availible commands:
 * disc - tells the bot to disconnect and terminate it's program.
 * 
 * TODO: list the commands in the comment!
 * 
 * @author Piotr Gwizda³a
 */
public class BasicCommands extends Job {
	
	/**
	 * The name of the commander
	 */
	String commanderName = "";

	/**
	 * Constructor
	 * @param bot the bot to use the Job
	 */
	public BasicCommands(BotBase bot) {
		super(bot);
	}
	
	/**
	 * Constructor
	 * @param bot the bot to use the job
	 * @param commanderName the name of the commander. Only his/hers commands will be
	 * valid.
	 */
	public BasicCommands(BotBase bot, String commanderName) {
		super(bot);
		this.commanderName = commanderName;
	}
	
	@Override
	public void run() {
		super.run();
		Vector<String> commands = bot.getMessages(commanderName+": ");
		if (commands == null) return;
		for (String cmd : commands) {
			cmd = cmd.trim();
			
			if (cmd.equals("disc")) {
				bot.say("Bye, bye!");
				bot.disconnect();
			}
			
			else {
				bot.say("Hey "+commanderName+", I don't get it: "+cmd);				
			}

		}
	}

}
