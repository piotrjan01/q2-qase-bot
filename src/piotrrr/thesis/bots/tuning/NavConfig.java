package piotrrr.thesis.bots.tuning;

import java.lang.reflect.Field;

public class NavConfig implements Config {
	
	public Float armorAspiration = 80f;
	public Float armorAspiration_MIN = 50f;
	public Float armorAspiration_MAX = 100f;
	
	public Float armorReservation = 0f;
	public Float armorReservation_MIN = 0f;
	public Float armorReservation_MAX = 60f;
	
	
	public Float healthAspiration = 120f;
	public Float healthAspiration_MIN = 50f;
	public Float healthAspiration_MAX = 200f;
	
	public Float healthReservation = 30f;
	public Float healthReservation_MIN = 5f;
	public Float healthReservation_MAX = 90f;
	
	
	public Float firepowerAspiration = 100f;
	public Float firepowerAspiration_MIN = 70f;
	public Float firepowerAspiration_MAX = 100f;
	
	public Float firepowerReservation = 30f;
	public Float firepowerReservation_MIN = 5f;
	public Float firepowerReservation_MAX = 90f;
	
	public float minimalHealthOrArmorNeedsToPreventEnemySearch = 0.5f;
	public float minimalNeedsToPreventEnemySearch_MIN = 0.1f;
	public float minimalNeedsToPreventEnemySearch_MAX = 0.8f;

	@Override
	public float getParameter(String name) throws Exception {
		for (Field f : NavConfig.class.getDeclaredFields()) {
			if (f.getName().equals(name)) return (Float)f.get(this);
		}
		throw new Exception("Unknown field name: "+name);
	}

	@Override
	public float getParameterMax(String name) throws Exception {
		name = name+"_MAX";
		return getParameter(name);
	}

	@Override
	public float getParameterMin(String name) throws Exception {
		name = name+"_MIN";
		return getParameter(name);
	}

	@Override
	public boolean isParameterInteger(String name) throws Exception {
		for (Field f : NavConfig.class.getDeclaredFields()) {
			if (f.getName().equals(name)) {
				return (f.getType().equals(Integer.class) || f.getType().equals(int.class));
			}
		}
		throw new Exception("Unknown field name!");
	}
	
}
