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
 * The aiming module
 * @author Piotr Gwizda�a
 */
public class RLCombatModule {

    class Shooting {

        long shotTime;
        long hitTime;
        String enemyName;

        public Shooting(long shotTime, long hitTime, String enemyName) {
            this.shotTime = shotTime;
            this.hitTime = hitTime;
            this.enemyName = enemyName;
        }
    }

    RlBot bot;
    QLearning qf = new QLearning(new Action(Action.NO_FIRE));
    State lastState = null;
    Action lastAction = null;
    LinkedList<Shooting> lastShootings = new LinkedList<Shooting>();
     public static final int lastShootingMaxSize = 100;

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
            if (reward > 0.5) {
                System.out.println("" + lastState + " -> " + lastAction);
                System.out.println("--------> Reward = " + reward);
            }
            if (BotStatistic.getInstance() != null) {
                BotStatistic.getInstance().addReward(bot.getBotName(), reward, bot.getFrameNumber());
            }
        }
        Action action = (Action) qf.chooseAction(state);

        lastState = state;
        lastAction = action;
        FiringInstructions fi = getFIForAction(fd, action);

        if (fi.doFire) {
          lastShootings.add(new Shooting(
                    bot.getFrameNumber(),
                    bot.getFrameNumber() + (long) fi.timeToHit,
                    fd.enemyInfo.ent.getName()));
            if (lastShootings.size() > lastShootingMaxSize) {
                lastShootings.pollFirst();
            }
        }
        return fi;
    }

    FiringInstructions getNoFiringInstructions(Vector3f enemyPos) {
        FiringInstructions ret = new FiringInstructions(CommFun.getNormalizedDirectionVector(bot.getBotPosition(), enemyPos));
        ret.doFire = false;
        return ret;
    }

    State getCurrentState(FiringDecision fd) {
        double dist = CommFun.getDistanceBetweenPositions(bot.getBotPosition(), fd.enemyInfo.getObjectPosition());
        State ret = new State(State.getWpnFromInventoryIndex(bot.getCurrentWeaponIndex()), continuousDistToDiscrete(dist), bot);
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
            default:
                int wpind = action.actionToInventoryIndex();
                if (bot.getCurrentWeaponIndex() != wpind)
                    bot.changeWeaponToIndex(wpind);
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

    public double getReward() {
        bot.rewardsCount++;
        double r = 0;
        if (bot.scoreCounter.getBotScore() > bot.lastBotScore) {
            bot.lastBotScore = bot.scoreCounter.getBotScore();
            r += 1;
        }
        LinkedList<Shooting> toDelete = new LinkedList<Shooting>();
//            Dbg.prn("");
        for (Shooting s : lastShootings) {
//                Dbg.prn("shooting: "+s.enemyName+"@"+s.shotTime+"-"+s.hitTime+" ");
            int damage = HitsReporter.wasHitInGivenPeriod(s.shotTime + 1, s.hitTime + 2, s.enemyName);
            if (damage > 0) {
                toDelete.add(s);
                r += damage / 100d;
            } else if (s.hitTime + 4 < bot.getFrameNumber()) {
                toDelete.add(s);
                r -= 0.005;
            }
        }

        lastShootings.removeAll(toDelete);
        bot.totalReward += r;
//        if (r != 0) {
//            System.out.println("--------> Reward = " + r);
//        }
        return r;
    }

//    private int getWeaponChangeIndex() {
//       HashMap<RLState,Double> states = qf.getStatesWithValues();
//       HashMap<RLState,Double> okStates = new HashMap<RLState, Double>();
//       for (RLState s : states.keySet()) {
//           State st = (State)s;
//           if (st.getDist() == lastState.getDist())
//               if (bot.botHasItem(st.getWpnAsInventoryIndex()))
//                   okStates.put(st, states.get(st));
//       }
//       int bestGun = 7; //BLASTER
//       double max = Double.NEGATIVE_INFINITY;
//       for (RLState s : okStates.keySet()) {
//           State st = (State)s;
//           if (okStates.get(s) > max) {
//               max = okStates.get(s);
//               bestGun = st.getWpnAsInventoryIndex();
//           }
//       }
//       return bestGun;
//    }
  
}
