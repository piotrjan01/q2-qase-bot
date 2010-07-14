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
public class Action extends RLAction {

    public static final int NO_FIRE = 0;
    public static final int FIRE_CURRENT = 1;
    public static final int FIRE_PREDICTED = 2;
    public static final int FIRE_HITPOINT = 3;
    public static final int WPN_BLASTER = 4;
    public static final int WPN_SHOTGUN = 5;
    public static final int WPN_SUPER_SHOTGUN = 6;
    public static final int WPN_MACHINEGUN = 7;
    public static final int WPN_CHAINGUN = 8;
    public static final int WPN_GRENADES = 9;
    public static final int WPN_GRENADE_LAUNCHER = 10;
    public static final int WPN_ROCKET_LAUNCHER = 11;
    public static final int WPN_HYPERBLASTER = 12;
    public static final int WPN_RAILGUN = 13;
    public static final int WPN_BFG10K = 14;


    public static final int firstAction = 0;
    public static final int actionsCount = 15;
    

    int action = 0;

    public Action(int action) {
        if (action < firstAction || action >= actionsCount) action = firstAction;
        this.action = action;
    }

    @Override
    public RLAction getRandomRLAction() {
        Random r = new Random();
        return new Action(r.nextInt(actionsCount));
    }

    public static boolean isChangeWeaponAction(int action) {
        return (action >= WPN_BLASTER && action <= WPN_BFG10K);
    }

    public int actionToInventoryIndex() {
        return actionToInventoryIndex(this.action);
    }

    public static int actionToInventoryIndex(int action) {
        if ( ! isChangeWeaponAction(action)) action = WPN_BLASTER;
        return action+3;
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
}
