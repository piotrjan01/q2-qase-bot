/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import pl.gdan.elsy.qconf.Brain;
import pl.gdan.elsy.qconf.Perception;
import rll.RLAction;
import testenv.Action;
import testenv.Distance;
import testenv.Gun;
import testenv.TestAction;
import testenv.WorldState;

/**
 *
 * @author piotrrr
 */
public class ConnQLBot extends Perception implements Bot {

    //NO_ACTION, WPN_BLASTER, WPN_SHOTGUN, WPN_SUPER_SHOTGUN, WPN_MACHINEGUN, 
    //WPN_CHAINGUN, WPN_GRENADES, WPN_GRENADE_LAUNCHER, WPN_ROCKET_LAUNCHER, 
    //WPN_HYPERBLASTER, WPN_RAILGUN, WPN_BFG10K;


    Brain b;

    double lastReward = 0;

    WorldState lastState;

    TestAction [] acts = new TestAction[Action.values().length];

    public ConnQLBot() {
        
        int i=0;
        for (Action a : Action.values())
            acts[i++] = new TestAction(a);
        b = new Brain(this,acts);
        b.setAlpha(0.2);
        b.setGamma(0.7);
        b.setLambda(0.9);
        b.setUseBoltzmann(true);
        b.setTemperature(0.02);
    }



    public TestAction getAction(WorldState state) {
        lastReward = state.getLastReward();
        lastState = state;
        perceive();
        b.count();
        return acts[b.getAction()];
    }

    @Override
    public boolean isUnipolar() {
        return true;
    }

    @Override
    public double getReward() {
        return lastReward;
    }

    @Override
    protected void updateInputValues() {
        if (lastState == null) {
            setNextValue(Gun.WPN_BLASTER.getGunId());
            for (Gun g : Gun.values())
                setNextValue(false);
            setNextValue(Distance.Close.getDistanceId());
            return;
        }
            
        setNextValue(lastState.getCurrentGun().getGunId());
        for (Gun g : Gun.values()) {
            if (lastState.getOwnedGuns().ownsGun(g))
                setNextValue(true);
            else setNextValue(false);
        }
        setNextValue(lastState.getDistance().getDistanceId());
    }

}
