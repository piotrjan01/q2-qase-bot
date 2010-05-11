package piotrrr.thesis.bots.simplebot;

import piotrrr.thesis.common.CommFun;
import piotrrr.thesis.common.combat.AimingModule;
import piotrrr.thesis.common.combat.FiringDecision;
import piotrrr.thesis.common.combat.FiringInstructions;
import piotrrr.thesis.tools.Dbg;
import soc.qase.tools.vecmath.Vector3f;

public class SimpleAimingModule implements AimingModule {
	
	@Override
	public FiringInstructions getFiringInstructions(FiringDecision fd, SimpleBot bot) {
		if (fd == null || fd.enemyInfo == null) return null;
		
		boolean reloading = bot.getWorld().getPlayer().getPlayerGun().isCoolingDown();
		Vector3f noFiringLook = fd.enemyInfo.predictedPos == null ? fd.enemyInfo.getPosition() : fd.enemyInfo.predictedPos;
		if (reloading) return getNoFiringInstructions(bot, noFiringLook);
		
		if (fd.enemyInfo.lastUpdateFrame + bot.cConfig.MAX_ENEMY_INFO_AGE_4_FIRING.value() < bot.getFrameNumber()) 
			return getNoFiringInstructions(bot, noFiringLook);
		
		
		if (fd.enemyInfo.lastPredictionError > bot.cConfig.MAX_PREDICTION_ERROR.value()) {
			bot.dtalk.addToLog("Too big prediction error. Fast firing.");
			return getFastFiringInstructions(fd, bot);
		}
		//we may be predicting well
		
		return getPredictingFiringInstructions(bot, fd, bot.cConfig.getBulletSpeedForGivenGun(fd.gunIndex), 
				bot.cConfig.isDangerousToShootWith(fd.gunIndex)); 
	}
	
	FiringInstructions getPredictingFiringInstructions(SimpleBot bot, FiringDecision fd, float bulletSpeed,	boolean careful) {
		Vector3f playerPos = bot.getBotPosition();
		Vector3f enemyPos = fd.enemyInfo.getBestVisibleEnemyPart(bot);
		
		float distance = CommFun.getDistanceBetweenPositions(playerPos, enemyPos);
		
		if (distance < bot.cConfig.MAX_SHORT_DISTANCE_4_FIRING.value()) {
			bot.dtalk.addToLog("Target is very close. Fast firing.");
			return getFastFiringInstructions(fd, bot);
		}
		
		//Calculate the time to hit
		float timeToHit = distance / bulletSpeed;
		if (timeToHit < 1) timeToHit = 1f;
		
		//If it is too big - quit
		if (timeToHit > bot.cConfig.MAX_TIME_TO_HIT.value()) {
			bot.dtalk.addToLog("Target too far. No shooting.");
			return getNoFiringInstructions(bot, enemyPos);
		}
		
		//We add to enemy position the movement that the enemy is predicted to do in timeToHit.
		Vector3f hitPoint = CommFun.cloneVector(enemyPos);
		//movement is between bot position, not the visible part of the bot
		Vector3f movement = CommFun.getMovementBetweenVectors(fd.enemyInfo.getPosition(), fd.enemyInfo.predictedPos);
		movement = CommFun.multiplyVectorByScalar(movement, timeToHit);
		hitPoint.add(movement);
		
		if ( careful &&  bot.getBsp().getObstacleDistance(playerPos, hitPoint, 20.0f, bot.cConfig.MAX_SHORT_DISTANCE_4_FIRING.value()*2) 
				< bot.cConfig.MAX_SHORT_DISTANCE_4_FIRING.value()) {
			bot.dtalk.addToLog("Being careful. No shooting!");
			return getNoFiringInstructions(bot, enemyPos);
		}
		bot.dtalk.addToLog("Prediction shooting: @"+fd.enemyInfo.ent.getName()+" gun: "+CommFun.getGunName(fd.gunIndex)+"\n pred mov: "+movement+" timeToHit: "+timeToHit+" dist: "+distance+" bspeed: "+bulletSpeed);
		return new FiringInstructions(CommFun.getNormalizedDirectionVector(playerPos, hitPoint));
	}
	
	
	/**
	 * * Point the enemy and shoot.
	 * @param fd
	 * @param playerPos
	 * @return
	 */
	public FiringInstructions getFastFiringInstructions(FiringDecision fd, SimpleBot bot) {
		Vector3f to = new Vector3f(fd.enemyInfo.getBestVisibleEnemyPart(bot));
		Vector3f fireDir = CommFun.getNormalizedDirectionVector(bot.getBotPosition(), to);
		bot.dtalk.addToLog("Fast firing.");
		return new FiringInstructions(fireDir);
	}
	
	
	FiringInstructions getNoFiringInstructions(SimpleBot bot, Vector3f enemyPos) {
		FiringInstructions ret = new FiringInstructions(CommFun.getNormalizedDirectionVector(bot.getBotPosition(), enemyPos));
		ret.doFire = false;
		return ret;
	}
	
	
	
	

}
