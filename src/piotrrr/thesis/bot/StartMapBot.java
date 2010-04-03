package piotrrr.thesis.bot;

import piotrrr.thesis.bot.mapbot.MapLearningBot;
import piotrrr.thesis.misc.jobs.ShutdownJob;

/**
 * 
 * @author Piotr Gwizda³a
 *
 */
public class StartMapBot {
	
	static String quakePath = "H:\\workspace\\inzynierka\\testing-env\\quake2";
	static String botName = "MapLearningBot";
	static String skinName = "female/voodoo";
	static String serverIP = "127.0.0.1";
	static int serverPort = 27910;
	
	public static void main(String[] args) {
		System.setProperty("QUAKE2", quakePath);
		MapLearningBot bot = new MapLearningBot(botName, skinName);
		Runtime.getRuntime().addShutdownHook(new ShutdownJob(bot));
		bot.connect(serverIP, serverPort);
	}
	
}
