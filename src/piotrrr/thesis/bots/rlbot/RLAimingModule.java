package piotrrr.thesis.bots.rlbot;

import piotrrr.thesis.bots.rlbot.rl.other.RLWorld;
import piotrrr.thesis.bots.rlbot.rl.other.RLearner;
import piotrrr.thesis.common.combat.*;
import piotrrr.thesis.bots.wpmapbot.MapBotBase;
import piotrrr.thesis.common.CommFun;
import piotrrr.thesis.common.stats.BotStatistic;
import soc.qase.tools.vecmath.Vector3f;

/**
 * The aiming module
 * @author Piotr Gwizda�a
 */
public class RLAimingModule implements RLWorld {

    public static final int ACTION_0 = 0;
    public static final int ACTION_CURRENT = 1;
    public static final int ACTION_PREDICTED = 2;
    public static final int ACTION_HITPOINT1 = 3;
    public static final int ACTION_HITPOINT09 = 4;
    public static final int ACTION_HITPOINT08 = 5;
    public static final int ACTION_HITPOINT07 = 6;
    public static final int ACTION_HITPOINT06 = 7;
    public static final int ACTION_HITPOINT05 = 8;
    public static final int ACTION_HITPOINT04 = 9;
    public static final int ACTION_HITPOINT03 = 10;
    public static final int ACTION_HITPOINT02 = 11;
    public static final int ACTION_HITPOINT01 = 12;

    public static final int STATE_DIST_SMALL = 0;
    public static final int STATE_DIST_MEDIUM = 1;
    public static final int STATE_DIST_LARGE = 2;

    public static final int STATE_GUN_BLASTER = 0;
    public static final int STATE_GUN_SHOTGUN = 1;
    public static final int STATE_GUN_SUPER_SHOTGUN = 2;
    public static final int STATE_GUN_MACHINEGUN = 3;
    public static final int STATE_GUN_CHAINGUN = 4;
    public static final int STATE_GUN_GRENADES = 5;
    public static final int STATE_GUN_GRENADE_LAUNCHER = 6;
    public static final int STATE_GUN_ROCKET_LAUNCHER = 7;
    public static final int STATE_GUN_HYPERBLASTER = 8;
    public static final int STATE_GUN_RAILGUN = 9;
    public static final int STATE_GUN_BFG10K = 10;

    private int [] lastState = null;

    RLearner learner = new RLearner(this);

    RlBot bot = null;

    public RLAimingModule(RlBot bot) {
        this.bot = bot;
    }

	/**
	 * Returns the firing instructions
	 * @param fd firing decision
	 * @param bot the bot
	 * @return
	 */
	public FiringInstructions getFiringInstructions(FiringDecision fd) {
            if (fd == null) return null;

            boolean reloading = bot.getWorld().getPlayer().getPlayerGun().isCoolingDown();
            if (reloading) return getNoFiringInstructions(fd.enemyInfo.getObjectPosition());

//            if (fd.enemyInfo.)
            int [] currentState = getCurrentState(fd);

            if (lastState != null)  {
                double reward = bot.getReward();
                learner.setQlearningReward(lastState, reward);
                if (BotStatistic.getInstance() != null)
                    BotStatistic.getInstance().addReward(bot.getBotName(), reward, bot.getFrameNumber());
            }
            int action = learner.getQlearningAction(currentState);

            lastState = currentState.clone();
            return getFIForAction(fd, action);
	}

        int [] getCurrentState(FiringDecision fd) {
            int state_dist = STATE_DIST_MEDIUM;
            int state_gun = fd.gunIndex-7;
            double distance = CommFun.getDistanceBetweenPositions(bot.getBotPosition(), fd.enemyInfo.getObjectPosition());
            if (distance < 200) state_dist = STATE_DIST_SMALL;
            else if (distance > 500) state_dist = STATE_DIST_LARGE;
            return new int [] {state_dist, state_gun};
        }


