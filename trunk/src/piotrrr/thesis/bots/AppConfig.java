package piotrrr.thesis.bots;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

/**
 * This class is used as a starter of the bots.
 * It starts the bots programs and tells them where to connect basing
 * on given configuration.
 * 
 * TODO: create better service, that can be used by GUI.
 * 
 * @author Piotr Gwizda�a
 */
public class AppConfig {

    public static String configFileName = "config.cnf";
    /**
     * Path to Q2 main directory without slash at the end
     */
    public static String quakePath = "H:\\workspace\\inzynierka\\testing-env\\quake2-3_21\\quake2";
    /**
     * Maps dir with slash at the end
     */
    public static String botMapsDir = "H:\\workspace\\inzynierka\\SmartBot\\botmaps\\";
    public static String skinName = "male/voodoo";
    public static String altSkinName = "female/voodoo";
    public static String serverIP = "127.0.0.1";
    public static int serverPort = 27910;

    public static void writeConfig() {

        try {
            PrintWriter pw = new PrintWriter(new FileWriter(configFileName));
            pw.println(quakePath);
            pw.println(botMapsDir);
            pw.println(skinName);
            pw.println(altSkinName);
            pw.println(serverIP);
            pw.println(serverPort);
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readConfig() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(configFileName));
            quakePath = br.readLine();
            botMapsDir = br.readLine();
            skinName = br.readLine();
            altSkinName = br.readLine();
            serverIP = br.readLine();
            serverPort = Integer.parseInt(br.readLine());
            br.close();

            System.setProperty("QUAKE2", AppConfig.quakePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
