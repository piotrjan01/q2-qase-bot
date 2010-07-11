package piotrrr.thesis.bots.rlbot;

import java.util.HashSet;
import piotrrr.thesis.bots.rlbot.rl.Action;
import piotrrr.thesis.bots.rlbot.rl.QFunction;
import piotrrr.thesis.bots.rlbot.rl.RLAction;
import piotrrr.thesis.bots.rlbot.rl.State;
import piotrrr.thesis.common.combat.*;
import piotrrr.thesis.common.CommFun;
import piotrrr.thesis.common.stats.BotStatistic;
import soc.qase.tools.vecmath.Vector3f;

/**
 * The aiming module
 * @author Piotr Gwizda�a
 */
public class RLCombatModule {

    RlBot bot;
    QFunction qf = new QFunction(new Action(Action.NO_FIRE));
    State lastState = null;
    Action lastAction = null;

    public RLCombatModule(RlBot bot) {
        this.bot = bot;
    }

    public FiringDecision getFiringDecision() {
        Vector3f playerPos = bot.getBotPosition();
        EnemyInfo chosen = null;
        float chosenRisk = Float.MAX_VALUE;
        for (EnemyInfo ei : bot.kb.enemyInformation.values()) {

            if (ei.getBestVisibleEnemyPart(bot) == null) {
                continue;
            }

            float risk = CommFun.getDistanceBetweenPositions(playerPos, ei.getObjectPosition());
            if (risk < chosenRisk) {
                chosen = ei;
                chosenRisk = risk;
            }
        }
        if (chosen == null) {
            return null;
        }
        return new FiringDecision(chosen, 7);
    }

//    public int chooseWeapon(float distance) {
//        /**
//        int BLASTER = 7, SHOTGUN = 8,
//        SUPER_SHOTGUN = 9, MACHINEGUN = 10, CHAINGUN = 11, GRENADES = 12,
//        GRENADE_LAUNCHER = 13, ROCKET_LAUNCHER = 14, HYPERBLASTER = 15,
//        RAILGUN = 16, BFG10K = 17, SHELLS = 18, BULLETS = 19, CELLS = 20,
//        ROCKETS = 21, SLUGS = 22;
//         **/
//        if (bot.forcedweapon != 0) {
//            return bot.forcedweapon;
//        }
//
//        int maxWeight = -1;
//        int gunInd = 7;
//        for (int i = 7; i < 18; i++) {
//            if (!bot.botHasItem(i) || !bot.botHasItem(WeaponConfig.ammoTable[i])) {
//                continue;
//            }
//            if (distance < bot.cConfig.maxShortDistance4WpChoice && CombatConfig.isBannedForShortDistance(i)) {
//                continue;
//            }
//            if (distance > bot.cConfig.minLongDistance && CombatConfig.isBannedForLongDistance(i)) {
//                continue;
//            }
//            int weight = bot.wConfig.getWeaponWeightByInvIndex(i);
//            if (weight > maxWeight) {
//                maxWeight = weight;
//                gunInd = i;
//            }
//        }
//
//        return gunInd;
//    }

    static int continuousDistToDiscrete(double dist) {
        if (dist < 400) {
            return State.DIST_CLOSE;
        }
        if (dist < 900) {
            return State.DIST_MEDIUM;
        }
        return State.DIST_FAR;
    }

    /**
     * Returns the firing instructions
     * @param fd firing decision
     * @param bot the bot
     * @return
     */
    public FiringInstructions getFiringInstructions(FiringDecision fd) {
        if (fd == null) {
            return null;
        }

        boolean reloading = bot.getWorld().getPlayer().getPlayerGun().isCoolingDown();
        if (reloading) {
            return getNoFiringInstructions(fd.enemyInfo.getObjectPosition());
        }

        State state = getCurrentState(fd);

        if (lastState != null && lastAction != null) {
            double reward = bot.getReward();
            qf.update(lastState, lastAction, reward, state);
            if (reward != 0) System.out.println(""+lastState+" -> "+lastAction);
            if (BotStatistic.getInstance() != null) {
                BotStatistic.getInstance().addReward(bot.getBotName(), reward, bot.getFrameNumber());
            }
        }
        Action action = (Action) qf.chooseAction(state, getForbiddenActions());

        lastState = state;
        lastAction = action;
        return getFIForAction(fd, action);
    }

