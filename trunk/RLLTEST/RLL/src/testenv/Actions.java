/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testenv;

import java.util.Random;
import rll.RLAction;

/**
 *
 * @author piotrrr
 */
public enum Actions {
    fire, nofire, WPN_BLASTER, WPN_SHOTGUN, WPN_SUPER_SHOTGUN, WPN_MACHINEGUN, WPN_CHAINGUN, WPN_GRENADES, WPN_GRENADE_LAUNCHER, WPN_ROCKET_LAUNCHER, WPN_HYPERBLASTER, WPN_RAILGUN, WPN_BFG10K;

    public static Actions getRandomAction() {
        Random r = new Random();
        int s = Actions.values().length;
        return Actions.values()[r.nextInt(s)];

    }


}
