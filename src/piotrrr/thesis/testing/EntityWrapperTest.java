package piotrrr.thesis.testing;

import piotrrr.thesis.misc.EntityWrapper;
import soc.qase.state.Entity;

public class EntityWrapperTest implements Test {
	
	class T1 implements TestCase {
		@Override
		public boolean runTestCase() {
			EntityWrapper ew = new EntityWrapper(Entity.TYPE_AMMO, 5.0);
			assert ew.isAmmo() == true;
			assert ew.isArmor() == false;
			assert ew.isEnemy() == false;
			assert ew.isHealth() == false;
			assert ew.isWeapon() == false;
			
			ew = new EntityWrapper(Entity.CAT_PLAYERS);
			assert ew.isAmmo() == false;
			assert ew.isArmor() == false;
			assert ew.isEnemy() == true;
			assert ew.isHealth() == false;
			assert ew.isWeapon() == false;
			
			ew = new EntityWrapper(Entity.TYPE_HEALTH);
			assert ew.isAmmo() == false;
			assert ew.isArmor() == false;
			assert ew.isEnemy() == false;
			assert ew.isHealth() == true;
			assert ew.isWeapon() == false;	
			return true;
		}		
	}
	
	public EntityWrapperTest() {
	}

	@Override
	public boolean runTest() {
		new T1().runTestCase();
		return true;
	}
	
	public static void main(String[] args) {
		System.out.println("Starting...");
		EntityWrapperTest t = new EntityWrapperTest();
		assert (t.runTest()==true) : "Test failed!";
		System.out.println("Done!");
	}

}
