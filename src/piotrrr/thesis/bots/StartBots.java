package piotrrr.thesis.bots;

import piotrrr.thesis.bots.botbase.BotBase;
import piotrrr.thesis.bots.simplebot.SimpleBot;
import piotrrr.thesis.common.jobs.HitsReporter;
import piotrrr.thesis.common.jobs.ShutdownJob;

/**
 * This class is used as a starter of the bots.
 * It starts the bots programs and tells them where to connect basing
 * on given configuration.
 * 
 * TODO: create better service, that can be used by GUI.
 * 
 * @author Piotr Gwizda³a
 */
public class StartBots {
	
	public static String quakePath = "H:\\workspace\\inzynierka\\testing-env\\quake2-3_21\\quake2";
	public static String botName = "SimpleBot";
	public static String skinName = "female/voodoo";
//	public static String serverIP = "127.0.0.1";
	public static String serverIP = "192.168.0.103";
	public static int serverPort = 27910;
	
	public static void addBots(int num) {
		BotBase bot;
		for (int i=0; i<num; i++) {
			bot = new SimpleBot(botName+"-"+(i+1), skinName);
			bot.connect(serverIP, serverPort);
			Runtime.getRuntime().addShutdownHook(new ShutdownJob(bot));
		}
	}
	
	public static void aimingExperiments1() {
		SimpleBot bot = new SimpleBot(botName, skinName);
		bot.connect(serverIP, serverPort);
		
		bot = new SimpleBot(botName+"-t1", skinName);
		bot.dtalk.active = false;
		bot.addBotJob(new HitsReporter(bot));
		bot.connect(serverIP, serverPort);
		
		bot = new SimpleBot(botName+"-t2", skinName);
		bot.dtalk.active = false;
		bot.addBotJob(new HitsReporter(bot));
		bot.connect(serverIP, serverPort);
		
		bot = new SimpleBot(botName+"-t3", skinName);
		bot.dtalk.active = false;
		bot.addBotJob(new HitsReporter(bot));
		bot.connect(serverIP, serverPort);
	}
	
	
//	public static void main(String[] args) {
//		System.setProperty("QUAKE2", quakePath);
//		
//		addBots(1);
//		
////		aimingExperiments2();
//		
//	}
	
}
