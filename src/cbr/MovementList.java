/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cbr;

import java.util.LinkedList;

/**
 *
 * @author piotrrr
 */
public class MovementList {

    public static double acceptableDifference = 4;

    public static int maxMovements = 10;

    public LinkedList<Movement> movements = new LinkedList<Movement>();

    public void add(Movement nm) {
        for (Movement m : movements) {
            if (m.getDifference(nm) <= acceptableDifference) {
                m.merge(nm);
                return;
            }
        }
        movements.add(nm);

        if (movements.size() > maxMovements)
            movements.removeFirst();

    }

    public double [] getBestPrediction(double [] last) {
        Movement best = null;
        double minDiff = Double.MAX_VALUE;
        for (Movement m : movements) {
            double diff = Movement.getDifference(m.last, last);
            if (minDiff > diff) {
                best = m;
                minDiff = diff;
            }
        }
        return best.next;
    }

    public double [] getBestAcceptablePrediction(double [] last) {
        Movement best = null;
        double minDiff = Double.MAX_VALUE;
        for (Movement m : movements) {
            double diff = Movement.getDifference(m.last, last);
            if (minDiff > diff && diff <= acceptableDifference) {
                best = m;
                minDiff = diff;
            }
        }
        return (best == null) ? null : best.next;
    }
    


}
