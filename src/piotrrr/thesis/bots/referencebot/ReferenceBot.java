package piotrrr.thesis.bots.referencebot;

import piotrrr.thesis.bots.wpmapbot.MapBotBase;
import piotrrr.thesis.common.combat.FiringDecision;
import piotrrr.thesis.common.combat.FiringInstructions;
import piotrrr.thesis.common.combat.SimpleAimingModule;
import piotrrr.thesis.common.combat.SimpleCombatModule;
import piotrrr.thesis.common.jobs.StateReporter;
import piotrrr.thesis.common.navigation.NavInstructions;
import piotrrr.thesis.common.navigation.WorldKB;
import soc.qase.ai.waypoint.Waypoint;

public class ReferenceBot extends MapBotBase {
	
	/**
	 * Finite state machine - used to determine bot's needs.
	 */
	NeedsFSM fsm;

	/**
	 * The job that reports the bot's state and state changes.
	 */
	public StateReporter stateReporter;

	public ReferenceBot(String botName, String skinName) {
		super(botName, skinName);
		fsm = new NeedsFSM(this);
		stateReporter = new StateReporter(this);
		addBotJob(dtalk);
		addBotJob(stateReporter);
		
		globalNav = new ReferenceBotGlobalNav();
		localNav = new ReferenceBotLocalNav();
	}
	
	
	@Override
	protected void botLogic() {
		super.botLogic();
		
		if (botPaused) return;
		
		if (kb == null) { 
			kb = WorldKB.createKB(MAPS_DIR+getMapName(), this);
			assert kb != null;
			dtalk.addToLog("KB loaded!");
		}
		
		kb.updateEnemyInformation();
		
	
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
			fd = SimpleCombatModule.getFiringDecision(this);
			if (fd != null && getWeaponIndex() != fd.gunIndex) changeWeaponByInventoryIndex(fd.gunIndex);
			else {
				int justInCaseWeaponIndex = SimpleCombatModule.chooseWeapon(this, cConfig.MAX_SHORT_DISTANCE_4_WP_CHOICE.value()+0.1f);
				if (getWeaponIndex() != justInCaseWeaponIndex)
					changeWeaponByInventoryIndex(justInCaseWeaponIndex);
			}
		}
		
		FiringInstructions fi = SimpleAimingModule.getFiringInstructions(fd, this);
		
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
	public String toString() {
		
		int edgs = 0;
		for (Waypoint wp : kb.map.getAllNodes()) {
			edgs += wp.getEdges().length;
		}
		
		return "Bot name: "+getBotName()+"\n"+
				"health: "+getBotHealth()+"\n"+
				"armor: "+getBotArmor()+"\n"+
				"state name: "+getCurrentStateName()+"\n"+
				"frame nr: "+getFrameNumber()+"\n"+
				"KB size: "+kb.getKBSize()+"\n"+
				"position: "+getBotPosition()+"\n"+
				"map wps: "+kb.map.getAllNodes().length+"\n"+
				"map edges: "+edgs+"\n";
	}

}
