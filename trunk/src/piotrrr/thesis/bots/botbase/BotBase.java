package piotrrr.thesis.bots.botbase;

import java.util.LinkedList;
import java.util.Vector;

import piotrrr.thesis.misc.jobs.Job;
import soc.qase.bot.ObserverBot;
import soc.qase.file.bsp.BSPParser;
import soc.qase.state.PlayerGun;
import soc.qase.state.World;
/**
 * 
 * @author Piotr Gwizda³a
 *
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
	
	private int deathsNumber = 0;
	
	protected World world;
	
	protected BSPParser bsp;

	private String lastMessage = "";

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
	
	public float getBotHealth() {
		float h = getHealth();
		if (h<0) h = 100.0f;
		return h;
	}
	
	public float getBotArmor() {
		float a = getArmor();
		if (a<0) a = 0.0f;
		return a;
	}
	
	/**
	 * Get owned guns
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
	public void respawn() {
		super.respawn();
		deathsNumber++;
		say("I came back after dieing for "+deathsNumber+" times.");
	}
	
	/**
	 * Gets the name of the current server map.
	 * @return
	 */
	public String getMapName() {
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
				//Avoid reading the same message twice.
				if ( ! s.equals(lastMessage)) {
					ret.add(s.substring(prefix.length()));
					lastMessage = s;
				}
					
			}
		}
		return ret;
	}
	

}
