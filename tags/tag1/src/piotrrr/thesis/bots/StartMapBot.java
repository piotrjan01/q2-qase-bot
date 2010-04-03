package piotrrr.thesis.bots;

import piotrrr.thesis.bots.mapbot.MapLearningBot;
import piotrrr.thesis.misc.jobs.ShutdownJob;

/**
 * This class is used as a starter of the map learning bots.
 * It starts the bots programs and tells them where to connect basing
 * on given configuration.
 * 
 * TODO: remove it and merge with another one
 * 
 * @author Piotr Gwizda³a
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
