package piotrrr.thesis.bots.rlbot;

import java.util.LinkedList;
import piotrrr.thesis.bots.rlbot.rl.Action;
import piotrrr.thesis.bots.rlbot.rl.State;
import piotrrr.thesis.common.combat.*;
import piotrrr.thesis.common.CommFun;
import piotrrr.thesis.common.jobs.HitsReporter;
import piotrrr.thesis.common.stats.BotStatistic;
import pl.gdan.elsy.qconf.Brain;
import pl.gdan.elsy.qconf.Perception;
import pl.gdan.elsy.tool.Rand;
import soc.qase.tools.vecmath.Vector3f;

/**
 * TODO:
 * - tylko jeden sposób strzelania? - przewidywanie z tw. sinusów.
 */
/**
 * The aiming module
 * @author Piotr Gwizda�a
 */
public class SecondRLCombatModule extends Perception {

   
    RLBot bot;

    double actionReward = 0;

    int actionTime = 10;

    int actionEndFrame = -1;

    class Shooting {

        long shotTime = 0;
        long hitTime = 0;
        String enemyName = null;
        double reward = 0;

        public Shooting(long shotTime, long hitTime, String enemyName) {
            this.shotTime = shotTime;
            this.hitTime = hitTime;
            this.enemyName = enemyName;
        }

    }

    LinkedList<Shooting> shootings = new LinkedList<Shooting>();

    Action [] actions = Action.getAllActionsArray();

    public static final int maxShootingsCount = 100;

    Brain brain;

    double actionDistance = 0;

    boolean unipolar = true;

    public SecondRLCombatModule(RLBot bot) {
        this.bot = bot;
        brain = new Brain(this, actions);

        brain.setAlpha(0.75); //learning rate
        brain.setGamma(0.6); //discounting rate
        brain.setLambda(0.4); //trace forgetting
//        b.setUseBoltzmann(true);
//        b.setTemperature(0.001);
        brain.setRandActions(0.15); //exploration

//          brain.setAlpha(Rand.d(0.1, 0.8)); //learning rate
//        brain.setGamma(Rand.d(0, 0.9)); //discounting rate
//        brain.setLambda(Rand.d(0, 0.8)); //trace forgetting
////        b.setUseBoltzmann(true);
////        b.setTemperature(0.001);
//        brain.setRandActions(Rand.d(0.05, 0.4)); //exploration
//        unipolar = Rand.b();
    }

    public String getBrainParamsString() {
        String s = "alpha="+brain.getAlpha()+" gamma="+brain.getGamma()+" lambda="+brain.getLambda()+" rand="+brain.getRandActions()+" unip="+isUnipolar();
        return s;
    }

    public FiringDecision getFiringDecision() {
        Vector3f playerPos = bot.getBotPosition();
        EnemyInfo chosen = null;
        float chosenDist = Float.MAX_VALUE;
        for (EnemyInfo ei : bot.kb.enemyInformation.values()) {

            if (ei.getBestVisibleEnemyPart(bot) == null) {
                continue;
            }

            float dist = CommFun.getDistanceBetweenPositions(playerPos, ei.getObjectPosition());
            if (dist < chosenDist) {
                chosen = ei;
                chosenDist = dist;
            }
        }
        actionDistance = chosenDist;
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
        if (fd == null || fd.enemyInfo == null) return null;

        boolean reloading = bot.getWorld().getPlayer().getPlayerGun().isCoolingDown();
        Vector3f noFiringLook = fd.enemyInfo.predictedPos == null ? fd.enemyInfo.getObjectPosition() : fd.enemyInfo.predictedPos;
        if (reloading) return getNoFiringInstructions(noFiringLook);

        shootings.add(new Shooting(bot.getFrameNumber(), (long) getTimeToHit(bot.getBotPosition(), fd.enemyInfo.getObjectPosition(), fd), fd.enemyInfo.ent.getName()));
        if (shootings.size() > maxShootingsCount) shootings.removeFirst();

        updateRewards();

        if (actionEndFrame == -1) {
            //first time
        }
        if (bot.getFrameNumber() >= actionEndFrame) {
            actionEndFrame = bot.getFrameNumber() + actionTime;
            //count rewards:
            bot.rewardsCount++;
            if (actionReward <= 1 && actionReward >= -1)
                bot.totalReward += actionReward;
            else {
                bot.totalReward += (actionReward > 0) ? 1 : -1;
                System.out.println("excessive reward! "+actionReward);
            }
            
            BotStatistic.getInstance().addReward(bot.getBotName(), actionReward, bot.getFrameNumber());
            //choose new action and execute it
            perceive();
            brain.count();
            executeAction(actions[brain.getAction()]);
            actionReward = 0;
        }

//        return getFiringInstructionsAtHitpoint(fd, 1);

        return SimpleAimingModule.getPredictingFiringInstructions(bot, fd, bot.cConfig.getBulletSpeedForGivenGun(fd.gunIndex),
				false);

    }

    FiringInstructions getNoFiringInstructions(Vector3f enemyPos) {
        FiringInstructions ret = new FiringInstructions(CommFun.getNormalizedDirectionVector(bot.getBotPosition(), enemyPos));
        ret.doFire = false;
        return ret;
    }

   
    private void executeAction(Action action) {
        if ( ! Action.isChangeWeaponAction(action.getAction())) return;
        
                int wpind = action.actionToInventoryIndex();
                if (bot.getCurrentWeaponIndex() != wpind) {
                    bot.changeWeaponToIndex(wpind);
//                    System.out.println("chng wpn to: "+CommFun.getGunName(wpind));
                }
    }

    private float getTimeToHit(Vector3f playerPos, Vector3f enemyPos, FiringDecision fd) {
        float distance = CommFun.getDistanceBetweenPositions(playerPos, enemyPos);
        float bulletSpeed = bot.cConfig.getBulletSpeedForGivenGun(fd.gunIndex);
        //Calculate the time to hit
        float timeToHit = distance / bulletSpeed;
        if (timeToHit <= 1.5) {
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

    public void updateRewards() {
        if (bot.scoreCounter.getBotScore() > bot.lastBotScore) {
            bot.lastBotScore = bot.scoreCounter.getBotScore();
            //TODO: moze dac to do innego shootingu? Np. tego ktory ma hitpoint na teraz
            actionReward += 0.4;
        }
//            Dbg.prn("");
        LinkedList<Shooting> toDelete = new LinkedList<Shooting>();
        for (Shooting s : shootings) {
        
            int damage = HitsReporter.wasHitInGivenPeriod(s.shotTime + 1, s.hitTime + 2, s.enemyName);
            if (damage > 0) {
                actionReward += (damage*damage / 100d)*0.2;
            } else if (s.hitTime + 4 < bot.getFrameNumber()) {
                toDelete.add(s);
//                actionReward -= 0.001;
            }
        }
        shootings.removeAll(toDelete);
        return;
    }

    @Override
    public boolean isUnipolar() {
        return unipolar;
    }

    @Override
    public double getReward() {
        return actionReward;
    }

    @Override
    protected void updateInputValues() {
        setNextValue(bot.getCurrentWeaponIndex());
        setNextValue(actionDistance);
        for (int i=7; i<18; i++) {
            setNextValue(bot.botHasItem(i));
        }
    }
}
