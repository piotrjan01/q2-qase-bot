/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package piotrrr.thesis.bots.rlbot.rl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author piotrrr
 */
public class QLearning {

   

    HashMap<RLState, HashMap<RLAction, Double>> qf = new HashMap<RLState, HashMap<RLAction, Double>>();

    RLAction defaultAction;

    private static double gamma = 0.95;

    private static double beta = 0.5;

    private static double exploration = 0.2;

    private Random r = new Random();

    public QLearning(RLAction defaultAction) {
        this.defaultAction = defaultAction;
    }

    public RLAction chooseAction(RLState state) {
        return chooseAction(state, state.getForbiddenActions());
    }

    public synchronized RLAction chooseAction(RLState state, HashSet<RLAction> forbidden) {
        if (r.nextDouble() < exploration)
            return defaultAction.getRandomRLAction();
        if (qf.containsKey(state)) {
//            Greedy action choosing
            RLAction act = defaultAction.getRandomRLAction();
            double max = Double.NEGATIVE_INFINITY;
            HashMap<RLAction, Double> actions = qf.get(state);
            for (RLAction a : actions.keySet()) {
                if (actions.get(a) > max && ! forbidden.contains(a)) {
                    act = a;
                    max = actions.get(a);
                }
            }
            return act;
        }
//        default action
        return defaultAction.getRandomRLAction();
    }

    public void update(RLState state, RLAction action, double reward, RLState nextState) {
        double qsa = getQ(state, action);
        double maxq = getMaxQ(nextState);
        double error = reward + gamma*maxq - qsa;

//        System.out.println("\nqsa="+qsa);
//        System.out.println("r="+reward);
//        System.out.println("gamma="+gamma);
//        System.out.println("r="+reward);
//        System.out.println("maxq="+maxq);
//        System.out.println("error="+error);
//        System.out.println("beta="+beta);
//        System.out.println("q'="+(qsa+beta*error));
        setQ(state, action, qsa+beta*error);

    }

    synchronized void setQ(RLState state, RLAction action, double val) {

        if ( ! qf.containsKey(state)) {
            qf.put(state, new HashMap<RLAction, Double>());
        }

        HashMap<RLAction, Double> map = qf.get(state);

        map.put(action, val);

    }

    synchronized double getQ(RLState state, RLAction action) {

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

    synchronized double getMaxQ(RLState state) {
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
    synchronized public String toString() {

        double nstates = qf.keySet().size();
        double avgActions = 0;
        for (RLState s : qf.keySet()) {
            avgActions+= qf.get(s).keySet().size();
        }
        avgActions /= nstates;
        String ret = "QFunction states: "+nstates+" avg actions:"+avgActions+"\n";
        ret+="gamma="+gamma+" beta="+beta+" explor="+exploration;
        double oldexp = exploration;
        exploration = 0;
        RLAction b;
        for (RLState s : qf.keySet()) {
            b = chooseAction(s);
            ret+=""+s+" -> "+b+"\n";
        }
        exploration = oldexp;
        return ret;
    }

    synchronized public String toDetailedString() {
        String ret = "QFunction states: "+qf.keySet().size()+"\n";
        for (RLState s : qf.keySet()) {
            ret+="\nstate: "+s+" actions: "+qf.get(s).keySet().size()+"\n";
            for (RLAction a : qf.get(s).keySet()) {
                ret+=a.toString()+" -> "+qf.get(s).get(a)+"\n";
            }
        }
        return ret;
    }

    public static void setParameters(double gamma, double beta, double exploration) {
        QLearning.gamma = gamma;
        QLearning.beta = beta;
        QLearning.exploration = exploration;
    }

    public static double getGamma() {
        return gamma;
    }

    public static double getBeta() {
        return beta;
    }

    public static double getExploration() {
        return exploration;
    }

    synchronized public HashMap<RLState, Double> getStatesWithValues() {
        HashMap<RLState, Double> ret = new HashMap<RLState, Double>();
        for (RLState s : qf.keySet()) {
            Double v = getMaxQ(s);
            ret.put(s, v);
        }
        return ret;
    }

    public void setRandomSeed(int seed) {
        r = new Random(seed);
    }

    @Override
    synchronized public int hashCode() {
        int h = 111;
        for (Map.Entry<RLState, HashMap<RLAction, Double>> be : qf.entrySet()) {
            for (Map.Entry<RLAction, Double> se : be.getValue().entrySet()) {
                h+=se.getValue().hashCode()+se.getKey().hashCode();
            }
        }
        return h;
    }

    @Override
   synchronized public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QLearning other = (QLearning) obj;
        if (this.qf != other.qf && (this.qf == null || !this.qf.equals(other.qf))) {
            return false;
        }
        if (this.defaultAction != other.defaultAction && (this.defaultAction == null || !this.defaultAction.equals(other.defaultAction))) {
            return false;
        }
        return true;
    }

     public static String getParametersString() {
        return "gamma="+gamma+" beta="+beta+" explor="+exploration;
    }


}
