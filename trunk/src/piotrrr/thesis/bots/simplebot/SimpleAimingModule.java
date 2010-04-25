package piotrrr.thesis.bots.simplebot;

import java.util.Collection;

import piotrrr.thesis.common.CommFun;
import piotrrr.thesis.common.combat.FiringDecision;
import piotrrr.thesis.common.combat.FiringInstructions;
import soc.qase.file.bsp.BSPParser;
import soc.qase.tools.vecmath.Vector3f;

public class SimpleAimingModule {
	
	//These are the calibrated values. 
	public static final float rocketSpeed = 50;
	public static final float blasterSpeed = 100;
	public static final float hblasterSpeed = 100;
	public static final float maxMovementLength = 80.0f;
	public static final float shortDistanceLimit = 60.0f;

	private Vector3f lastPrediction = null; 
	private Vector3f lastPos = new Vector3f();
	private int lastEnId = 0;
	
	private float predQuality = Float.MAX_VALUE;
	
	FiringInstructions getFiringInstructions(FiringDecision fd, SimpleBot bot) {
		
		if (fd == null || fd.enemy == null) return null;
		FiringInstructions ret = null;
		
		if (lastPrediction != null) {
			predQuality = getSimilarityBetweenVectors(lastPrediction, fd.enemy.getOrigin().toVector3f());
			bot.dtalk.addToLog(" ----------> prediction q: "+predQuality+" lastEnemy: "+lastPos+" enemy: "+fd.enemy.getOrigin().toVector3f()+" pred: "+lastPrediction);
		}
		lastPrediction = predictPlayerPosition(fd, 1);
		
		lastPos = fd.enemy.getOrigin().toVector3f();
		
		if (predQuality > 50) {
			return getFastFiringInstructions(fd, bot);
		}

		/**
		int BLASTER = 7, SHOTGUN = 8,
		SUPER_SHOTGUN = 9, MACHINEGUN = 10, CHAINGUN = 11, GRENADES = 12,
		GRENADE_LAUNCHER = 13, ROCKET_LAUNCHER = 14, HYPERBLASTER = 15,
		RAILGUN = 16, BFG10K = 17, SHELLS = 18, BULLETS = 19, CELLS = 20,
		ROCKETS = 21, SLUGS = 22;
		**/
		
		float bspeed = Float.MAX_VALUE;
		boolean careful = false;
		
		switch (fd.gunIndex) {
		
		case 7:
//			return getDelayedHitFiringInstructions(fd, blasterSpeed, 30f, false, bot);
			bspeed = blasterSpeed;
			break;
		case 14:
		case 17:
//			return getDelayedHitFiringInstructions(fd, rocketSpeed, 60f, true, bot);
			careful = true;
			bspeed = rocketSpeed;
			break;
		case 15:
//			return getDelayedHitFiringInstructions(fd, hblasterSpeed, 30f, false, bot);
			bspeed = hblasterSpeed;
			break;
		case 16:
		case 8:
		case 9:
		case 10:
		case 11:
			break;
		default: 
			System.err.println("Warning: Wrong inventory gun index passed to " +
					"BasicFiringModule. Gave: "+fd.gunIndex);
			
		}
		return getPredictingFiringInstructions(bot, fd, bspeed, careful);
	}
	
