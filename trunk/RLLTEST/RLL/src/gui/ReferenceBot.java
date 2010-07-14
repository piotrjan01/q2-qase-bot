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
        if (Environment.isReloading()) return new TestAction(Actions.nofire);
        switch (state.getDistance()) {
            case Close:
            case Medium:
                if (state.getCurrentGun().equals(Gun.WPN_CHAINGUN))
                    return new TestAction(Actions.fire);
                else return new TestAction(Actions.WPN_CHAINGUN);
            case Far:
                if (state.getCurrentGun().equals(Gun.WPN_RAILGUN))
                    return new TestAction(Actions.fire);
                else return new TestAction(Actions.WPN_RAILGUN);
        }
        return new TestAction(Actions.nofire);
    }


}
