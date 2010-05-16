package piotrrr.thesis.bots.smartbot;

import java.lang.reflect.Method;
import java.util.Vector;

import piotrrr.thesis.bots.tuning.NavConfig;
import piotrrr.thesis.common.entities.EntityDoublePair;
import soc.qase.state.Entity;

public class SmartBotEntityRanking {
	
	public Vector<EntityDoublePair> getEntityRanking(SmartBot bot) throws Exception {
		Vector<EntityDoublePair> ret = new Vector<EntityDoublePair>();
		
		float hn = getNeedFromAspitationReservationValues(bot, "health");
		float an = getNeedFromAspitationReservationValues(bot, "armor");
		float fpn = getNeedFromAspitationReservationValues(bot, "firepower");
		float en = getEnemiesNeed(bot, hn, an);
		
		return ret;
	}
	
	public float getFirePowerBenefit(SmartBot bot, Entity e) {
		return 0;
	}
	
	public float getFitnessBenefit(SmartBot bot, Entity e) {
		return 0;
	}
	
	public float getEnemyCost(SmartBot bot, Entity e) {
		return 0;
	}
	
	public float getNeedFromAspitationReservationValues(SmartBot bot, String param) throws Exception {
		NavConfig nc = bot.nConfig;
		float asp = nc.getParameter(param+"Aspiration");
		float rsv = nc.getParameter(param+"Reservation");
		float val = 0;
		boolean read = false;
		String mName = "getBotParam_"+param;
		
		for (Method m : SmartBotEntityRanking.class.getMethods()) {
			if (m.getName().equals(mName)) {
				val = (Float)m.invoke(this, bot);
				read = true;
			}
		}
		if (! read) throw new Exception("Didn't find the method: "+mName);
		
		if (val > asp) return 0f;
		if (val < rsv) return 100f;
		float range = Math.abs(asp - rsv);
		float percent = Math.abs(rsv - val) / range;
		return percent*100f;
		
	}
	
	public float getEnemiesNeed(SmartBot bot, float healthNeed, float armorNeed) {
		if (healthNeed > bot.nConfig.minimalHealthOrArmorNeedsToPreventEnemySearch ||
			armorNeed > bot.nConfig.minimalHealthOrArmorNeedsToPreventEnemySearch)
			return 0f;
		else return getBotParam_firepower(bot); //else our need to meet the enemy is proportional to firepower.
	}
	
	public float getBotParam_health(SmartBot bot) {
		return bot.getBotHealth();
	}
	
	public float getBotParam_armor(SmartBot bot) {
		return bot.getBotArmor();
	}
	
	public float getBotParam_firepower(SmartBot bot) {
		return 0;
	}
	
	
	

}
