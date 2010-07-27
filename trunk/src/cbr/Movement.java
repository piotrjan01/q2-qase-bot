/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cbr;

import piotrrr.thesis.common.CommFun;
import soc.qase.tools.vecmath.Vector3f;

/**
 *
 * @author piotrrr
 */
public class Movement {



    double [] last;

    double [] next;

    public Movement(double[] last, double[] next) {
        this.last = last;
        this.next = next;
    }

    double getDifference(Movement other) {
        return getDifference(last, other.last) + getDifference(next, other.next);
    }

    public static double getDifference(double [] a, double [] b) {
        if (a.length != b.length) return Double.NaN;
        double err = 0;
        int cnt = 0;
        for (int i=0; i<a.length; i++) {
            err += Math.abs(a[i]-b[i]);
            cnt++;
        }
        return err/cnt;
    }

    public static double getDifference(float[] a, float[] b) {
        return getDifference(floatToDouble(a), floatToDouble(b));
    }

    public static double [] getAvg(double [] a, double [] b) {
        double [] c = new double [a.length];
        for (int i=0; i<c.length; i++) {
            c[i] = (a[i] + b[i]) / 2d;
        }
        return c;
    }

    void merge(Movement nm) {
        last = getAvg(last, nm.last);
        next = getAvg(next, nm.next);
    }

    public static double [] getMovement(Vector3f from, Vector3f to) {
        if (from == null || to == null) return null;
        double [] r = new double [3];
        Vector3f m = CommFun.getMovementBetweenVectors(from, to);
        r[0] = m.x; r[1] = m.y; r[2] = m.z;
        return r;
    }

    public static double [] floatToDouble(float [] x) {
        double [] a = new double [x.length];
        for (int i=0; i<a.length; i++) a[i] = x[i];
        return a;
    }

    public static float [] doubleToFloat(double [] x) {
        float [] a = new float [x.length];
        for (int i=0; i<a.length; i++) a[i] = (float) x[i];
        return a;
    }

}
