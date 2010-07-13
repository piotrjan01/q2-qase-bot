/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rll;

import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author piotrrr
 */
public class Sarsa {

    HashMap<RLState, HashMap<RLAction, Double>> qf = new HashMap<RLState, HashMap<RLAction, Double>>();

    RLAction defaultAction;

    private static double gamma = 0.95;

    private static double beta = 0.5;

    private static double exploration = 0.2;

    private Random r = new Random();

    public Sarsa(RLAction defaultAction) {
        this.defaultAction = defaultAction;
    }

    public RLAction chooseAction(RLState state) {
        if (r.nextDouble() < exploration)
            return defaultAction.getRandomRLAction();
        if (qf.containsKey(state)) {
//            Greedy action choosing
            RLAction act = defaultAction;
            double max = Double.NEGATIVE_INFINITY;
            HashMap<RLAction, Double> actions = qf.get(state);
            for (RLAction a : actions.keySet()) {
                if (actions.get(a) > max) {
                    act = a;
                    max = actions.get(a);
                }
            }
            return act;
        }
//        default action
        return defaultAction;
    }

    public void update(RLState state, RLAction action, double reward, RLState nextState, RLAction nextAction) {
        double error = reward + gamma*getQ(nextState, nextAction) - getQ(state, action);
//        if (error != 0) {
//            System.out.println(""+state+" + "+action+" -> "+nextState+" err="+error);
//        }
        setQ(state, action, getQ(state, action)+beta*error);
//        beta = beta*(1-Double.MIN_VALUE);
//        exploration = exploration*(1-Double.MIN_VALUE);
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
        double max = Double.NEGATIVE_INFINITY;
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
        if (max == Double.NEGATIVE_INFINITY) return 0;
        else return max;
    }

    @Override
    public String toString() {
        String ret = "QFunction states: "+qf.keySet().size()+"\n";
        for (RLState s : qf.keySet()) {
            ret+="\nstate: "+s+" actions: "+qf.get(s).keySet().size()+"\n";
            RLAction besta = null;
            double maxq = Double.NEGATIVE_INFINITY;
            for (RLAction a : qf.get(s).keySet()) {
                if (qf.get(s).get(a).isNaN() || qf.get(s).get(a).isInfinite())
                    ret+="\n NaN or Inf !!\n";
                if (qf.get(s).get(a) > maxq) {
                    maxq = qf.get(s).get(a);
                    besta = a;
                }
            }
            if (besta != null)
                ret+=besta.toString()+" -> "+qf.get(s).get(besta)+"\n";
            else ret+= "NOACTIONS";
        }
        return ret;
    }

    public static void setParameters(double gamma, double beta, double exploration) {
        Sarsa.gamma = gamma;
        Sarsa.beta = beta;
        Sarsa.exploration = exploration;
    }


    public HashMap<RLState, Double> getStatesWithValues() {
        HashMap<RLState, Double> ret = new HashMap<RLState, Double>();
        for (RLState s : qf.keySet()) {
            Double v = getMaxQ(s);
            ret.put(s, v);
        }
        return ret;
    }




}
