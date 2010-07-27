/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cbr;

import java.util.TreeMap;

/**
 *
 * @author piotrrr
 */
public class CaseBase {

    TreeMap<Integer, MovementList> cases = new TreeMap<Integer, MovementList>();

    public void addNewCase(int wpnum, double [] last, double [] next) {
        if (cases.containsKey(wpnum)) {
            MovementList l = cases.get(wpnum);
            l.add(new Movement(last, next));
        }
        else {
            MovementList l = new MovementList();
            l.add(new Movement(last, next));
            cases.put(wpnum, l);
        }
    }

    public double [] getPredictionMove(int wpnum, double [] last) {
        if (last == null) return null;
        if (cases.containsKey(wpnum)) {
            MovementList l = cases.get(wpnum);
            return l.getBestAcceptablePrediction(last);
        }
        else return null;
    }

    public void addNewCase(int wpnind, float[] x, float[] y) {
        double [] a = new double [x.length];
        double [] b = new double [y.length];
        for (int i=0; i<a.length; i++) a[i] = x[i];
        for (int i=0; i<b.length; i++) b[i] = y[i];
        addNewCase(wpnind, a, b);
    }

    public int getSize() {
        int r = 0;
        for (MovementList l : cases.values()) {
            r += l.movements.size();
        }
        return r;
    }

    public double [] getPrediction(int wpnind, float[] x) {
        if (x == null) return null;
        double [] a = new double [x.length];
        for (int i=0; i<a.length; i++) a[i] = x[i];
        return getPredictionMove(wpnind, a);
    }

}
