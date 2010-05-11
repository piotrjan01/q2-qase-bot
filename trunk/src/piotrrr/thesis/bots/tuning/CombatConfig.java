package piotrrr.thesis.bots.tuning;

public class CombatConfig {
	
	/**
	 * Maximum time allowed that the bullet would take to hit the target. 
	 * If time to hit is grater than this, the shooting is not performed.
	 */
	public Param<Float> MAX_TIME_TO_HIT = new Param<Float>(20f, 3f, 30f);
	
	/**
	 * Maximum error of the prediction to still consider the prediction good
	 */
	public Param<Float> MAX_PREDICTION_ERROR = new Param<Float>(60f, 10f, 200f);
	
	/**
	 * The maximal age of enemy information to be considered relevant
	 * for prediction in the firing module.
	 */
	public Param<Integer> MAX_ENEMY_INFO_AGE_4_FIRING = new Param<Integer>(2, 0, 5);
	
	/**
	 * The maximum distance to be still considered as short distance when firing
	 */
	public Param<Float> MAX_SHORT_DISTANCE_4_FIRING = new Param<Float>(100f, 10f, 500f);
	
	/**
	 * The maximum distance to be still considered as short distance when choosing a weapon
	 */
	public Param<Float> MAX_SHORT_DISTANCE_4_WP_CHOICE = new Param<Float>(400f, 100f, 800f);
	
	/**
	 * The minimum distance to be still considered as long distance
	 */
	public Param<Float> MIN_LONG_DISTANCE = new Param<Float>(800f, 500f, 1200f);
	
	/**
	 * Weapon preference weight
	 */
	public Param<Integer> WP_WEIGHT_BLASTER = new Param<Integer>(10, 0, 100);
	
	/**
	 * Weapon preference weight
	 */
	public Param<Integer> WP_WEIGHT_SHOTGUN = new Param<Integer>(30, 0, 100);
	
	/**
	 * Weapon preference weight
	 */	
	public Param<Integer> WP_WEIGHT_SUPER_SHOTGUN = new Param<Integer>(50, 0, 100);

	/**
	 * Weapon preference weight
	 */	
	public Param<Integer> WP_WEIGHT_MACHINEGUN = new Param<Integer>(60, 0, 100);

	/**
	 * Weapon preference weight
	 */	
	public Param<Integer> WP_WEIGHT_CHAINGUN = new Param<Integer>(80, 0, 100);

	/**
	 * Weapon preference weight
	 */	
	public Param<Integer> WP_WEIGHT_GRENADES = new Param<Integer>(0, 0, 100);

	/**
	 * Weapon preference weight
	 */	
	public Param<Integer> WP_WEIGHT_GRENADE_LAUNCHER = new Param<Integer>(0, 0, 100);

	/**
	 * Weapon preference weight
	 */	
	public Param<Integer> WP_WEIGHT_ROCKET_LAUNCHER = new Param<Integer>(40, 0, 100);

	/**
	 * Weapon preference weight
	 */	
	public Param<Integer> WP_WEIGHT_HYPERBLASTER = new Param<Integer>(50, 0, 100);
	
	/**
	 * Weapon preference weight
	 */	
	public Param<Integer> WP_WEIGHT_RAILGUN = new Param<Integer>(90, 0, 100);
	
	/**
	 * Weapon preference weight
	 */	
	public Param<Integer> WP_WEIGHT_BFG10K = new Param<Integer>(40, 0, 100);
	
	
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
			return WP_WEIGHT_BLASTER.value();
		case 8:
			return WP_WEIGHT_SHOTGUN.value();
		case 9:
			return WP_WEIGHT_SUPER_SHOTGUN.value();
		case 10:
			return WP_WEIGHT_MACHINEGUN.value();
		case 11:
			return WP_WEIGHT_CHAINGUN.value();
		case 12:
			return WP_WEIGHT_GRENADES.value();
		case 13:
			return WP_WEIGHT_GRENADE_LAUNCHER.value();
		case 14:
			return WP_WEIGHT_ROCKET_LAUNCHER.value();
		case 15:
			return WP_WEIGHT_HYPERBLASTER.value();
		case 16:
			return WP_WEIGHT_RAILGUN.value();
		case 17:
			return WP_WEIGHT_BFG10K.value();
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
	
}
