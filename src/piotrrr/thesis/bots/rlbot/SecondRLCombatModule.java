package piotrrr.thesis.bots.rlbot;

import java.util.LinkedList;
import piotrrr.thesis.bots.rlbot.rl.Action;
import piotrrr.thesis.bots.rlbot.rl.QLearning;
import piotrrr.thesis.bots.rlbot.rl.State;
import piotrrr.thesis.common.combat.*;
import piotrrr.thesis.common.CommFun;
import piotrrr.thesis.common.jobs.HitsReporter;
import piotrrr.thesis.common.stats.BotStatistic;
import soc.qase.tools.vecmath.Vector3f;

/**
 * TODO:
 * - tylko jeden sposób strzelania? - przewidywanie z tw. sinusów.
 */
/**
 * The aiming module
 * @author Piotr Gwizda�a
 */
public class SecondRLCombatModule {

    class StateTransfer {

        long shotTime = 0;
        long hitTime = 0;
        String enemyName = null;
        State state;
        State nextState = null;
        Action action;
        double reward = 0;
        boolean done = false;
        boolean shooting;

        public StateTransfer(long shotTime, long hitTime, String enemyName, State state, Action action) {
            this.shotTime = shotTime;
            this.hitTime = hitTime;
            this.enemyName = enemyName;
            this.state = state;
            this.action = action;
            shooting = true;
        }

        public StateTransfer(State state, Action action) {
            this.state = state;
            this.action = action;
            shooting = false;
        }

    }
    RlBot bot;
    QLearning qf = new QLearning(new Action(Action.NO_FIRE));
//    State lastState = null;
//    Action lastAction = null;
    LinkedList<StateTransfer> stateTransfers = new LinkedList<StateTransfer>();
    public static final int lastShootingMaxSize = 100;

    public SecondRLCombatModule(RlBot bot) {
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

        State state = getCurrentState(fd);
        StateTransfer lastTransf = stateTransfers.isEmpty() ? null : stateTransfers.getLast();
        if (lastTransf != null && lastTransf.nextState == null) {
            lastTransf.nextState = state;
//            System.out.println("" + lastTransf.state + " + " + lastTransf.action + " -> " + lastTransf.nextState + ", r=" + lastTransf.reward);
        }

        updateRewards();
        updateQLearning();


        if (fd == null) {
            return null;
        }

        boolean reloading = bot.getWorld().getPlayer().getPlayerGun().isCoolingDown();
        if (reloading) {
            state.setReloading(true);
//            return getNoFiringInstructions(fd.enemyInfo.getObjectPosition());
        }

        Action action = (Action) qf.chooseAction(state);

        FiringInstructions fi = getFIForAction(fd, action);

        if (fi.doFire) {
            stateTransfers.add(new StateTransfer(
                    bot.getFrameNumber(),
                    bot.getFrameNumber() + (long) fi.timeToHit,
                    fd.enemyInfo.ent.getName(), state, action));
            if (stateTransfers.size() > lastShootingMaxSize) {
                stateTransfers.pollFirst();
            }
        }
        else {
            stateTransfers.add(new StateTransfer(state, action));
        }
        return fi;
    }

    FiringInstructions getNoFiringInstructions(Vector3f enemyPos) {
        FiringInstructions ret = new FiringInstructions(CommFun.getNormalizedDirectionVector(bot.getBotPosition(), enemyPos));
        ret.doFire = false;
        return ret;
    }

    State getCurrentState(FiringDecision fd) {
        if (fd == null) {
            int dist;
            if ( ! stateTransfers.isEmpty())
                dist = stateTransfers.getLast().state.getDist();
            else dist = State.DIST_MEDIUM;
            return new State(State.getWpnFromInventoryIndex(bot.getCurrentWeaponIndex()), dist, bot);
        }
        double distDbl = CommFun.getDistanceBetweenPositions(bot.getBotPosition(), fd.enemyInfo.getObjectPosition());
        int dist = continuousDistToDiscrete(distDbl);
        return new State(State.getWpnFromInventoryIndex(bot.getCurrentWeaponIndex()), dist, bot);
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
            default:
                int wpind = action.actionToInventoryIndex();
                if (bot.getCurrentWeaponIndex() != wpind) {
                    bot.changeWeaponToIndex(wpind);
                    System.out.println("chng wpn to: "+CommFun.getGunName(wpind));
                }
            case Action.NO_FIRE:
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

    public void updateQLearning() {
        LinkedList<StateTransfer> toDelete = new LinkedList<StateTransfer>();

        for (StateTransfer s : stateTransfers) {
            if ( ! s.done) continue;
                bot.rewardsCount++;
                bot.totalReward+=s.reward;
            if (s.shooting) {
                qf.update(s.state, s.action, s.reward, s.nextState);
                toDelete.add(s);
                if (BotStatistic.getInstance() != null) {
//                    System.out.println(" adding to stats: "+s.reward);
                    BotStatistic.getInstance().addReward(bot.getBotName(),
                            s.reward, bot.getFrameNumber());
                }
                if (s.reward > 0.2) {
//                    System.out.println("" + s.state + " + " + s.action + " -> " + s.nextState + ", r=" + s.reward);
                }
            }
            else {
                qf.update(s.state, s.action, s.reward, s.nextState);
//                System.out.println("" + s.state + " + " + s.action + " -> " + s.nextState + ", r=" + s.reward);
                toDelete.add(s);
            }
        }
        stateTransfers.removeAll(toDelete);
    }

    public void updateRewards() {
        if (bot.scoreCounter.getBotScore() > bot.lastBotScore) {
            bot.lastBotScore = bot.scoreCounter.getBotScore();
            //TODO: moze dac to do innego shootingu? Np. tego ktory ma hitpoint na teraz
            if ( ! stateTransfers.isEmpty()) {
                stateTransfers.getLast().reward += 1;
                stateTransfers.getLast().done = true;
            }
            else System.out.println("Bot killed, but nowhere to add the reward!!!");
        }
//            Dbg.prn("");
        for (StateTransfer s : stateTransfers) {
            if ( ! s.shooting) {
//                s.reward += -0.0001;
                s.done = true;
                continue;
            }
            int damage = HitsReporter.wasHitInGivenPeriod(s.shotTime + 1, s.hitTime + 2, s.enemyName);
            if (damage > 0) {
                s.reward += damage / 100d;
                s.done = true;
            } else if (s.hitTime + 4 < bot.getFrameNumber()) {
                s.reward += -0.02;
                s.done = true;
            }
        }
        return;
    }
}
