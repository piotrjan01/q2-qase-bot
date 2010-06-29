/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package piotrrr.thesis.common.stats;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.TreeSet;
import soc.qase.tools.vecmath.Vector3f;

/**
 *
 * @author piotrrr
 */
public class BotStatistic implements Serializable {
    
    public static class Kill implements Serializable {
        public String killer;
        public String victim;
        public String gunUsed;
        public long time;

        public Kill(String killer, String victim, String gunUsed, long time) {
            this.killer = killer;
            this.victim = victim;
            this.gunUsed = gunUsed;
            this.time = time;
        }
    }

    public static class Pickup implements Serializable {
        public String what;
        public Vector3f where;
        public long time;

        public Pickup(String what, Vector3f where, long time) {
            this.what = what;
            this.where = where;
            this.time = time;
        }


    }

    public LinkedList<Kill> kills = new LinkedList<Kill>();

    public LinkedList<Pickup> pickups = new LinkedList<BotStatistic.Pickup>();

    public String statsInfo = "no-info";

    private static BotStatistic instance = null;



    public static BotStatistic getInstance() {
        return instance;
    }

    public static BotStatistic createNewInstance() {
        instance = new BotStatistic();
        return instance;
    }

    public void addKill(long time, String killer, String victim, String gun) {
        kills.add(new Kill(killer, victim, gun, time));
    }

    public TreeSet<String> getAllBotsNames() {
        TreeSet<String> ret = new TreeSet<String>();
        for (Kill k : kills) {
            ret.add(k.killer);
            ret.add(k.victim);
        }
        return ret;
    }

    public LinkedList<Kill> getAllKillsForGivenBot(String name) {
        LinkedList<Kill> ret = new LinkedList<BotStatistic.Kill>();
        for (Kill k : kills) {
            if (k.killer.equals(name)) ret.add(k);
        }
        return ret;
    }


    public void saveToFile(String filename) {
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static BotStatistic readFromFile(String fileName) {
        BotStatistic r = null;
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            r = (BotStatistic)ois.readObject();
        }
        catch (Exception e) {
            System.err.println("Error reading statisics: "+fileName);
        }
        return r;
    }


}
