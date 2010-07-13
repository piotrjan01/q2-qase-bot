/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.util.HashMap;
import rll.QLearning;
import rll.Sarsa;
import rll.RLState;
import testenv.Actions;
import testenv.Environment;
import testenv.Gun;
import testenv.TestAction;
import testenv.WorldState;

/**
 *
 * @author piotrrr
 */
public class QLearningBot implements Bot {



    TestAction lastAction = new TestAction(Actions.fire, this);

    public QLearning learner = new QLearning(new TestAction(lastAction.getAct(), this));
    
    RLState lastState = Environment.getNextState(lastAction);

    

    public QLearningBot() {
    }

    

    public TestAction getAction(WorldState state) {
        

//        System.out.println("not reloading...");

        //obserwuj ostatnia nagrode
        double rew = state.getLastReward();

        
//        System.out.println(""+lastAction.getAct()+" -> "+rew);

         //wybierz akcje
        TestAction todo;
        if (Environment.isReloading()) todo = new TestAction(Actions.nofire, this);
        else todo = (TestAction)learner.chooseAction(state);

        learner.update(lastState, lastAction, rew, state);

       

        //wykonaj akcje
        lastAction = todo;
        lastState = state;
        return new TestAction(todo.getAct(), this);
    }

    public Gun changeWeapon(WorldState state) {
        HashMap<RLState, Double> states = learner.getStatesWithValues();
        WorldState best = null;
        double max = Double.NEGATIVE_INFINITY;
        for (RLState s : states.keySet()) {
            if (((WorldState)s).getDistance().equals(((WorldState)state).getDistance())) {
                if (states.get(s) > max) {
                    max = states.get(s);
                    best = (WorldState) s;
                }
            }
        }
        if (best == null) return Gun.getRandomGun();
        return best.getCurrentGun();
    }

}
