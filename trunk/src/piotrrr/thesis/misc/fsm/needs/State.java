package piotrrr.thesis.misc.fsm.needs;


import piotrrr.thesis.misc.entities.EntityWrapper;

public interface State {
	
	public EntityWrapper [] getDesiredEntities();
	
	public State getNextState();

}
