/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package piotrrr.thesis.bots.rlbot.rl;

import java.util.HashSet;

/**
 *
 * @author piotrrr
 */
public abstract class RLState {
    public HashSet<RLAction> getForbiddenActions() {
        return new HashSet<RLAction>();
    }
}
