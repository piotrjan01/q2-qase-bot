/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rll;

import java.util.HashSet;

/**
 *
 * @author piotrrr
 */
public abstract class RLState implements Cloneable {

    public HashSet<RLAction> getForbiddenActions() {
        return new HashSet<RLAction>();
    }

}
