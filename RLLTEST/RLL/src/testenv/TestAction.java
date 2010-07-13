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

    Bot actioner;

    public TestAction(Actions act, Bot actioner) {
        this.act = act;
        this.actioner = actioner;
    }

    @Override
    public RLAction getRandomRLAction() {
         return new TestAction(Actions.getRandomAction(), actioner);
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