	FiringInstructions getPredictingFiringInstructions(SimpleBot bot, FiringDecision fd, float bulletSpeed, boolean careful) {
		Vector3f playerPos = bot.getBotPosition();
		float distance = CommFun.getDistanceBetweenPositions(playerPos, new Vector3f(fd.enemy.getOrigin()));
		
		float timeToHit = distance / bulletSpeed;
		if (timeToHit < 1) timeToHit = 1;
		Vector3f to = predictPlayerPosition(fd, timeToHit);
		
		bot.dtalk.addToLog(" ==========> prediction shooting: timeToHit: "+timeToHit);
		
		if ( careful &&  bot.getBsp().getObstacleDistance(playerPos, to, 30.0f, shortDistanceLimit) 
				< shortDistanceLimit) return null;
		return new FiringInstructions(to);
	}
	
	
	/**
	 * * Point the enemy and shoot.
	 * @param fd
	 * @param playerPos
	 * @return
	 */
	public FiringInstructions getFastFiringInstructions(FiringDecision fd, SimpleBot bot) {
		if (fd.enemy != null) {
				Vector3f to = new Vector3f(fd.enemy.getOrigin());
				bot.dtalk.addToLog("=========> fast shooting enemy at origin: "+to);
				Vector3f fireDir = CommFun.getNormalizedDirectionVector(bot.getBotPosition(), to);
				return new FiringInstructions(fireDir);
				
		}
		return null;
	}
	
	
	/**
	 * Returns firing instructions following the firing decision for delayed hit guns.
	 * @param fd
	 * @param speed
	 * @param shortDistanceLimit
	 * @param careful
	 * @param bot
	 * @return
	 */
	public FiringInstructions getDelayedHitFiringInstructions(FiringDecision fd, float speed,
			float shortDistanceLimit, boolean careful, SimpleBot bot) {
		
		Vector3f playerPos = bot.getBotPosition();
		BSPParser bsp = bot.getBsp();
		
		if (fd.enemy != null) {
			
			if (lastEnId != fd.enemy.getNumber()) lastPos = null;
			
			if (lastPos == null) {
				lastPos = new  Vector3f(fd.enemy.getOrigin());
				lastEnId = fd.enemy.getNumber();
				return getFastFiringInstructions(fd, bot);
			}		
			
			float distance = CommFun.getDistanceBetweenPositions(playerPos, new Vector3f(fd.enemy.getOrigin()));
			if (distance <= shortDistanceLimit) return getFastFiringInstructions(fd, bot);
			
			Vector3f to = new Vector3f(fd.enemy.getOrigin());
			
			bot.dtalk.addToLog("====> delayed shooting enemy at origin: "+to+" old: "+lastPos);
		
			Vector3f movement = CommFun.getMovementBetweenVectors(lastPos, to);
			
			//Sometimes the movement length is weirdly too long, so we just do fast firing.
			if (movement.length() > maxMovementLength) return getFastFiringInstructions(fd, bot);
			
			Vector3f firePos = new Vector3f(to);

			float mult = CommFun.getDistanceBetweenPositions(playerPos, to);
			mult = mult / speed;  //we get the time in frames to reach the target, theoretically.
			
			firePos.add(CommFun.multiplyVectorByScalar(movement, mult));
			
			Vector3f fireDir = CommFun.getNormalizedDirectionVector(playerPos, firePos);
			if ( careful &&  bsp.getObstacleDistance(playerPos, firePos, 30.0f, shortDistanceLimit) 
					< shortDistanceLimit) return null;
			return new FiringInstructions(fireDir);
		}
		return null;
	}
	
	/**
	 * Returns the firing instructions for instant hit weapons
	 * @param fd
	 * @param bot
	 * @param shortDistanceLimit
	 * @return
	 */
	public FiringInstructions getBulletsFiringInstructions(FiringDecision fd, SimpleBot bot,
			float shortDistanceLimit) {
		
		Vector3f playerPos = bot.getBotPosition();
		
		if (fd.enemy != null) {
			
			if (lastEnId != fd.enemy.getNumber()) lastPos = null;
			
			if (lastPos == null) {
				lastPos = new  Vector3f(fd.enemy.getOrigin());
				lastEnId = fd.enemy.getNumber();
				return getFastFiringInstructions(fd, bot);
			}		
			
			float distance = CommFun.getDistanceBetweenPositions(playerPos, new Vector3f(fd.enemy.getOrigin()));
			if (distance <= shortDistanceLimit) return getFastFiringInstructions(fd, bot);
			
			Vector3f to = new Vector3f(fd.enemy.getOrigin());
			
			bot.dtalk.addToLog("====> bullet shooting enemy at origin: "+to+" old: "+lastPos);
		
			Vector3f movement = CommFun.getMovementBetweenVectors(lastPos, to);
			lastPos = new  Vector3f(fd.enemy.getOrigin());
			
			if (movement.length() > maxMovementLength) return getFastFiringInstructions(fd, bot);
			Vector3f firePos = new Vector3f(to);
			
			firePos.add(movement);
			
			Vector3f fireDir = CommFun.getNormalizedDirectionVector(playerPos, firePos);
			return new FiringInstructions(fireDir);
		}
		return null;
	}
	
	Vector3f predictPlayerPosition(FiringDecision fd, float inFrames) {
		
		if (lastEnId != fd.enemy.getNumber()) lastPos = null;
		
		if (lastPos == null) {
			lastPos = new  Vector3f(fd.enemy.getOrigin());
			lastEnId = fd.enemy.getNumber();
			return lastPos;
		}	
		
		Vector3f to = new Vector3f(fd.enemy.getOrigin());
		Vector3f movement = CommFun.getMovementBetweenVectors(lastPos, to);
		
		Vector3f predictPos = new Vector3f(to);
		predictPos.add(CommFun.multiplyVectorByScalar(movement, inFrames));
		
		return predictPos;
		
	}
	
	
	
	
	/**
	 *  Calculates the average vector given collection of vectors.
	 * @param col
	 * @return
	 */
	Vector3f calculateAverageVector(Collection<Vector3f> col) {
		float count = 0f;
		float x = 0f, y = 0f, z = 0f;
		for (Vector3f v : col) {
			count++;
			x+=v.x; y+=v.y; z+=v.z;
		}
		return new Vector3f(x/count, y/count, z/count);
	}
	
	/**
	 * Returns the sum of the distances between each dimensions of given vectors.
	 * @param v1
	 * @param v2
	 * @return
	 */
	float getSimilarityBetweenVectors(Vector3f v1, Vector3f v2) {
		float ret = Math.abs(v1.x-v2.x);
		ret+=Math.abs(v1.y-v2.y);
		ret+=Math.abs(v1.z-v2.z);
		return ret;
	}
	
	
	

}
