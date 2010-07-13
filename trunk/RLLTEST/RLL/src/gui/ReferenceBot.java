/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import testenv.Actions;
import testenv.Environment;
import testenv.Gun;
import testenv.TestAction;
import testenv.WorldState;

/**
 *
 * @author piotrrr
 */
public class ReferenceBot implements Bot {

    public TestAction getAction(WorldState state) {
        if (Environment.isReloading()) return new TestAction(Actions.nofire, this);
        switch (state.getDistance()) {
            case Close:
            case Medium:
                if (state.getCurrentGun().equals(Gun.WPN_CHAINGUN))
                    return new TestAction(Actions.fire, this);
                else return new TestAction(Actions.changeWpn, this);
            case Far:
                if (state.getCurrentGun().equals(Gun.WPN_RAILGUN))
                    return new TestAction(Actions.fire, this);
                else return new TestAction(Actions.changeWpn, this);
        }
        return new TestAction(Actions.nofire, this);
    }

    
    public Gun changeWeapon(WorldState state) {
        switch (state.getDistance()) {
            case Close:
            case Medium:
            default:
                return Gun.WPN_CHAINGUN;
            case Far:
                return Gun.WPN_RAILGUN;
        }
    }

}
