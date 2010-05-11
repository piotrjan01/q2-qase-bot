package piotrrr.thesis.bots.smartbot;


import piotrrr.thesis.bots.botbase.BotBase;
import piotrrr.thesis.bots.simplebot.SimpleBot;
import piotrrr.thesis.bots.tuning.CombatConfig;
import piotrrr.thesis.common.combat.FiringDecision;
import piotrrr.thesis.common.combat.FiringInstructions;
import piotrrr.thesis.common.combat.SimpleCombatModule;
import piotrrr.thesis.common.jobs.BasicCommands;
import piotrrr.thesis.common.jobs.GeneralDebugTalk;
import piotrrr.thesis.common.jobs.StateReporter;
import piotrrr.thesis.common.jobs.StuckDetector;
import piotrrr.thesis.common.navigation.NavInstructions;
import piotrrr.thesis.common.navigation.NavPlan;
import piotrrr.thesis.tools.Dbg;
import soc.qase.ai.waypoint.Waypoint;
import soc.qase.state.Action;
import soc.qase.state.Angles;
import soc.qase.state.Entity;
import soc.qase.state.PlayerMove;
import soc.qase.tools.vecmath.Vector3f;


public class SmartBot extends SimpleBot {

	public SmartBot(String botName, String skinName) {
		super(botName, skinName);
	}
		
}
