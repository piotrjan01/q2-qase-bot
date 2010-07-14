/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testenv;

import gui.Bot;
import rll.RLAction;

/**
 *
 * @author piotrrr
 */
public class TestAction extends RLAction {

    Actions act;

   

    public TestAction(Actions act) {
        this.act = act;
    }

    @Override
    public RLAction getRandomRLAction() {
         return new TestAction(Actions.getRandomAction());
    }

    public Actions getAct() {
        return act;
    }

    @Override
    public String toString() {
        return act.toString();
    }

    @Override
    public boolean equals(Object obj) {
        TestAction o = (TestAction)obj;
        if (o.act.equals(act)) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return act.hashCode();
    }






    
    

}
