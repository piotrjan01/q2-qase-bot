package piotrrr.thesis.common.jobs;

import java.util.Vector;

import piotrrr.thesis.bots.botbase.BotBase;
import piotrrr.thesis.bots.smartbot.SmartBot;
import piotrrr.thesis.bots.smartbot.SmartBotEntityRanking;
import piotrrr.thesis.bots.tuning.FileLogger;
import piotrrr.thesis.common.CommFun;

public class BasicMeasuresJob extends Job {

	public BasicMeasuresJob(BotBase bot) {
		super(bot);
		killsLog.addToLog("Starting the measurement,null,null\n");
	}
	
	FileLogger killsLog = new FileLogger("kills.csv");
	
	
	@Override
	public void run() {
		super.run();
//		logKillsToFile();
                logKillsToStats();
	}
	

	private void logKillsToFile() {
		Vector<String> commands = bot.getMessages("");
		if (commands == null) return;
		for (String cmd : commands) {
			if (cmd.contains(" was ") && cmd.contains(" by ")) {
                            	String t = ""+bot.getFrameNumber()/10+","; 
				t += cmd.substring(0, cmd.indexOf(" was "));
				t += ","+cmd.substring(cmd.indexOf(" by ")+4);
				if (t.indexOf("'s") != -1)
					t = t.substring(0, t.indexOf("'s"));
//				t.substring(0, (t.indexOf("'s") == -1 ? t.length() : t.indexOf("'s")));
				t += ","+cmd+"\n";
//				Dbg.prn(t);
				killsLog.addToLog(t);
			}
		}
		
	}

        private void logKillsToStats() {
		Vector<String> commands = bot.getMessages("");
		if (commands == null) return;
		for (String cmd : commands) {
			if (cmd.contains(" was ") && cmd.contains(" by ")) {
                            String victim = cmd.substring(0, cmd.indexOf(" was "));
                            String killer = cmd.substring(cmd.indexOf(" by ")+4);
			    if (killer.indexOf("'s") != -1)
					killer = killer.substring(0, killer.indexOf("'s"));
                            bot.stats.addKill(
                                    bot.getFrameNumber(),
                                    killer,
                                    victim,
                                    CommFun.getGunName(cmd)
                                );
			}
		}

	}
	

}
