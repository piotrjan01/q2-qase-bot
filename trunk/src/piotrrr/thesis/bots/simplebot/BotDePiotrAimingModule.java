package piotrrr.thesis.bots.simplebot;

import java.util.Collection;

import piotrrr.thesis.common.CommFun;
import piotrrr.thesis.common.combat.AimingModule;
import piotrrr.thesis.common.combat.FiringDecision;
import piotrrr.thesis.common.combat.FiringInstructions;

import soc.qase.file.bsp.BSPParser;
import soc.qase.tools.vecmath.Vector3f;

/**
 * Basic Firing Module - knows how to fire every gun.
 * @author piotrrr
 *
 */
public class BotDePiotrAimingModule implements AimingModule {
	
	
	//TODO: Hide them !
	//These are the calibrated values. They showed up to be quite round.
	public float rocketSpeed = 50;
	public float blasterSpeed = 100;
	public float hblasterSpeed = 100;
	public float maxMovementLength = 80.0f;

	private Vector3f lastPos = new Vector3f();
	private int lastEnId = 0;

	@Override
	public FiringInstructions getFiringInstructions(FiringDecision fd, SimpleBot bot) {
		
		
		/**
		int BLASTER = 7, SHOTGUN = 8,
		SUPER_SHOTGUN = 9, MACHINEGUN = 10, CHAINGUN = 11, GRENADES = 12,
		GRENADE_LAUNCHER = 13, ROCKET_LAUNCHER = 14, HYPERBLASTER = 15,
		RAILGUN = 16, BFG10K = 17, SHELLS = 18, BULLETS = 19, CELLS = 20,
		ROCKETS = 21, SLUGS = 22;
		**/
	
		
		if (fd == null || fd.enemy == null) return null;
		
		Vector3f playerPos = bot.getBotPosition();
		BSPParser bsp = bot.getBsp();
		
		FiringInstructions ret = null;
		switch (fd.gunIndex) {
		
		case 7:
			return getDelayedHitFiringInstructions(fd, blasterSpeed, 30f, false, playerPos, bsp);
		case 14:
		case 17:
			return getDelayedHitFiringInstructions(fd, rocketSpeed, 60f, true, playerPos, bsp);
		case 15:
			return getDelayedHitFiringInstructions(fd, hblasterSpeed, 30f, false, playerPos, bsp);
		case 16:
			return getBulletsFiringInstructions(fd, playerPos, 300f);
		case 8:
		case 9:
		case 10:
		case 11:
			return getBulletsFiringInstructions(fd, playerPos, 200f);
		default: 
			System.err.println("Warning: Wrong inventory gun index passed to " +
					"BasicFiringModule. Gave: "+fd.gunIndex);
			
		}
		return ret;
	}
	
	
	/**
	 * Point the enemy and shoot.
	 * @param fd
	 * @param playerPos
	 * @return
	 */
	public FiringInstructions getFastFiringInstructions(FiringDecision fd, Vector3f playerPos) {
		
		if (fd.enemy != null) {
				Vector3f to = new Vector3f(fd.enemy.getOrigin());
				Vector3f fireDir = CommFun.getNormalizedDirectionVector(playerPos, to);
				return new FiringInstructions(fireDir);
		}
		return null;
	}
	
	
	/**
	 *  Returns firing instructions following the firing decision for delayed hit guns.
	 * @param fd - firing decision
	 * @param speed - speed of the bullet of the gun.
	 * @param careful - weather or not to be careful (don't kill myself with rocket launcher)
	 * @param playerPos - players actual position
	 * @param bsp - BSPParser object.
	 * @return
	 */
	public FiringInstructions getDelayedHitFiringInstructions(FiringDecision fd, float speed,
			float shortDistanceLimit, boolean careful, Vector3f playerPos, BSPParser bsp) {
		
		if (fd.enemy != null) {
			if (lastEnId != fd.enemy.getNumber()) lastPos = null;
			
			float distance = CommFun.getDistanceBetweenPositions(playerPos, new Vector3f(fd.enemy.getOrigin()));
			if (distance <= shortDistanceLimit) return getFastFiringInstructions(fd, playerPos);
			
			if (lastPos == null) {
				lastPos = new  Vector3f(fd.enemy.getOrigin());
				lastEnId = fd.enemy.getNumber();
				return getFastFiringInstructions(fd, playerPos);
			}		
			else {	
				Vector3f to = new Vector3f(fd.enemy.getOrigin());
			
				Vector3f movement = CommFun.getMovementBetweenVectors(lastPos, to);
				lastPos = new  Vector3f(fd.enemy.getOrigin());
				//TODO: ??
				if (movement.length() > maxMovementLength) return getFastFiringInstructions(fd, playerPos);
				Vector3f firePos = new Vector3f(to);
				
				float mult = CommFun.getDistanceBetweenPositions(playerPos, to);
				mult = mult / speed;  //we get the time in frames to reach the target, theoretically.
				
				firePos.add(CommFun.multiplyVectorByScalar(movement, mult));
				
				Vector3f fireDir = CommFun.getNormalizedDirectionVector(playerPos, firePos);
				if ( careful &&  bsp.getObstacleDistance(playerPos, firePos, 30.0f, shortDistanceLimit) 
						< shortDistanceLimit) return null;
				return new FiringInstructions(fireDir);
			}
		}
		return null;
	}
	
	/**
	 * Returns the firing instructions for instant hit weapons
	 * @param fd
	 * @return
	 */
	public FiringInstructions getBulletsFiringInstructions(FiringDecision fd, Vector3f playerPos,
			float shortDistanceLimit) {
		
		if (fd.enemy != null) {
			if (lastEnId != fd.enemy.getNumber()) lastPos = null;
			
			float distance = CommFun.getDistanceBetweenPositions(playerPos, new Vector3f(fd.enemy.getOrigin()));
			if (distance <= shortDistanceLimit) return getFastFiringInstructions(fd, playerPos);
			
			if (lastPos == null) {
				lastPos = new  Vector3f(fd.enemy.getOrigin());
				lastEnId = fd.enemy.getNumber();
				return getFastFiringInstructions(fd, playerPos);
			}		
			else {	
				Vector3f to = new Vector3f(fd.enemy.getOrigin());
			
				Vector3f movement = CommFun.getMovementBetweenVectors(lastPos, to);
				lastPos = new  Vector3f(fd.enemy.getOrigin());
				//TODO: ??
				if (movement.length() > maxMovementLength) return getFastFiringInstructions(fd, playerPos);
				Vector3f firePos = new Vector3f(to);
				
				firePos.add(movement);
				
				Vector3f fireDir = CommFun.getNormalizedDirectionVector(playerPos, firePos);
				return new FiringInstructions(fireDir);
			}
		}
		return null;
	}
	
	
	
	
	/**
	 * Calculates the average vector given collection of vectors.
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
