package piotrrr.thesis.bots.rlbot;

import java.util.LinkedList;
import piotrrr.thesis.common.fsm.NeedsFSM;
import piotrrr.thesis.bots.wpmapbot.MapBotBase;
import piotrrr.thesis.common.combat.FiringDecision;
import piotrrr.thesis.common.combat.FiringInstructions;
import piotrrr.thesis.common.combat.SimpleCombatModule;
import piotrrr.thesis.common.fsm.StateBot;
import piotrrr.thesis.common.jobs.CountMyScoreJob;
import piotrrr.thesis.common.jobs.HitsReporter;
import piotrrr.thesis.common.jobs.StateReporter;
import piotrrr.thesis.common.navigation.NavInstructions;
import piotrrr.thesis.common.stats.BotStatistic;
import piotrrr.thesis.common.stats.StatsTools;

public class RlBot extends MapBotBase implements StateBot {

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


	/**
	 * Finite state machine - used to determine bot's needs.
	 */
	NeedsFSM fsm;

	/**
	 * The job that reports the bot's state and state changes.
	 */
	public StateReporter stateReporter;

        public CountMyScoreJob scoreCounter;

        public RLCombatModule combatModule = new RLCombatModule(this);

        public int lastBotScore = 0;

        public static final int lastShootingMaxSize = 30;

        public double totalReward = 0;

        public double rewardsCount = 0;

        LinkedList<Shooting> lastShootings = new LinkedList<Shooting>();

	public RlBot(String botName, String skinName) {
		super(botName, skinName);
		
		fsm = new NeedsFSM(this);
		
                stateReporter = new StateReporter(this, this);
                scoreCounter = new CountMyScoreJob(this);
		addBotJob(stateReporter);
                addBotJob(scoreCounter);

		globalNav = new RLBotGlobalNav();
		localNav = new RLBotLocalNav();
	}
	
	
	@Override
	protected void botLogic() {
		super.botLogic();
	
		NavInstructions ni = null;
		if ( ! noMove) {
			plan = globalNav.establishNewPlan(this, plan);
			if (plan == null) {
				// ??
				return;
			}
			assert plan != null;
			ni = localNav.getNavigationInstructions(this);
		}
		
		FiringDecision fd =  null;
		if ( ! noFire ) {
			fd = combatModule.getFiringDecision();
//			if (fd != null && getWeaponIndex() != fd.gunIndex) changeWeaponByInventoryIndex(fd.gunIndex);
//			else {
//				int justInCaseWeaponIndex = SimpleCombatModule.chooseWeapon(this, cConfig.maxShortDistance4WpChoice+0.1f);
//				if (getWeaponIndex() != justInCaseWeaponIndex)
//					changeWeaponByInventoryIndex(justInCaseWeaponIndex);
//			}
		}
		
		FiringInstructions fi = combatModule.getFiringInstructions(fd);
                if (fi != null && fi.doFire) {
                    lastShootings.add(new Shooting(
                                            getFrameNumber(),
                                            getFrameNumber()+(long)fi.timeToHit,
                                            fd.enemyInfo.ent.getName())
                                        );
                    if (lastShootings.size() > lastShootingMaxSize)
                        lastShootings.pollFirst();
                }
		
		executeInstructions(ni,	fi);
	}
	
	/**
	 * Returns the current name of the state of bot's finite state machine.
	 * @return state name
	 */
	public String getCurrentStateName() {
		String stateName =  fsm.getCurrentStateName();
		return stateName.substring(stateName.lastIndexOf(".")+1);
	}
	
	@Override
	public String toDetailedString() {
		
		return "Bot name: "+getBotName()+"\n"+
				"health: "+getBotHealth()+"\n"+
				"armor: "+getBotArmor()+"\n"+
				"state name: "+getCurrentStateName()+"\n"+
				"frame nr: "+getFrameNumber()+"\n"+
				"position: "+getBotPosition()+"\n"+
                                "rewards count: "+rewardsCount+"\n"+
                                "reward total: "+totalReward+"\n"+
                                "reward avg: "+totalReward/rewardsCount+"\n"+
				kb.toString();
	}



        public double getReward() {
            rewardsCount++;
            double r = 0;
            if (scoreCounter.getBotScore() > lastBotScore) {
                lastBotScore = scoreCounter.getBotScore();
                r+=1;
            }
            LinkedList<Shooting> toDelete = new LinkedList<Shooting>();
//            Dbg.prn("");
            for (Shooting s : lastShootings) {
//                Dbg.prn("shooting: "+s.enemyName+"@"+s.shotTime+"-"+s.hitTime+" ");
                int damage = HitsReporter.wasHitInGivenPeriod(s.shotTime+1, s.hitTime+2, s.enemyName);
                if (damage > 0) {
                    toDelete.add(s);
                    r+=damage/1000d;
                }
                else if (s.hitTime+10 < getFrameNumber()) toDelete.add(s);
            }
            
            lastShootings.removeAll(toDelete);
            totalReward+=r;
            if (r!=0) System.out.println("--------> Reward = "+r);
            return r;
        }
	

}