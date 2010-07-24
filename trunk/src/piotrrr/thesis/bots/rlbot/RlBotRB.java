package piotrrr.thesis.bots.rlbot;

import java.util.LinkedList;
import piotrrr.thesis.common.fsm.NeedsFSM;
import piotrrr.thesis.bots.wpmapbot.MapBotBase;
import piotrrr.thesis.common.combat.FiringDecision;
import piotrrr.thesis.common.combat.FiringInstructions;
import piotrrr.thesis.common.fsm.StateBot;
import piotrrr.thesis.common.jobs.CountMyScoreJob;
import piotrrr.thesis.common.jobs.HitsReporter;
import piotrrr.thesis.common.jobs.StateReporter;
import piotrrr.thesis.common.navigation.NavInstructions;

public class RlBotRB extends MapBotBase implements StateBot {

   
    /**
     * Finite state machine - used to determine bot's needs.
     */
    NeedsFSM fsm;
    /**
     * The job that reports the bot's state and state changes.
     */
    public StateReporter stateReporter;
    public CountMyScoreJob scoreCounter;
    public SecondRLCombatModule combatModule = new SecondRLCombatModule(this);
    public int lastBotScore = 0;
   
    public double totalReward = 0;
    public double rewardsCount = 0;
    

    public RlBotRB(String botName, String skinName) {
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
        if (!noMove) {
            plan = globalNav.establishNewPlan(this, plan);
            if (plan == null) {
                // ??
                return;
            }
            assert plan != null;
            ni = localNav.getNavigationInstructions(this);
        }

        FiringDecision fd = null;
        if (!noFire) {
            fd = combatModule.getFiringDecision();
        }

        FiringInstructions fi = combatModule.getFiringInstructions(fd);
        if (fi != null && fi.doFire) {
          
        }

        executeInstructions(ni, fi);
    }

    /**
     * Returns the current name of the state of bot's finite state machine.
     * @return state name
     */
    public String getCurrentStateName() {
        String stateName = fsm.getCurrentStateName();
        return stateName.substring(stateName.lastIndexOf(".") + 1);
    }

    @Override
    public String toDetailedString() {

        return "Bot name: " + getBotName() + "\n" +
                "health: " + getBotHealth() + "\n" +
                "armor: " + getBotArmor() + "\n" +
                "state name: " + getCurrentStateName() + "\n" +
                "frame nr: " + getFrameNumber() + "\n" +
                "position: " + getBotPosition() + "\n" +
                "rewards count: " + rewardsCount + "\n" +
                "reward total: " + totalReward + "\n" +
                "reward avg: " + totalReward / rewardsCount + "\n" +
                kb.toString()+"\n\n"+combatModule.getBrainParamsString();
    }

    
}
