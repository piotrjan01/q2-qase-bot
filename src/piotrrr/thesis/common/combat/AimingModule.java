package piotrrr.thesis.common.combat;

import piotrrr.thesis.bots.simplebot.SimpleBot;

public interface AimingModule {
	
	FiringInstructions getFiringInstructions(FiringDecision fd, SimpleBot bot);

}
