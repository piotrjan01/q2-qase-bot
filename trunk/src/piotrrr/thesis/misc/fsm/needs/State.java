package piotrrr.thesis.misc.fsm.needs;


import piotrrr.thesis.misc.entities.EntityTypeDoublePair;

public interface State {
	
	public EntityTypeDoublePair [] getDesiredEntities();
	
	public State getNextState();

}
