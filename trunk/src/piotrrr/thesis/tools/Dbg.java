package piotrrr.thesis.tools;

/**
 * Some static debug methods
 * @author Piotr Gwizda³a
 */
public class Dbg {

	/**
	 * Prints the given message, indicating from which class it came from
	 * @param src the class that is causing a message to appear.
	 * @param o the text or object that should be output.
	 */
	public static void prn(Object src, Object o) {
		Dbg.prn(src.getClass().toString()+": "+o.toString());
	}
	
	/**
	 * Prints the given object to standard output.
	 * @param o
	 */
	public static void prn(Object o) {
		System.out.println(o.toString());
	}
	
	/**
	 * Prints the given object to standard error output.
	 * @param o
	 */
	public static void err(Object o) {
		System.err.println(o.toString());
	}
	
}
