package piotrrr.misc.fsm.needs;


import piotrrr.misc.EntityWrapper;

public interface NeedsState {
	
	public EntityWrapper [] getDesiredEntities();
	
	public NeedsState getNextState();

}
