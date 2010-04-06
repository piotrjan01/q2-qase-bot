package piotrrr.thesis.bots.botbase;

import java.util.LinkedList;
import java.util.Vector;

import piotrrr.thesis.common.jobs.Job;
import soc.qase.bot.ObserverBot;
import soc.qase.file.bsp.BSPParser;
import soc.qase.state.PlayerGun;
import soc.qase.state.World;
import soc.qase.tools.vecmath.Vector3f;
/**
 * The bot that is used as super class for all the other bots.
 * @author Piotr Gwizda³a
 */
public class BotBase extends ObserverBot {
	
	/**
	 * Stores all the jobs of the bot, that he runs every frame.
	 */
	private LinkedList<Job> botJobs = new LinkedList<Job>();
	
	/**
	 * Remembers the number of the current frame. Used to measure time.
	 */
	private long frameNumber = 0;
	
	/**
	 * Counts the deaths of the bot.
	 */
	private int deathsNumber = 0;
	
	/**
	 * Stores information about the world from QASE
	 */
	protected World world;
	
	/**
	 * BSPParser from QASE
	 */
	protected BSPParser bsp;

	/**
	 * Basic constructor
	 * @param botName name of the bot to be created.
	 * @param skinName the name of the skin to be used with the bot.
	 */
	public BotBase(String botName, String skinName) {
		super(botName, skinName);
	}

	@Override
	public void runAI(World world) {
		try {
			this.world = world;
			updateFrameNumber();
			runBotJobs();
			botLogic();
		}
		catch (Exception e) {
			say("Runtime exception!");
			say(e.toString());
			e.printStackTrace();
			disconnect();
		}
	}	
	
	/**
	 * In this method all the bot's logic is implemented.
	 * Extending classes should override it.
	 */
	protected void botLogic() {
		
	}
	
	/**
	 * Sends the given command as a console command.
	 * @param cmd the string of console command.
	 */
	protected void consoleCommand(String cmd) {
		this.sendConsoleCommand(cmd);
	}
	
	/**
	 * Sends given string as in-game chat message.
	 * @param txt the text to be sent.
	 */
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
	
	/**
	 * @return current frame number
	 */
	public long getFrameNumber() {
		return frameNumber;
	}
	
	/**
	 * Adds job to bot's job list.
	 * @param j the job.
	 * @return true if successful.
	 */
	protected boolean addBotJob(Job j) {
		return botJobs.add(j);
	}
	
	/**
	 * Returns bot's current health.
	 * @return health in float type - number between 0 and 100.
	 */
	public float getBotHealth() {
		float h = getHealth();
		if (h<0) h = 100.0f;
		return h;
	}
	
	/**
	 * Returns bot's armor state.
	 * @return The amount of armor that bot has. Between 0 and 100.
	 */
	public float getBotArmor() {
		float a = getArmor();
		if (a<0) a = 0.0f;
		return a;
	}
	
	/**
	 * Get list of guns owned by bot.
	 * @return the indexes of the owned guns only if there is also some ammo available.
	 */
	public LinkedList<Integer> getIndexesOfOwnedGunsWithAmmo() {
		LinkedList<Integer> ret = new LinkedList<Integer>();
		int [] gunIndexes = { 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 };
		int [] ammoTable = { 0, 0, 0, 0, 0, 0, 0,
				7, //blaster - ammo for blaster is just blaster himself
				18, //shotgun
				18, //ss
				19, //mgun
				19, //chgun
				12, // granades - ammo for granates are granates themselves
				12, //g launcher
				21, //r launcher
				20, //hyperblaster - energy cells
				22, //railgun - slugs
				20 //bfgk - energy cells
		};
		
		for (int i : gunIndexes) {
			if (hasItem(i) && hasItem(ammoTable[i]) ) {
				ret.add(i);
			}
		}
		return ret;
		
	}
	
	/**
	 * Get how much ammo the bot has for given gun
	 * @param gunIndex index of the gun in inventory
	 * @return the number between 0 and 1 that tells how much ammo we have
	 */
	public float getAmmunitionState(int gunIndex) {
		int max = PlayerGun.getMaxAmmoByGun(gunIndex);
		int ammo = world.getInventory().getCount(gunIndex);
		if (max == -1) return 1;
		assert (float)ammo/max >= 0.0;
		assert (float)ammo/max <= 1.0;
		return (float)ammo/max;
	}
	
	@Override
	protected void respawn() {
		super.respawn();
		deathsNumber++;
		say("I came back after dieing for "+deathsNumber+" times.");
	}
	
	/**
	 * Gets the name of the current server map.
	 * @return
	 */
	protected String getMapName() {
		if (getBsp() == null) {
			say("BSPParser is not availible!");
			return null;
		}
		String mapName = getBsp().getFileName();
		mapName = mapName.substring(mapName.lastIndexOf("/")+1, mapName.lastIndexOf(".bsp"));
		return mapName;
	}
	
	/**
	 * Used not to call the bsp parser fetching too often and to make sure its not null.
	 * Convenience method.
	 * @return
	 */
	private BSPParser getBsp() {
		if (bsp != null) return bsp;
		bsp = this.getBSPParser();
		assert bsp != null;
		return bsp;
	}
	
	/**
	 * Gets the world's messages that are having given prefix.
	 * @return the messages that appeared in the world 
	 * with given prefix, but without it.
	 */
	@SuppressWarnings("unchecked")
	public Vector<String> getMessages(String prefix) {
		Vector<String> ret = new Vector<String>();
		Vector<String> msgs = world.getMessages();
		if (msgs == null) return null;
		for (String s : msgs) {
			if (s.startsWith(prefix)) {
				ret.add(s.substring(prefix.length()));
			}
		}
		return ret;
	}
	
	public Vector3f getBotPosition() {
		return new Vector3f(world.getPlayer().getPlayerMove().getOrigin());
	}
	

}
