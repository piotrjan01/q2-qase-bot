package piotrrr.thesis.common;

/**
 * Some static debug methods
 * @author Piotr Gwizda³a
 */
public class Dbg {

	public static void prn(Object src, Object o) {
		Dbg.prn(src.getClass().toString()+": "+o.toString());
	}
	
	public static void prn(Object o) {
		System.out.println(o.toString());
	}
	
	public static void err(Object o) {
		System.err.println(o.toString());
	}
	
}
