package piotrrr.thesis.bots.rlbot;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
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
public class RLCombatModule extends Perception {

    RLBot bot;
    double actionReward = 0;
    int actionTime = 10;
    int actionEndFrame = -1;

    public TreeMap<Integer, WeaponScore> weaponRanking = new TreeMap<Integer, WeaponScore>();
    

    public static class WeaponScore implements Comparable<WeaponScore> {
        private double rewardsTotal;
        private int rewardsCount;

        public WeaponScore(double rewardsTotal, int rewardsCount) {
            this.rewardsTotal = rewardsTotal;
            this.rewardsCount = rewardsCount;
        }

        double getScore() {
            return rewardsTotal/rewardsCount;
        }

        public int compareTo(WeaponScore o) {
            double t = getScore();
            double a = o.getScore();
            if (t>a) return -1;
            if (t<a) return 1;
            return 0;
        }


      
    }

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
    Action[] actions = Action.getAllActionsArray();
    public static final int maxShootingsCount = 100;
    Brain brain;
    double actionDistance = 0;
    boolean unipolar = true;

    public RLCombatModule(RLBot bot) {
        this.bot = bot;
        brain = new Brain(this, actions);

        brain.setAlpha(0.3); //learning rate
        brain.setGamma(0.3); //discounting rate
        brain.setLambda(0.6); //trace forgetting
//        b.setUseBoltzmann(true);
//        b.setTemperature(0.001);
        brain.setRandActions(0.05); //exploration

//        brain.setAlpha(getRandParam(0.1, 0.8, 0.1)); //learning rate
//        brain.setGamma(getRandParam(0, 0.9, 0.2)); //discounting rate
//        brain.setLambda(getRandParam(0, 0.9, 0.2)); //trace forgetting
////        b.setUseBoltzmann(true);
////        b.setTemperature(0.001);
//        brain.setRandActions(getRandParam(0.01, 0.4, 0.1)); //exploration
//        unipolar = Rand.b();

        for (int i=7; i<=18; i++) {
            weaponRanking.put(i, new WeaponScore(0, 1));
        }

    }

    double getRandParam(double from, double to, double quant) {
        int count = (int)((to-from)/quant)+1;
        Random r = new Random();
        return from+r.nextInt(count)*quant;
    }

    @Override
    public String toString() {
        String s = "alpha=" + brain.getAlpha() + " gamma=" + brain.getGamma() + 
                "\nlambda=" + brain.getLambda() + " rand=" + brain.getRandActions() +
                "\nunip=" + isUnipolar()+"\n" ;
        for (Map.Entry<Integer, WeaponScore> e : weaponRanking.entrySet()) {
            s += "\n"+CommFun.getGunName(e.getKey())+" : "+e.getValue().getScore();
        }
        return s;
    }
    

    public FiringDecision getFiringDecision() {
        Vector3f playerPos = bot.getBotPosition();
        EnemyInfo chosen = null;
        float chosenRisk = Float.MAX_VALUE;
        for (EnemyInfo ei : bot.kb.enemyInformation.values()) {

            if (ei.getBestVisibleEnemyPart(bot) == null) {
                continue;
            }

            if (ei.lastUpdateFrame + bot.cConfig.maxEnemyInfoAge4Firing < bot.getFrameNumber())
                continue;

            float dist = CommFun.getDistanceBetweenPositions(playerPos, ei.getObjectPosition());
            float error = ei.lastPredictionError / bot.cConfig.maxPredictionError;
            float risk = dist * error;
            if (risk < chosenRisk) {
                chosen = ei;
                chosenRisk = risk;
            }
        }
        if (chosen == null) {
            return null;
        }
        actionDistance = CommFun.getDistanceBetweenPositions(playerPos, chosen.getObjectPosition());
        return new FiringDecision(chosen, -1);
    }

    /**
     * Returns the firing instructions
     * @param fd firing decision
     * @param bot the bot
     * @return
     */
    public FiringInstructions getFiringInstructions(FiringDecision fd) {
        if (fd == null || fd.enemyInfo == null) {
            return null;
        }

        boolean reloading = bot.getWorld().getPlayer().getPlayerGun().isCoolingDown();
        Vector3f noFiringLook = fd.enemyInfo.predictedPos == null ? fd.enemyInfo.getObjectPosition() : fd.enemyInfo.predictedPos;
        if (reloading) {
            return getNoFiringInstructions(noFiringLook);
        }

        shootings.add(new Shooting(
                bot.getFrameNumber(),
                (long) SimpleAimingModule.getTimeToHit(
                bot.cConfig.getBulletSpeedForGivenGun(bot.getCurrentWeaponIndex()),
                bot.getBotPosition(),
                fd.enemyInfo.getObjectPosition(),
                fd.enemyInfo.predictedPos),
                fd.enemyInfo.ent.getName()));

        if (shootings.size() > maxShootingsCount) {
            shootings.removeFirst();
        }

        updateRewards();

        if (actionEndFrame == -1) {
            //first time
        }
        if (bot.getFrameNumber() >= actionEndFrame) {
            actionEndFrame = bot.getFrameNumber() + actionTime;
            //count rewards:
            bot.rewardsCount++;
            if (actionReward <= 1 && actionReward >= -1) {
                bot.totalReward += actionReward;
            } else {
                bot.totalReward += (actionReward > 0) ? 1 : -1;
                System.out.println("excessive reward! " + actionReward);
            }

            int rc = 1;
            double rwd = actionReward;
            int cwpi = bot.getCurrentWeaponIndex();
            if (weaponRanking.get(cwpi) != null) {
                rc = weaponRanking.get(cwpi).rewardsCount+1;
                rwd = weaponRanking.get(cwpi).rewardsTotal+actionReward;
            }
            weaponRanking.put(cwpi, new WeaponScore(rwd, rc));

            BotStatistic.getInstance().addReward(bot.getBotName(), actionReward, bot.getFrameNumber());
            //choose new action and execute it
            perceive();
            brain.count();
            executeAction(actions[brain.getAction()]);
            actionReward = 0;
        }

//        return getFiringInstructionsAtHitpoint(fd, 1);

        return SimpleAimingModule.getNewPredictingFiringInstructions(bot, fd, bot.cConfig.getBulletSpeedForGivenGun(bot.getCurrentWeaponIndex()));

    }

    FiringInstructions getNoFiringInstructions(Vector3f enemyPos) {
        FiringInstructions ret = new FiringInstructions(CommFun.getNormalizedDirectionVector(bot.getBotPosition(), enemyPos));
        ret.doFire = false;
        return ret;
    }

    private void executeAction(Action action) {
        if (!Action.isChangeWeaponAction(action.getAction()) || Action.isProhibited(action.getAction())) {
            return;
        }

        int wpind = action.actionToInventoryIndex();
        if (bot.getCurrentWeaponIndex() != wpind) {
            bot.changeWeaponToIndex(wpind);
//                    System.out.println("chng wpn to: "+CommFun.getGunName(wpind));
        }
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
                actionReward += (damage * damage / 100d) * 0.2;
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
        for (int i = 7; i < 18; i++) {
            setNextValue(bot.botHasItem(i));
        }
    }
}
