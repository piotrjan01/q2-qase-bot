package piotrrr.thesis.bots.simplebot;

import java.util.Collection;

import piotrrr.thesis.common.CommFun;
import piotrrr.thesis.common.combat.AimingModule;
import piotrrr.thesis.common.combat.FiringDecision;
import piotrrr.thesis.common.combat.FiringInstructions;
import soc.qase.tools.vecmath.Vector3f;

public class SimpleAimingModule implements AimingModule {
	
	//These are the calibrated values. 
	public static final float rocketSpeed = 50;
	public static final float blasterSpeed = 100;
	public static final float hblasterSpeed = 100;
	public static final float maxMovementLength = 80.0f;
	public static final float shortDistanceLimit = 60.0f;

	private Vector3f lastPrediction = null; 
	private Vector3f lastPos = new Vector3f();
	private int lastEnId = 0;
	
	private float lastPredQuality;
	
	@Override
	public FiringInstructions getFiringInstructions(FiringDecision fd, SimpleBot bot) {
		
		if (fd == null || fd.enemy == null) return null;
		
		boolean reloading = bot.getWorld().getPlayer().getPlayerGun().isCoolingDown();
		
//		if (! reloading) bot.dtalk.addToLog("SHOOTING AT THE ENEMY: "+fd.enemy.getName()+" gun: "+CommFun.getGunName(fd.gunIndex));
		
		Vector3f enemyPos = fd.enemy.getOrigin().toVector3f();
		Vector3f newPrediction;
		
		
		
		//Report the quality of the last prediction
		if (lastPrediction != null) {
			lastPredQuality = getSimilarityBetweenVectors(lastPrediction, enemyPos);
//			if (! reloading) {
//				bot.dtalk.addToLog(" -> last prediction q: "+lastPredQuality+" lastEnemyPos: "+lastPos+" nowEnemyPos: "+enemyPos+" lastPredPos: "+lastPrediction);
//				bot.dtalk.addToLog(" -> prediction mov: "+CommFun.getMovementBetweenVectors(lastPos, lastPrediction)+" enemy mov: "+CommFun.getMovementBetweenVectors(lastPos, enemyPos));
//			}
		}
		
		//Make the new predicton
		newPrediction = predictPlayerPosition(fd);
		
		//If we can't shoot - we exit
		if (reloading) {
			lastPrediction = newPrediction;
			lastPos = enemyPos;
			return getNoFiringInstructions(bot, enemyPos);
		}
		
		
		
		//if the prediction is bad, we don't use it
		if (lastPredQuality > 50) {
			lastPrediction = newPrediction;
			lastPos = enemyPos;
			bot.dtalk.addToLog(" => fast shooting - too poor prediction: @"+fd.enemy.getName()+" gun: "+CommFun.getGunName(fd.gunIndex));
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
			bspeed = blasterSpeed;
			break;
		case 14:
		case 17:
			careful = true;
			bspeed = rocketSpeed;
			break;
		case 15:
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
		FiringInstructions fi = getPredictingFiringInstructions(bot, fd, bspeed, careful, newPrediction);
		lastPrediction = newPrediction;
		lastPos = enemyPos;
		return fi;
	}
	
	FiringInstructions getPredictingFiringInstructions(SimpleBot bot, FiringDecision fd, float bulletSpeed, 
			boolean careful, Vector3f prediction) {
		Vector3f playerPos = bot.getBotPosition();
		Vector3f enemyPos = fd.enemy.getOrigin().toVector3f();
		
		float distance = CommFun.getDistanceBetweenPositions(playerPos, new Vector3f(enemyPos));
		
		//Calculate the time to hit
		float timeToHit = distance / bulletSpeed;
		if (timeToHit < 1) timeToHit = 1f;
		
		//If it is too big - quit
		if (timeToHit > 8) {
			bot.dtalk.addToLog("no prediction shooting - enemy too far...");
			return getNoFiringInstructions(bot, enemyPos);
		}
		
		//We add to predicted position the additional movement that the enemy is predicted to do in timeToHit.
		Vector3f movement = CommFun.getMovementBetweenVectors(enemyPos, prediction);
		movement = CommFun.multiplyVectorByScalar(movement, timeToHit-1);
		prediction.add(movement);
		
		if ( careful &&  bot.getBsp().getObstacleDistance(playerPos, prediction, 30.0f, shortDistanceLimit) 
				< shortDistanceLimit) {
			bot.dtalk.addToLog("careful! no shooting!");
			return getNoFiringInstructions(bot, enemyPos);
		}
		bot.dtalk.addToLog(" => prediction shooting: @"+fd.enemy.getName()+" gun: "+CommFun.getGunName(fd.gunIndex)+"\n pred mov: "+movement+" timeToHit: "+timeToHit+" dist: "+distance+" bspeed: "+bulletSpeed);
		return new FiringInstructions(CommFun.getNormalizedDirectionVector(playerPos, prediction));
	}
	
	
	/**
	 * * Point the enemy and shoot.
	 * @param fd
	 * @param playerPos
	 * @return
	 */
	public FiringInstructions getFastFiringInstructions(FiringDecision fd, SimpleBot bot) {
		Vector3f to = new Vector3f(fd.enemy.getOrigin());
		Vector3f fireDir = CommFun.getNormalizedDirectionVector(bot.getBotPosition(), to);
		return new FiringInstructions(fireDir);
	}
	
	
	Vector3f predictPlayerPosition(FiringDecision fd) {
		
		if (lastEnId != fd.enemy.getNumber()) lastPos = null;
		
		if (lastPos == null) {
			lastPos = new  Vector3f(fd.enemy.getOrigin());
			lastEnId = fd.enemy.getNumber();
			return lastPos;
		}	
		
		Vector3f to = new Vector3f(fd.enemy.getOrigin());
		Vector3f movement = CommFun.getMovementBetweenVectors(lastPos, to);
		
		to.add(movement);
		
		return to;
		
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
	
	FiringInstructions getNoFiringInstructions(SimpleBot bot, Vector3f enemyPos) {
		FiringInstructions ret = new FiringInstructions(CommFun.getNormalizedDirectionVector(bot.getBotPosition(), enemyPos));
		ret.doFire = false;
		return ret;
	}
	
	

}
