/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import pl.gdan.elsy.qconf.Brain;
import pl.gdan.elsy.qconf.Perception;
import pl.gdan.elsy.tool.Rand;
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


    Brain brain;

    double lastReward = 0;

    WorldState lastState;

    TestAction [] acts = new TestAction[Action.values().length];

    public ConnQLBot() {
        
        int i=0;
        for (Action a : Action.values())
            acts[i++] = new TestAction(a);
        brain = new Brain(this,acts);
        brain.setAlpha(0.6); //learning rate
        brain.setGamma(0.1); //discounting rate
        brain.setLambda(0.999); //trace forgetting
//        b.setUseBoltzmann(true);
//        b.setTemperature(0.001);
        brain.setRandActions(0.2); //exploration
      
    }



    public TestAction getAction(WorldState state) {
        lastReward = state.getLastReward();
        lastState = state;
        perceive();
        brain.count();
        return acts[brain.getAction()];
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
