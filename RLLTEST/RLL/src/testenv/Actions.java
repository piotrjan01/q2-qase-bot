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
    fire, nofire, changeWpn;

    public static Actions getRandomAction() {
        Random r = new Random();
        int s = Actions.values().length;
        return Actions.values()[r.nextInt(s)];

    }


}
