package piotrrr.bot.misc.jobs;

import java.util.Vector;

import piotrrr.bot.misc.BotBase;

/**
 * 
 * @author Piotr Gwizda³a
 */
public class BasicCommands extends Job {
	
	String commanderName = "";

	public BasicCommands(BotBase bot) {
		super(bot);
	}
	
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
