package piotrrr.bot;

import piotrrr.bot.basicbot.SimpleBot;
import piotrrr.bot.misc.GenericBot;
import piotrrr.bot.misc.jobs.ShutdownJob;

/**
 * 
 * @author Piotr Gwizda³a
 *
 */
public class StartBots {
	
	static String quakePath = "C:\\workspace\\inzynierka\\testing-env\\quake2-3_21\\quake2";
	static String botName = "SimpleBot";
	static String skinName = "female/voodoo";
	static String serverIP = "127.0.0.1";
//	static String serverIP = "192.168.0.103";
	static int serverPort = 27910;
	
	public static void main(String[] args) {
		System.setProperty("QUAKE2", quakePath);
		GenericBot bot = new SimpleBot(botName, skinName);
		Runtime.getRuntime().addShutdownHook(new ShutdownJob(bot));
		bot.connect(serverIP, serverPort);
	}
	
}
