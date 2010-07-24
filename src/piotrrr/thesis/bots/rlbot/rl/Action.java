/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package piotrrr.thesis.bots.rlbot.rl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Random;

/**
 *
 * @author piotrrr
 */
public class Action {

    public static final int NO_ACTION = 0;
    public static final int WPN_BLASTER = 1;
    public static final int WPN_SHOTGUN = 2;
    public static final int WPN_SUPER_SHOTGUN = 3;
    public static final int WPN_MACHINEGUN = 4;
    public static final int WPN_CHAINGUN = 5;
    public static final int WPN_GRENADES = 6;
    public static final int WPN_GRENADE_LAUNCHER = 7;
    public static final int WPN_ROCKET_LAUNCHER = 8;
    public static final int WPN_HYPERBLASTER = 9;
    public static final int WPN_RAILGUN = 10;
    public static final int WPN_BFG10K = 11;


    public static final int minAct = 0;
    public static final int maxAct = 11;
    

    int action = 0;

    public Action(int action) {
        if (action < minAct || action >= maxAct) action = minAct;
        this.action = action;
    }


    public static boolean isChangeWeaponAction(int action) {
        return (action >= WPN_BLASTER && action <= WPN_BFG10K);
    }

    public int actionToInventoryIndex() {
        return actionToInventoryIndex(this.action);
    }

    public static int actionToInventoryIndex(int action) {
        if ( ! isChangeWeaponAction(action)) action = WPN_BLASTER;
        return action+6;
    }

    @Override
    public boolean equals(Object obj) {
        Action a = (Action)obj;
        if (a.action == action) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return action % 256;
    }

    public int getAction() {
        return action;
    }

    @Override
    public String toString() {
        for (Field f : this.getClass().getFields()) {
            if (Modifier.isStatic(f.getModifiers())) {
                try {
                    if ((Integer) f.get(this) == this.action) {
                        return f.getName();
                    }
                } catch (Exception ex) {

                }
            }
        }
        return "unknown action";
    }

    public static Action [] getAllActionsArray() {
        Action [] ret = new Action[maxAct-minAct+1];
        for (int i=minAct; i<=maxAct; i++) {
            ret[i-minAct] = new Action(i);
        }
        return ret;
    }
}
