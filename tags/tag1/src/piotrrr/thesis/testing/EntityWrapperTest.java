package piotrrr.thesis.testing;


public class EntityWrapperTest implements Test {
	
	class T1 implements TestCase {
		@Override
		public boolean runTestCase() {
			return false;
		}		
	}
	
	public EntityWrapperTest() {
	}

	@Override
	public boolean runTest() {
		assert new T1().runTestCase();
		return true;
	}
	
}
