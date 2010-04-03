package piotrrr.thesis.misc.fsm.needs;


import piotrrr.thesis.misc.EntityWrapper;

public interface NeedsState {
	
	public EntityWrapper [] getDesiredEntities();
	
	public NeedsState getNextState();

}
