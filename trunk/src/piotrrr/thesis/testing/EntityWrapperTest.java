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
