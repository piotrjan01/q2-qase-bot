/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testenv;

/**
 *
 * @author piotrrr
 */
public enum Distance {

    Close, Medium, Far;
    
    public int getDistanceId() {
        for (int i=0; i<Distance.values().length; i++) {
            if (Distance.values()[i] == this)
                return i;
        }
        return -1;
    }

}
