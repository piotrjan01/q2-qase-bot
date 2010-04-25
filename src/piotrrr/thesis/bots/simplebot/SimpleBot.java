package piotrrr.thesis.bots.simplebot;

import piotrrr.thesis.bots.botbase.BotBase;
import piotrrr.thesis.common.combat.AimingModule;
import piotrrr.thesis.common.combat.FiringDecision;
import piotrrr.thesis.common.combat.FiringInstructions;
import piotrrr.thesis.common.jobs.BasicCommands;
import piotrrr.thesis.common.jobs.GeneralDebugTalk;
import piotrrr.thesis.common.jobs.StateReporter;
import piotrrr.thesis.common.jobs.StuckDetector;
import piotrrr.thesis.common.navigation.NavInstructions;
import piotrrr.thesis.common.navigation.NavPlan;
import piotrrr.thesis.tools.Dbg;
import soc.qase.ai.waypoint.WaypointMap;
import soc.qase.state.Action;
import soc.qase.state.Angles;
import soc.qase.state.PlayerMove;
import soc.qase.tools.vecmath.Vector3f;

/**
 * FIXME: Global TODO list.
 * 
 * Jak nie widzi waypointu do ktorego ma isc - nalezy skasowac polaczenie :)
 * 
 * Jak jest stuck, to moze spadl - niech sprobuje sie odnalezc na mapie.
 * 
 */

/**
 * This is a simple bot that will be used as a base of other bots as well as 
 * reference in comparative study.
 * @author Piotr Gwizda³a
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
	 * Bot's Knowledge Base about the environment and items it can pick up.
	 */
	public WorldKB kb = null;
	
	/**
	 * Bot's current navigation plan
	 */
	NavPlan plan = null;
	
	/**
	 * The job that reports the bot's state and state changes.
	 */
	StateReporter stateReporter;
	
	/**
	 * The job that detects when the bot is stuck.
	 */
	StuckDetector stuckDetector;
	
	/**
	 * The job that handles basic commands.
	 */
	BasicCommands basicCommands;
	
	/**
	 * Bot's job that is used to periodically say 
	 * some debug information in the game.
	 */
	public GeneralDebugTalk dtalk;
		
	
	public boolean noFire = false;
	
	/**
	 * Bot's aiming module
	 */
	AimingModule aimingModule = new SimpleAimingModule();

	/**
	 * Basic constructor.
	 * @param botName the name of the bot to be created
	 * @param skinName the name of the skin to be used
	 */
	public SimpleBot(String botName, String skinName) {
		super(botName, skinName);
		fsm = new NeedsFSM(this);

		dtalk = new GeneralDebugTalk(this, 30);
//		dtalk.active = false;
		
		stateReporter = new StateReporter(this);
		stuckDetector = new StuckDetector(this, 5);
		
		basicCommands = new BasicCommands(this, "Player");
		
		addBotJob(dtalk);
		
		addBotJob(stateReporter);
		addBotJob(basicCommands);
		addBotJob(stuckDetector);
	}
	
	public SimpleBot(String botName, String skinName, int aimingModuleNr) {
		this(botName, skinName);
		switch (aimingModuleNr) {
		case 2:
			aimingModule = new BotDePiotrAimingModule();
			break;
		case 1:
		default:
			break;
		}
	}

	@Override
	protected void botLogic() {
		super.botLogic();
		
		if (botPaused) return;
		
		if (map == null) { 
			map = WaypointMap.loadMap(MAPS_DIR+getMapName());
			assert map != null;
			kb = WorldKB.createKB(map);
			assert kb != null;
			dtalk.addToLog("KB built. Categorized items: "+kb.getKBSize()+" out of "+map.getItemNodes().size()+".");
		}
		
		kb.updateKB(world.getEntities(false), getFrameNumber(), this.getPlayerInfo().getName());
		
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
		
		
		
		FiringDecision fd =  null;
		if ( ! noFire ) fd = SimpleCombatModule.getFiringDecision(this);
		if (fd != null && getWeaponIndex() != fd.gunIndex) changeWeaponByInventoryIndex(fd.gunIndex);
		executeInstructions(
				LocalNav.getNavigationInstructions(this), 
				aimingModule.getFiringInstructions(fd, this));
		
	}
	
	
	/**
	 * Executes the instructions got from the plan.
	 * @param ni - navigation instructions.
	 */
	private void executeInstructions(NavInstructions ni, FiringInstructions fi) {
		//Do the navigation and look ad good direction
		Vector3f aimDir;
		Vector3f moveDir;
		int walk = PlayerMove.WALK_STOPPED;
		int posture = PlayerMove.POSTURE_CROUCH;
		
		if (fi != null) aimDir = fi.fireDir;
		else if (ni != null) aimDir = ni.moveDir;
		else aimDir = new Vector3f(0,0,0);
		
		if (ni != null) {
			moveDir = ni.moveDir;
			walk = ni.walkState;
			posture = ni.postureState;
		}
		else moveDir = new Vector3f(0,0,0);
		
		if (fi != null) {
			Angles arg0=new Angles(fi.fireDir.x,fi.fireDir.y,fi.fireDir.z);
			world.getPlayer().setGunAngles(arg0);
			if (fi.doFire) setAction(Action.ATTACK, true);
			else setAction(Action.ATTACK, false);
		}
		else setAction(Action.ATTACK, false); 
		
		setBotMovement(moveDir, aimDir, walk, posture);
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
		Dbg.prn(getBotName()+": I DIED!");
	}
	
	@Override
	public void handleCommand(String cmd) {
		basicCommands.handleCommand(cmd);
	}
	

}
