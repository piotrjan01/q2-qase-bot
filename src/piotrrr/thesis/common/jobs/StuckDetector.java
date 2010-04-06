package piotrrr.thesis.common.jobs;

import piotrrr.thesis.bots.botbase.BotBase;
import piotrrr.thesis.bots.simplebot.SimpleBot;
import piotrrr.thesis.common.CommFun;
import piotrrr.thesis.tools.Dbg;
import soc.qase.tools.vecmath.Vector3f;

/**
 * This job detects when the bot got stuck.
 * @author Piotr Gwizda³a
 */
public class StuckDetector extends Job {
	
	public static final float EPSILON = 20;
	
	long lastFrame;
	
	int period;
	
	/**
	 * Remembers last bot's position. 
	 */
	Vector3f lastPos = null;
	
	Vector3f lastlastPos = null;
	
	public StuckDetector(BotBase bot, int period) {
		super(bot);
		this.period = period;
		this.lastFrame = bot.getFrameNumber();
		//lastPos = ((SimpleBot)bot).getBotPosition();
		//lastlastPos = lastPos;
	}
	
	@Override
	public void run() {
		if (bot.getFrameNumber() - lastFrame < period ) {
			((SimpleBot)bot).setStuck(false);
			return;
		}
		lastFrame = bot.getFrameNumber();
		
		Vector3f pos;
		try {
			pos = bot.getBotPosition();
		}
		catch (NullPointerException e) {
			Dbg.err("Can't read bot position yet");
			return;
		}
		
		if (lastPos == null) {
			lastPos = pos;
			return;
		}
		
		if (lastlastPos == null) {
			lastlastPos = lastPos;
			lastPos = pos;
			return;
		}
		
		float distance = CommFun.getDistanceBetweenPositions(pos, lastPos);
		distance += CommFun.getDistanceBetweenPositions(lastPos, lastlastPos);
		distance += CommFun.getDistanceBetweenPositions(pos, lastlastPos);
		lastlastPos = lastPos;
		lastPos = pos;
		
		if (distance <= EPSILON) {
			((SimpleBot)bot).dtalk.addToLog("I'm stuck!");
			((SimpleBot)bot).setStuck(true);
		}
		else ((SimpleBot)bot).setStuck(false);
		
	}

}
