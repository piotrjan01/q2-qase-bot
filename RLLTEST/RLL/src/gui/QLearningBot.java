/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import rll.QLearning;
import rll.RLState;
import testenv.Action;
import testenv.Environment;
import testenv.TestAction;
import testenv.WorldState;

/**
 *
 * @author piotrrr
 */
public class QLearningBot implements Bot {



    TestAction lastAction = new TestAction(Action.NO_ACTION);

    public QLearning learner = new QLearning(new TestAction(lastAction.getAct()));
    
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
        if (Environment.isReloading()) todo = new TestAction(Action.NO_ACTION);
        else todo = (TestAction)learner.chooseAction(state);

        learner.update(lastState, lastAction, rew, state);

       

        //wykonaj akcje
        lastAction = todo;
        lastState = state;
        return new TestAction(todo.getAct());
    }


}
