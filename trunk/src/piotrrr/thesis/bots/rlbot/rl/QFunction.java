/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package piotrrr.thesis.bots.rlbot.rl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 *
 * @author piotrrr
 */
public class QFunction {

    HashMap<RLState, HashMap<RLAction, Double>> qf = new HashMap<RLState, HashMap<RLAction, Double>>();

    RLAction defaultAction;

    private double gamma = 0.95;

    private double beta = 0.5;

    private double exploration = 0.2;

    private Random r = new Random();

    public QFunction(RLAction defaultAction) {
        this.defaultAction = defaultAction;
    }

    public RLAction chooseAction(RLState state) {
        return chooseAction(state, new HashSet<RLAction>());
    }

    public RLAction chooseAction(RLState state, HashSet<RLAction> forbiddenActions) {
        if (r.nextDouble() < exploration)
            return defaultAction.getRandomRLAction();
        if (qf.containsKey(state)) {
//            Greedy action choosing
            RLAction act = defaultAction;
            double max = Double.MIN_VALUE;
            HashMap<RLAction, Double> actions = qf.get(state);
            for (RLAction a : actions.keySet()) {
                if (actions.get(a) > max && ! forbiddenActions.contains(a)) {
                    act = a;
                    max = actions.get(a);
                }
            }
            return act;
        }
//        default action
        return defaultAction;
    }

    public void update(RLState state, RLAction action, double reward, RLState nextState) {
        double error = reward + gamma*getMaxQ(nextState) - getQ(state, action);
        setQ(state, action, getQ(state, action)+beta*error);
        
    }

    private void setQ(RLState state, RLAction action, double val) {
        
        if ( ! qf.containsKey(state)) {
            qf.put(state, new HashMap<RLAction, Double>());
        }

        HashMap<RLAction, Double> map = qf.get(state);

        if ( ! map.containsKey(action)) {
            map.put(action, val);
            return;
        }

        map.remove(action);
        map.put(action, val);
        
    }

    private double getQ(RLState state, RLAction action) {

        if ( ! qf.containsKey(state)) {
            qf.put(state, new HashMap<RLAction, Double>());
        }

        HashMap<RLAction, Double> map = qf.get(state);

        if ( ! map.containsKey(action)) {
            map.put(action, 0d);
            return 0;
        }

        return map.get(action);
        
    }

    private double getMaxQ(RLState state) {
        double max = Double.MIN_VALUE;
        if (qf.containsKey(state)) {
//            Greedy action choosing
            
            HashMap<RLAction, Double> actions = qf.get(state);
            for (RLAction a : actions.keySet()) {
                if (actions.get(a) > max)
                    max = actions.get(a);
            }
        }
        else {
            //if the state is new...
            qf.put(state, new HashMap<RLAction, Double>());
            return 0;
        }
        //if there were no actions, we return 0
        if (max == Double.MIN_VALUE) return 0;
        else return max;
    }

    @Override
    public String toString() {
        String r = "\n";
        for (RLState s : qf.keySet()) {
            r+="\nstate: "+s+"\n";
            RLAction a = chooseAction(s);
//            for (RLAction a : qf.get(s).keySet()) {
                r+=a.toString()+" -> "+qf.get(s).get(a)+"\n";
//            }
        }
        return r;
    }





}
