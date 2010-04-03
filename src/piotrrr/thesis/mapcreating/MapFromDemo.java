package piotrrr.thesis.mapcreating;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import soc.qase.ai.waypoint.WaypointMap;
import soc.qase.ai.waypoint.WaypointMapGenerator;

public class MapFromDemo {

	public static void main(String[] args) {
		prn("Current directory: "+System.getProperty("user.dir"));
		prn("Please enter a path\\name of the DM2 file: ");
		String path = readString();
		prn("Please enter the number of nodes to be generated or \n" +
				"the percent of nodes to be generated (e.g. 100 for 100 nodes or 0.5 for 50%): ");
		float nodes = Float.parseFloat(readString());
		prn("Enter the filename, where the generated map shall be saved: ");
		String saveMap = readString();
		WaypointMap map = WaypointMapGenerator.generate(path, nodes);
		map.saveMap(saveMap);
		prn("Done!");
	}
	
	static void prn(Object s) {
		System.out.println(s.toString());
	}
	
	static String readString() {
		String s;
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		try {
			s = br.readLine();
		}
		catch (Exception e) {
			prn("error reading input!");
			return readString();
		}
		return s;
	}
	
}
