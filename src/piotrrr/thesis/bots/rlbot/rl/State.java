/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package piotrrr.thesis.bots.rlbot.rl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import piotrrr.thesis.bots.rlbot.RlBot;

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
    private boolean reloading = false;
    private RlBot bot;


    public State(int wpn, int dist, RlBot bot) {
        this.wpn = wpn;
        this.dist = dist;
        this.bot = bot;
    }

    public static int getWpnFromInventoryIndex(int index) {
        return index - 7;
    }

    public int getWpnAsInventoryIndex() {
        return wpn + 7;
    }

    @Override
    public boolean equals(Object obj) {
        State a = (State) obj;
        if (a.wpn == wpn) {
            if (a.reloading == reloading)
                if (a.dist == dist) {
                    return true;
                }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (wpn + dist) % 256;
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
        String srld = reloading ? "RELOADIN" : "READY";
        return swpn + ":" + sdist + ":"+srld;
    }

    public int getDist() {
        return dist;
    }

    public int getWpn() {
        return wpn;
    }

    public boolean isReloading() {
        return reloading;
    }

    public void setReloading(boolean reloading) {
        this.reloading = reloading;
    }

    

    

    @Override
    public HashSet<RLAction> getForbiddenActions() {
        HashSet<RLAction> ret = new HashSet<RLAction>();
        for (int a=Action.firstAction; a<Action.actionsCount; a++) {
            if (Action.isChangeWeaponAction(a))
                if ( ! bot.botHasItem(Action.actionToInventoryIndex(a))
                    || bot.getCurrentWeaponIndex()==Action.actionToInventoryIndex(a))
                    ret.add(new Action(a));
        }
        if (reloading) {
            ret.add(new Action(Action.FIRE_CURRENT));
            ret.add(new Action(Action.FIRE_HITPOINT));
            ret.add(new Action(Action.FIRE_PREDICTED));
        }
        return ret;
    }


}
