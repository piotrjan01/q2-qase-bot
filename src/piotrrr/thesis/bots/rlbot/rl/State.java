/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package piotrrr.thesis.bots.rlbot.rl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 *
 * @author piotrrr
 */
public class State extends RLState {

    public static final int WPN_BLASTER = 0;
    public static final int WPN_SHOTGUN = 1;
    public static final int WPN_SUPER_SHOTGUN = 2;
    public static final int WPN_MACHINEGUN = 3;
    public static final int WPN_CHAINGUN = 4;
    public static final int WPN_GRENADES = 5;
    public static final int WPN_GRENADE_LAUNCHER = 6;
    public static final int WPN_ROCKET_LAUNCHER = 7;
    public static final int WPN_HYPERBLASTER = 8;
    public static final int WPN_RAILGUN = 9;
    public static final int WPN_BFG10K = 10;

    public static final int DIST_CLOSE = 11;
    public static final int DIST_MEDIUM = 12;
    public static final int DIST_FAR = 13;

    private int wpn;

    private int dist;

    public State(int wpn, int dist) {
        this.wpn = wpn;
        this.dist = dist;
    }

    public static int getWpnFromInventoryIndex(int index) {
        return index - 7;
    }

      @Override
    public boolean equals(Object obj) {
        State a = (State)obj;
        if (a.wpn == wpn)
            if (a.dist == dist)
                return true;
        return false;
    }

    @Override
    public int hashCode() {
        return (wpn+dist) % 256;
    }


      @Override
    public String toString() {
          String swpn = "unknown-weapon";
          String sdist = "unknown-distance";
        for (Field f : this.getClass().getFields()) {

            if (Modifier.isStatic(f.getModifiers())) {
                try {
                    if ((Integer) f.get(this) == this.wpn) {
                        swpn = f.getName();
                    }
                    if ((Integer) f.get(this) == this.dist) {
                        sdist = f.getName();
                    }
                } catch (Exception ex) {

                }
            }

        }
        return swpn+":"+sdist;
    }




}
