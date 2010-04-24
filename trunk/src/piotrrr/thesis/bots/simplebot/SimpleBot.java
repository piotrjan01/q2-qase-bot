package piotrrr.thesis.bots.simplebot;

import piotrrr.thesis.bots.botbase.BotBase;
import piotrrr.thesis.common.jobs.BasicCommands;
import piotrrr.thesis.common.jobs.DebugTalk;
import piotrrr.thesis.common.jobs.StateReporter;
import piotrrr.thesis.common.jobs.StuckDetector;
import piotrrr.thesis.common.navigation.NavInstructions;
import piotrrr.thesis.common.navigation.NavPlan;
import soc.qase.ai.waypoint.WaypointMap;
import soc.qase.state.PlayerMove;
import soc.qase.tools.vecmath.Vector3f;

/**
 * This is a simple bot that will be used as a base of other bots as well as 
 * reference in comparative study.
 * @author Piotr Gwizda�a
 */
public class SimpleBot extends BotBase {
	
	/**
	 * The directory where bot's maps are stored. Relative to main directory.
	 */
	static final String MAPS_DIR = "H:\\workspace\\inzynierka\\SmartBot\\botmaps\\from-demo\\";

	/**
	 * Finite state machine - used to determine bot's needs.
	 */
	NeedsFSM fsm;
	
	/**
	 * The map that bot uses to navigate.
	 */
	WaypointMap map = null;
	
	/**
	 * Bot's Knowledge Base
	 */
	SimpleKB kb = null;
	
	/**
	 * Bot's current navigation plan
	 */
	NavPlan plan = null;
	
	StateReporter stateReporter;
	
	StuckDetector stuckDetector;
	
	/**
	 * Bot's job that is used to periodically say 
	 * some debug information in the game.
	 */
	public DebugTalk dtalk;

	/**
	 * Basic constructor.
	 * @param botName the name of the bot to be created
	 * @param skinName the name of the skin to be used
	 */
	public SimpleBot(String botName, String skinName) {
		super(botName, skinName);
		fsm = new NeedsFSM(this);

		dtalk = new DebugTalk(this, 30);
		stateReporter = new StateReporter(this);
		stuckDetector = new StuckDetector(this, 5);
		
		addBotJob(dtalk);
		addBotJob(stateReporter);
		addBotJob(new BasicCommands(this, "Player"));
		addBotJob(stuckDetector);
	}

	@Override
	protected void botLogic() {
		super.botLogic();
		
		if (map == null) { 
			map = WaypointMap.loadMap(MAPS_DIR+getMapName());
			assert map != null;
			kb = SimpleKB.createKB(map);
			assert kb != null;
			dtalk.addToLog("KB built. Categorized items: "+kb.getKBSize()+" out of "+map.getItemNodes().size()+".");
		}
		
		kb.updateKB(world.getEntities(false), getFrameNumber());
		
		/**
		 * This is how we do:
		 * 1. Find where to go - get plan
		 * 2. Get move instructions
		 * 3. Execute movement
		 * 4. Get firing instructions
		 * 5. Execute firing
		 */
		
		plan = GlobalNav.establishNewPlan(this, plan);
		if (plan == null) {
			// ??
			return;
		}
		assert plan != null;
		
		executeInstructions(LocalNav.getNavigationInstructions(this));
		
	}
	
	
	/**
	 * Executes the instructions got from the plan.
	 * @param ni - navigation instructions.
	 */
	private void executeInstructions(NavInstructions ni) {
		//Do the navigation and look ad good direction	
		if (ni != null) {
			setBotMovement(ni.moveDir, ni.aimDir, 
					ni.walkState, ni.postureState);
		}
		else {
			setBotMovement(new Vector3f(0,0,0), null, 
					PlayerMove.WALK_STOPPED, PlayerMove.POSTURE_CROUCH);
		}
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
	public void respawn() {
		super.respawn();
		plan = null;
	}

}
