package piotrrr.bot.mapbot;

import java.util.Vector;

import piotrrr.bot.common.CommFun;
import piotrrr.bot.common.Dbg;
import piotrrr.bot.misc.BotBase;
import piotrrr.bot.misc.jobs.DebugTalk;
import piotrrr.bot.movement.MoveInstructions;
import piotrrr.bot.movement.MovePlan;
import soc.qase.ai.waypoint.Waypoint;
import soc.qase.state.Entity;
import soc.qase.state.PlayerMove;
import soc.qase.tools.vecmath.Vector3f;

public class MapLearningBot extends BotBase {
	
	Vector<Waypoint> observed = new Vector<Waypoint>();
	Vector3f lastCommanderPos = null;
	
	enum State { LEARN, OBSERVE, IDLE }
	
	State state = State.IDLE;
	
	String commanderName = "Piotrrr";
	String lastCommand = "";
	
	int minObservableMove = 50;
	
	/**
	 * States whether bot is in no-clip mode. 
	 * This bot to work correctly needs cheats turned on on server.
	 */
	boolean noclip = false;
	
	int reachedDistance = 50;
	
	MovePlan plan = null;

	public MapLearningBot(String arg0, String arg1) {
		super(arg0, arg1);
		Dbg.prn("Created!");
		addBotJob(new DebugTalk(this, 100));
	}

	@Override
	public void botLogic() {
		processCommand();
		MoveInstructions instr = null;
		
		switch (state) {
			case LEARN:
				break;
			case OBSERVE:
				setNoclip(true);
				if (plan == null || plan.done == true) {
					plan = getPlanForObserving();
				}
				observeCommander();
				instr = getInstructions(plan);
				break;
			default:
		}
		executeInstructions(instr);
	}
	
	private void observeCommander() {
		Entity commander = world.getOpponentByName(commanderName);
		if (commander == null) return;
		Vector3f currentPos = commander.getOrigin().toVector3f();
		if (lastCommanderPos == null) lastCommanderPos = currentPos;
		if (CommFun.getDistanceBetweenPositions(currentPos, lastCommanderPos) 
				>= minObservableMove) {
			lastCommanderPos = currentPos;
			observed.add(new Waypoint(currentPos.toOrigin()));
			Dbg.prn("New WP. Count: "+observed.size());
		}
		
	}

	private MovePlan getPlanForObserving() {
		Entity commander = world.getOpponentByName(commanderName);
		if (commander == null) { 
			say("I can't see you!"); 
			return null; 
		}
		MovePlan plan = new MovePlan(new Waypoint(commander.getOrigin()));
		if (isPositionReached(plan.dest.getPosition())) return null;
		say("Got new plan...");
		return plan;
	}
	
	private MoveInstructions getInstructions(MovePlan plan) {
		if (plan == null) return null;
		Vector3f playerPos = new Vector3f(world.getPlayer().getPlayerMove().getOrigin());
		if (isPositionReached(plan.dest.getPosition())) {
			plan.done = true;
			return null;
		}
		//else go to the direction of the goal.
		Vector3f movDir = CommFun.getNormalizedDirectionVector(playerPos, plan.dest.getPosition());	
		//Return the instructions
		return new MoveInstructions(movDir, movDir, 0.0f, 
				PlayerMove.POSTURE_NORMAL, PlayerMove.WALK_RUN);
	}
	
	private void executeInstructions(MoveInstructions mi) {
		//Do the navigation and look ad good direction 
		if (mi != null) {
			this.setBotMovement(mi.moveDir, mi.aimDir, mi.walkState, mi.postureState);
			if (mi.postureState == PlayerMove.POSTURE_JUMP && ! world.getPlayer().isJumping()) 
				this.setJump(true);
			else this.setJump(false);
		}
		else {
			this.setJump(false);
			setBotMovement(new Vector3f(0,0,0), new Vector3f(0,0,0), 
					PlayerMove.WALK_STOPPED, PlayerMove.POSTURE_NORMAL);
		}
	}
	
	private boolean isPositionReached(Vector3f pos) {
		Vector3f playerPos = new Vector3f(world.getPlayer().getPlayerMove().getOrigin());
		if (CommFun.getDistanceBetweenPositions(pos, playerPos)  <= reachedDistance) {
			return true;
		}
		return false;
	}
	
	private void processCommand() {
		String cmd = getCommand();
		if (cmd == null) return;
		cmd = cmd.trim();
		if (cmd.equals("observe")) {
			state = State.OBSERVE;
			say("New state: "+cmd);
		}
		else if (cmd.equals("learn")) {
			state = State.LEARN;
			say("New state: "+cmd);
		}
		else if (cmd.equals("idle")) {
			state = State.IDLE;
			say("New state: "+cmd);
		}
		else if (cmd.equals("disc")) {
			say("Goodbye!");
			this.disconnect();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private String getCommand() {
		String ret = "";
		Vector<String> msgs = world.getMessages();
		if (msgs == null) return null;
		for (String s : msgs) {
			if (s.startsWith(commanderName+": ")) {
				//Avoid reading the same message twice.
				ret = s.substring(commanderName.length()+2);
				if ( ! ret.equals(lastCommand))	return ret;
			}
		}
		return null;
	}
	
	private void setNoclip(boolean isOn) {
		if (noclip != isOn) {
			consoleCommand("noclip");
			noclip = isOn;
		}
	}
	
	@Override
	public void respawn() {
		super.respawn();
		noclip = false;
		plan = null;
		state = State.IDLE;
		lastCommand = "";
	}
	

}