        FiringInstructions getFIForAction(FiringDecision fd, int action) {
            switch (action) {
                case RLAimingModule.ACTION_0:
                    return getNoFiringInstructions(fd.enemyInfo.getObjectPosition());
                case RLAimingModule.ACTION_CURRENT:
                    return new FiringInstructions(
                                CommFun.getNormalizedDirectionVector(bot.getBotPosition(), fd.enemyInfo.getObjectPosition()),
                                getTimeToHit(bot.getBotPosition(), fd.enemyInfo.getObjectPosition(), fd)
                                );
                case RLAimingModule.ACTION_PREDICTED:
                    return new FiringInstructions(
                                CommFun.getNormalizedDirectionVector(bot.getBotPosition(), fd.enemyInfo.predictedPos),
                                getTimeToHit(bot.getBotPosition(), fd.enemyInfo.predictedPos, fd)
                                );
                case RLAimingModule.ACTION_HITPOINT1:
                case RLAimingModule.ACTION_HITPOINT09:
                case RLAimingModule.ACTION_HITPOINT08:
                case RLAimingModule.ACTION_HITPOINT07:
                case RLAimingModule.ACTION_HITPOINT06:
                case RLAimingModule.ACTION_HITPOINT05:
                case RLAimingModule.ACTION_HITPOINT04:
                case RLAimingModule.ACTION_HITPOINT03:
                case RLAimingModule.ACTION_HITPOINT02:
                case RLAimingModule.ACTION_HITPOINT01:
                    return getFiringInstructionsAtHitpoint(fd, (13d-(double)action)/10d);
                default:
                    return null;
            }
        }

        FiringInstructions getFiringInstructionsAtHitpoint(FiringDecision fd, double percent) {

            //We add to enemy position the movement that the enemy is predicted to do in timeToHit.
                Vector3f enemyPos = fd.enemyInfo.getObjectPosition();
                Vector3f playerPos = bot.getBotPosition();

                float timeToHit = getTimeToHit(playerPos, enemyPos, fd);

                Vector3f hitPoint = CommFun.cloneVector(enemyPos);
		//movement is between bot position, not the visible part of the bot
		Vector3f movement = CommFun.getMovementBetweenVectors(fd.enemyInfo.getObjectPosition(), fd.enemyInfo.predictedPos);
		movement = CommFun.multiplyVectorByScalar(movement, timeToHit);
                movement = CommFun.multiplyVectorByScalar(movement,(float) percent);
		hitPoint.add(movement);
		
//		bot.dtalk.addToLog("Prediction shooting: @"+fd.enemyInfo.ent.getName()+" gun: "+CommFun.getGunName(fd.gunIndex)+"\n pred mov: "+movement+" timeToHit: "+timeToHit+" dist: "+distance+" bspeed: "+bulletSpeed);
		return new FiringInstructions(CommFun.getNormalizedDirectionVector(playerPos, hitPoint), timeToHit);
	}


	/**
	 * Don't fire, just look at the enemy.
	 * @param bot
	 * @param enemyPos
	 * @return
	 */
	FiringInstructions getNoFiringInstructions(Vector3f enemyPos) {
		FiringInstructions ret = new FiringInstructions(CommFun.getNormalizedDirectionVector(bot.getBotPosition(), enemyPos));
		ret.doFire = false;
		return ret;
	}

    public int[] getDimension() {
        return new int [] {3, 11, 13};
    }

    public int[] getNextState(int action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getReward() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean validAction(int action) {
        return true;
    }

    public boolean endState() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int[] resetState() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getInitValues() {
        return ACTION_0;
    }

    private float getTimeToHit(Vector3f playerPos, Vector3f enemyPos, FiringDecision fd) {
        float distance = CommFun.getDistanceBetweenPositions(playerPos, enemyPos);
        float bulletSpeed = bot.cConfig.getBulletSpeedForGivenGun(fd.gunIndex);
        //Calculate the time to hit
        float timeToHit = distance / bulletSpeed;
        if (timeToHit < 1) {
            timeToHit = 1f;
        }
        return timeToHit;
    }
	
	
	
	

}
