package piotrrr.thesis.bots.tuning;

import java.lang.reflect.Field;


public class CombatConfig implements Config {
	
	/**
	 * Maximum time allowed that the bullet would take to hit the target. 
	 * If time to hit is grater than this, the shooting is not performed.
	 */
	public float maxTimeToHit = 20f;
	public float maxTimeToHit_MIN = 3f;
	public float maxTimeToHit_MAX = 30f;
	
	/**
	 * Maximum error of the prediction to still consider the prediction good
	 */
	public float maxPredictionError = 60f;
	public float maxPredictionError_MIN = 10f;
	public float maxPredictionError_MAX = 200f;
	
	/**
	 * The maximal age of enemy information to be considered relevant
	 * for prediction in the firing module.
	 */
	public int maxEnemyInfoAge4Firing = 2;
	public int maxEnemyInfoAge4Firing_MIN = 0;
	public int maxEnemyInfoAge4Firing_MAX = 5;
	
	/**
	 * The maximum distance to be still considered as short distance when firing
	 */
	public float maxShortDistance4Firing = 100f;
	public float maxShortDistance4Firing_MIN = 10f;
	public float maxShortDistance4Firing_MAX = 500f;
	
	/**
	 * The maximum distance to be still considered as short distance when choosing a weapon
	 */
	public float maxShortDistance4WpChoice = 400f;
	public float maxShortDistance4WpChoice_MIN = 100f;
	public float maxShortDistance4WpChoice_MAX = 800f;
	
	/**
	 * The minimum distance to be still considered as long distance
	 */
	public float minLongDistance = 800f;
	public float minLongDistance_MIN = 500f;
	public float minLongDistance_MAX = 1200f;
	
	/**
	 * Weapon preference weight
	 */
	public int wpWBlaster = 10;
	public int wpWBlaster_MIN = 0;
	public int wpWBlaster_MAX = 100;
	
	/**
	 * Weapon preference weight
	 */
	public int wpWShotgun = 30;
	public int wpWShotgun_MIN = 0;
	public int wpWShotgun_MAX = 100;
	
	/**
	 * Weapon preference weight
	 */	
	public int wpWSuperShotgun = 50;
	public int wpWSuperShotgun_MIN = 0;
	public int wpWSuperShotgun_MAX = 100;

	/**
	 * Weapon preference weight
	 */	
	public int wpWMachinegun = 60;
	public int wpWMachinegun_MIN = 0;
	public int wpWMachinegun_MAX = 100;

	/**
	 * Weapon preference weight
	 */	
	public int wpWChaingun = 80;
	public int wpWChaingun_MIN = 0;
	public int wpWChaingun_MAX = 100;

	/**
	 * Weapon preference weight
	 */	
	public int wpWGrenades = 0;
	public int wpWGrenades_MIN = 0;
	public int wpWGrenades_MAX = 100;

	/**
	 * Weapon preference weight
	 */	
	public int wpWGrenadeLauncher = 0;
	public int wpWGrenadeLauncher_MIN = 0;
	public int wpWGrenadeLauncher_MAX = 100;

	/**
	 * Weapon preference weight
	 */	
	public int wpWRocketLauncher = 40;
	public int wpWRocketLauncher_MIN = 0;
	public int wpWRocketLauncher_MAX = 100;

	/**
	 * Weapon preference weight
	 */	
	public int wpWHyperblaster = 50;
	public int wpWHyperblaster_MIN = 0;
	public int wpWHyperblaster_MAX = 100;
	
	/**
	 * Weapon preference weight
	 */	
	public int wpWRailgun = 90;
	public int wpWRailgun_MIN = 0;
	public int wpWRailgun_MAX = 100;
	
	/**
	 * Weapon preference weight
	 */	
	public int wpWBFG10K = 40;
	public int wpWBFG10K_MIN = 0;
	public int wpWBFG10KMax_MAX = 100;
	
	
	/**
	 * The table that specifies which gun uses which ammunition type.
	 */
	public static final int [] ammoTable = { 0, 0, 0, 0, 0, 0, 0,
			7, //blaster - ammo for blaster is just blaster himself
			18, //shotgun
			18, //ss
			19, //mgun
			19, //chgun
			12, // granades - ammo for granates are granates themselves
			12, //g launcher
			21, //r launcher
			20, //hyperblaster - energy cells
			22, //railgun - slugs
			20 //bfgk - energy cells
	};
	
	
	/**
	int BLASTER = 7, SHOTGUN = 8,
	SUPER_SHOTGUN = 9, MACHINEGUN = 10, CHAINGUN = 11, GRENADES = 12,
	GRENADE_LAUNCHER = 13, ROCKET_LAUNCHER = 14, HYPERBLASTER = 15,
	RAILGUN = 16, BFG10K = 17, SHELLS = 18, BULLETS = 19, CELLS = 20,
	ROCKETS = 21, SLUGS = 22;
	**/
	
