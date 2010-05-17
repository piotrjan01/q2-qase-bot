package piotrrr.thesis.common.navigation;

import piotrrr.thesis.bots.wpmapbot.MapBotBase;

/**
 * The local navigation module of the MapBotBase
 * @author Piotr Gwizda³a
 */
public interface LocalNav {

	/**
	 * @param bot the bot for which we want to get the navigation instructions
	 * @return the navigation instructions basing on bot's plan field
	 */
	public NavInstructions getNavigationInstructions(MapBotBase bot);
	
}
