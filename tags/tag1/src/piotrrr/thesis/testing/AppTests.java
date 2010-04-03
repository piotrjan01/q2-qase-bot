package piotrrr.thesis.testing;

/**
 * All application tests.
 * @author Piotr Gwizda³a
 */
public class AppTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting...");
		
		EntityWrapperTest t = new EntityWrapperTest();
		assert (t.runTest()==true) : "EntityWrapperTest failed!";
		
		System.out.println("Done!");
	}

}
