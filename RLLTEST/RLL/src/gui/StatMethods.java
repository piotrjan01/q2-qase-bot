/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

/**
 *
 * @author piotrrr
 */
public class StatMethods {

    /**
     * Calculates the sample standard deviation of an array
     * of numbers, when the number are obtained by a random sampling.
     * Code must be modified if you have a complete set of data.
     * To get estimate of a complete data sampling, use n instead of n-1 in last line.
     *
     * see http://davidmlane.com/hyperstat/A16252.html
     *
     * @param data Numbers to compute the standard deviation of.
     * Array must contain two or more numbers.
     * @return standard deviation estimate of population
     */
    public static double sdFast(double[] data) {
        // sd is sqrt of sum of (values-mean) squared divided by n - 1
        // Calculate the mean
        double mean = 0;
        final int n = data.length;
        if (n < 2) {
            return Double.NaN;
        }
        for (int i = 0; i < n; i++) {
            mean += data[i];
        }
        mean /= n;
        // calculate the sum of squares
        double sum = 0;
        for (int i = 0; i < n; i++) {
            final double v = data[i] - mean;
            sum += v * v;
        }
        // Change to ( n - 1 ) to n if you have complete data instead of a sample.
        return Math.sqrt(sum / (n - 1));
    }

    /**
     * Calculates the sample standard deviation of an array
     * of numbers,  when the number are obtained by a random sampling.
     * Code must be modified if you have a complete set of data.
     * To get estimate of a complete sample, use n instead of n-1 in last line.
     *
     * see Knuth's The Art Of Computer Programming
     * Volume II: Seminumerical Algorithms
     * This algorithm is slower, but more resistant to error propagation.
     *
     * @param data Numbers to compute the standard deviation of.
     * Array must contain two or more numbers.
     * @return standard deviation estimate of population
     */
    public static double sdKnuth(double[] data) {
        final int n = data.length;
        if (n < 2) {
            return Double.NaN;
        }
        double avg = data[0];
        double sum = 0;
        for (int i = 1; i < data.length; i++) {
            double newavg = avg + (data[i] - avg) / (i + 1);
            sum += (data[i] - avg) * (data[i] - newavg);
            avg = newavg;
        }
        // Change to ( n - 1 ) to n if you have complete data instead of a sample.
        return Math.sqrt(sum / (n - 1));
    }

    public static double getMean(double [] d) {
        double sum = 0;
        for (int i=0; i<d.length;i++) {
            sum += d[i];
        }
        return sum / (double)d.length;
    }

    public static double getMeanSkippingZeroes(double [] d) {
        int len = d.length;
        double sum = 0;
        for (int i=0; i<d.length;i++) {
            if (d[i] == 0) len--;
            sum += d[i];
        }
        return sum / len;
    }

    /**
     * Test driver
     *
     * @param args not used
     */
    public static void main(String[] args) {
        double[] data = {10, 100, 50};
        System.out.println(sdFast(data));
        System.out.println(sdKnuth(data));
    }
}
