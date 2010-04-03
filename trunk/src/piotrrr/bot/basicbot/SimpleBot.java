package piotrrr.bot.basicbot;

import piotrrr.bot.misc.BotBase;
import piotrrr.bot.misc.fsm.needs.NeedsFSM;
import piotrrr.bot.misc.jobs.BasicCommands;
import piotrrr.bot.misc.jobs.DebugTalk;
import piotrrr.bot.misc.jobs.StateReporter;
import soc.qase.ai.waypoint.WaypointMap;

public class SimpleBot extends BotBase {
	
	public static final String MAPS_DIR = "botmaps\\from-demo\\";

	/**
	 * Finite state machine - used to determine bot's needs.
	 */
	NeedsFSM fsm;
	
	/**
	 * The map that bot uses to navigate.
	 */
	WaypointMap map = null;

	public SimpleBot(String botName, String skinName) {
		super(botName, skinName);
		fsm = new NeedsFSM(this);
		//TODO:
		addBotJob(new DebugTalk(this, 50));
		addBotJob(new StateReporter(this));
		addBotJob(new BasicCommands(this, "Player"));
	}

	@Override
	public void botLogic() {
		super.botLogic();
		
		if (map == null) { 
			map = WaypointMap.loadMap(MAPS_DIR+getMapName());
			assert map!=null;
//			buildKnowledgeBase(); 
		}
		
		//Dbg.prn("Item nodes: "+map.getItemNodeWaypoints().length);
		
		/**
		 * This is how we do:
		 * 1. Find where to go - get plan
		 * 2. Get move instructions
		 * 3. Execute movement
		 * 4. Get firing instructions
		 * 5. Execute firing
		 */
		
		fsm.getDesiredEntities();
		
	}
	
	public String getCurrentStateName() {
		String stateName =  fsm.getCurrentStateName();
		return stateName.substring(stateName.lastIndexOf(".")+1);
	}
	
	
	

}
