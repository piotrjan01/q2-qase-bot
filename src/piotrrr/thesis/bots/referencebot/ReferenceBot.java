package piotrrr.thesis.bots.referencebot;

import piotrrr.thesis.bots.wpmapbot.WPMapBot;
import piotrrr.thesis.common.jobs.StateReporter;
import soc.qase.ai.waypoint.Waypoint;

public class ReferenceBot extends WPMapBot {
	
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