	/**
	 * The guns banned for the distance
	 */
	private static final int [] shortDistBanned = { 13, 12, 14, 17 };
	
	/**
	 * The guns banned for the distance
	 */
	private static final int [] longDistBanned = { 8, 9, 12, 13 };
	
	/**
	 * Checks if the given weapon is banned for short distance
	 * @param inventoryIndex - the inventory index of the weapon
	 * @return
	 */
	public static boolean isBannedForShortDistance(int inventoryIndex) {
		for (int i : shortDistBanned) 
			if (i == inventoryIndex) return true;
		return false;
	}
	
	/**
	 * Checks if the given weapon is banned for long distance
	 * @param inventoryIndex - the inventory index of the weapon
	 * @return
	 */
	public static boolean isBannedForLongDistance(int inventoryIndex) {
		for (int i : longDistBanned) 
			if (i == inventoryIndex) return true;
		return false;
	}
	
	public int getWeaponWeightByInvIndex(int ind) {
		
		/**
		int BLASTER = 7, SHOTGUN = 8,
		SUPER_SHOTGUN = 9, MACHINEGUN = 10, CHAINGUN = 11, GRENADES = 12,
		GRENADE_LAUNCHER = 13, ROCKET_LAUNCHER = 14, HYPERBLASTER = 15,
		RAILGUN = 16, BFG10K = 17, SHELLS = 18, BULLETS = 19, CELLS = 20,
		ROCKETS = 21, SLUGS = 22;
		**/
		
		switch (ind) {
		case 7:
			return wpWBlaster;
		case 8:
			return wpWShotgun;
		case 9:
			return wpWSuperShotgun;
		case 10:
			return wpWMachinegun;
		case 11:
			return wpWChaingun;
		case 12:
			return wpWGrenades;
		case 13:
			return wpWGrenadeLauncher;
		case 14:
			return wpWRocketLauncher;
		case 15:
			return wpWHyperblaster;
		case 16:
			return wpWRailgun;
		case 17:
			return wpWBFG10K;
		default:
			return 0;
		}
	}
	
	public boolean isDangerousToShootWith(int gunIndex) {
		switch (gunIndex) {
		case 17:
		case 14:
			return true;
		default:
			return false;
		}
	}
	
	public int getBulletSpeedForGivenGun(int gunIndex) {
		/**
		int BLASTER = 7, SHOTGUN = 8,
		SUPER_SHOTGUN = 9, MACHINEGUN = 10, CHAINGUN = 11, GRENADES = 12,
		GRENADE_LAUNCHER = 13, ROCKET_LAUNCHER = 14, HYPERBLASTER = 15,
		RAILGUN = 16, BFG10K = 17, SHELLS = 18, BULLETS = 19, CELLS = 20,
		ROCKETS = 21, SLUGS = 22;
		**/
		
		//These are the calibrated values. 
		int rocketSpeed = 50;
		int blasterSpeed = 100;
		int hblasterSpeed = 100;
		
		int bspeed = Integer.MAX_VALUE;
		
		switch (gunIndex) {
		
		case 7:
			bspeed = blasterSpeed;
			break;
		case 14:
		case 17:
			bspeed = rocketSpeed;
			break;
		case 15:
			bspeed = hblasterSpeed;
			break;
		case 16:
		case 8:
		case 9:
		case 10:
		case 11:
			break;
		default: 
			System.err.println("Warning: Wrong inventory gun index passed to " +
					"BasicFiringModule. Gave: "+gunIndex);
			
		}
		return bspeed;
	}
	
	@Override
	public float getParameter(String name) throws Exception {
		for (Field f : CombatConfig.class.getDeclaredFields()) {
			if (f.getName().equals(name)) return (Float)f.get(this);
		}
		throw new Exception("Unknown field name!");
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
		for (Field f : CombatConfig.class.getDeclaredFields()) {
			if (f.getName().equals(name)) {
				return (f.getType().equals(Integer.class) || f.getType().equals(int.class));
			}
		}
		throw new Exception("Unknown field name!");
	}
	
	
}