    HashSet<RLAction> getForbiddenActions() {
        HashSet<RLAction> ret = new HashSet<RLAction>();
        for (int i=7; i<18; i++) {
            if ( ! bot.botHasItem(i) ) ret.add(Action.getWeaponChangeAction(i));
        }
        return ret;
    }

    FiringInstructions getNoFiringInstructions(Vector3f enemyPos) {
        FiringInstructions ret = new FiringInstructions(CommFun.getNormalizedDirectionVector(bot.getBotPosition(), enemyPos));
        ret.doFire = false;
        return ret;
    }

    State getCurrentState(FiringDecision fd) {
        double dist = CommFun.getDistanceBetweenPositions(bot.getBotPosition(), fd.enemyInfo.getObjectPosition());
        State ret = new State(State.getWpnFromInventoryIndex(bot.getCurrentWeaponIndex()), continuousDistToDiscrete(dist));
        return ret;
    }

    private FiringInstructions getFIForAction(FiringDecision fd, Action action) {
        switch (action.getAction()) {
            case Action.FIRE_CURRENT:
                return new FiringInstructions(
                        CommFun.getNormalizedDirectionVector(bot.getBotPosition(), fd.enemyInfo.getObjectPosition()),
                        getTimeToHit(bot.getBotPosition(), fd.enemyInfo.getObjectPosition(), fd));
            case Action.FIRE_PREDICTED:
                return new FiringInstructions(
                        CommFun.getNormalizedDirectionVector(bot.getBotPosition(), fd.enemyInfo.predictedPos),
                        getTimeToHit(bot.getBotPosition(), fd.enemyInfo.getObjectPosition(), fd));
            case Action.FIRE_HITPOINT:
                return getFiringInstructionsAtHitpoint(fd, 1);
            case Action.NO_FIRE:
            default:
                if (action.isWeaponChange()) {
                    int wpnind = action.getWeaponChangeIndex();
                    bot.changeWeaponToIndex(wpnind);
                }
                return getNoFiringInstructions(fd.enemyInfo.getObjectPosition());
        }
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

    FiringInstructions getFiringInstructionsAtHitpoint(FiringDecision fd, double percent) {

        //We add to enemy position the movement that the enemy is predicted to do in timeToHit.
        Vector3f enemyPos = fd.enemyInfo.getObjectPosition();
        Vector3f playerPos = bot.getBotPosition();

        float timeToHit = getTimeToHit(playerPos, enemyPos, fd);

        Vector3f hitPoint = CommFun.cloneVector(enemyPos);
        //movement is between bot position, not the visible part of the bot
        Vector3f movement = CommFun.getMovementBetweenVectors(fd.enemyInfo.getObjectPosition(), fd.enemyInfo.predictedPos);
        movement = CommFun.multiplyVectorByScalar(movement, timeToHit);
        movement = CommFun.multiplyVectorByScalar(movement, (float) percent);
        hitPoint.add(movement);

        //		bot.dtalk.addToLog("Prediction shooting: @"+fd.enemyInfo.ent.getName()+" gun: "+CommFun.getGunName(fd.gunIndex)+"\n pred mov: "+movement+" timeToHit: "+timeToHit+" dist: "+distance+" bspeed: "+bulletSpeed);
        return new FiringInstructions(CommFun.getNormalizedDirectionVector(playerPos, hitPoint), timeToHit);
    }
    /**************


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
    case RLCombatModule.ACTION_0:
    return getNoFiringInstructions(fd.enemyInfo.getObjectPosition());
    case RLCombatModule.ACTION_CURRENT:

    case RLCombatModule.ACTION_PREDICTED:
    return new FiringInstructions(
    CommFun.getNormalizedDirectionVector(bot.getBotPosition(), fd.enemyInfo.predictedPos),
    getTimeToHit(bot.getBotPosition(), fd.enemyInfo.predictedPos, fd)
    );
    case RLCombatModule.ACTION_HITPOINT1:
    case RLCombatModule.ACTION_HITPOINT09:
    case RLCombatModule.ACTION_HITPOINT08:
    case RLCombatModule.ACTION_HITPOINT07:
    case RLCombatModule.ACTION_HITPOINT06:
    case RLCombatModule.ACTION_HITPOINT05:
    case RLCombatModule.ACTION_HITPOINT04:
    case RLCombatModule.ACTION_HITPOINT03:
    case RLCombatModule.ACTION_HITPOINT02:
    case RLCombatModule.ACTION_HITPOINT01:

    default:
    return null;
    }
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




     *******/
}
