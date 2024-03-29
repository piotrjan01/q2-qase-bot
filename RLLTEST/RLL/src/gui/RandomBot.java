/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import testenv.Action;
import testenv.Environment;
import testenv.Gun;
import testenv.TestAction;
import testenv.WorldState;

/**
 *
 * @author piotrrr
 */
public class RandomBot implements Bot {

    public TestAction getAction(WorldState state) {
        if (Environment.isReloading()) return new TestAction(Action.NO_ACTION);
        return new TestAction(Action.getRandomAction());
    }

    public Gun changeWeapon(WorldState state) {
        return Gun.getRandomGun();
    }

    

}
