package piotrrr.bot.misc.fsm.needs;


import piotrrr.bot.misc.EntityWrapper;

public interface NeedsState {
	
	public EntityWrapper [] getDesiredEntities();
	
	public NeedsState getNextState();

}
