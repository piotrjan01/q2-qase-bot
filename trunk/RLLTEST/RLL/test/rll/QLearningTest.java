/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rll;

import gui.Bot;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import testenv.Actions;
import testenv.Distance;
import testenv.Gun;
import testenv.TestAction;
import static org.junit.Assert.*;
import testenv.WorldState;

/**
 *
 * @author piotrrr
 */
public class QLearningTest {


    TestAction f = new TestAction(Actions.fire, null);
    TestAction nf = new TestAction(Actions.nofire , null);
    TestAction ch = new TestAction(Actions.changeWpn , null);

    WorldState cl_shtg = new WorldState(Distance.Close, Gun.WPN_SHOTGUN);
    WorldState fr_shtg = new WorldState(Distance.Far, Gun.WPN_SHOTGUN);
    WorldState md_shtg = new WorldState(Distance.Medium, Gun.WPN_SHOTGUN);

    WorldState cl_chg = new WorldState(Distance.Close, Gun.WPN_CHAINGUN);
    WorldState fr_chg = new WorldState(Distance.Far, Gun.WPN_CHAINGUN);
    WorldState md_chg = new WorldState(Distance.Medium, Gun.WPN_CHAINGUN);

    WorldState cl_bl = new WorldState(Distance.Close, Gun.WPN_BLASTER);
    WorldState fr_bl = new WorldState(Distance.Far, Gun.WPN_BLASTER);
    WorldState md_bl = new WorldState(Distance.Medium, Gun.WPN_BLASTER);

    
    public QLearningTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of chooseAction method, of class QLearning.
     */
    @Test
    public void testChooseAction() {
        fail();
    }

    /**
     * Test of update method, of class QLearning.
     */
    @Test
    public void testUpdate() {
        QLearning ql = getTestInstance();

        String correct = "QFunction states: 0";
        assertStrings(ql, correct);
               

        ql.update(cl_bl, nf, 0, fr_bl);
        ql.update(fr_bl, nf, 0, fr_bl);
        ql.update(fr_bl, nf, 0, fr_bl);
        ql.update(fr_bl, nf, 0, md_bl);
        ql.update(md_bl, nf, 0, md_bl);
        ql.update(md_bl, nf, 0, md_bl);
        ql.update(md_bl, nf, 0, fr_bl);
        ql.update(fr_bl, nf, 0, fr_bl);
        ql.update(fr_bl, nf, 0, fr_bl);
        ql.update(fr_bl, nf, 0, cl_bl);
        ql.update(cl_bl, nf, 0, cl_bl);

        ql.update(cl_bl, nf, 0, fr_bl);
        int h = 76550478;
        assertHash(ql, h);

        ql.update(cl_bl, f, 1, cl_bl);
        h=1158914693;
        assertHash(ql, h);

        ql.update(cl_bl, f, 1, cl_bl);
        h = 303014533;
        assertHash(ql, h);


        ql.update(cl_chg, ch, 0, cl_bl);
        ql.update(cl_chg, ch, 0, cl_shtg);
        ql.update(cl_chg, ch, 0, cl_bl);
        ql.update(cl_chg, ch, 0, cl_shtg);
        ql.update(cl_chg, ch, 0, cl_bl);
        ql.update(cl_chg, ch, 0, cl_shtg);
        ql.update(cl_chg, ch, 0, cl_bl);
        ql.update(cl_chg, ch, 0, cl_shtg);
        ql.update(cl_chg, ch, 0, cl_bl);
        ql.update(cl_chg, ch, 0, cl_shtg);
        ql.update(cl_chg, f, 1, cl_chg);
        ql.update(cl_chg, f, 1, cl_chg);
        ql.update(cl_chg, f, 1, cl_chg);
        ql.update(cl_chg, f, 1, md_chg);
        ql.update(md_chg, f, 1, md_chg);
        ql.update(md_chg, f, 1, fr_chg);
        ql.update(fr_chg, f, 1, fr_chg);
        ql.update(fr_chg, f, 1, fr_chg);
        ql.update(fr_chg, f, 1, fr_chg);
        prnh(ql.toDetailedString());

        

    }

    /**
     * Test of setParameters method, of class QLearning.
     */
    @Test
    public void testSetParameters() {
        QLearning.setParameters(0.131, 0.11, 0.111);
        assertTrue(QLearning.getBeta() == 0.11);
        assertTrue(QLearning.getExploration() == 0.111);
        assertTrue(QLearning.getGamma() == 0.131);
    }

    /**
     * Test of getStatesWithValues method, of class QLearning.
     */
    @Test
    public void testGetStatesWithValues() {
        fail();
    }


    private QLearning getTestInstance() {
        //exploration is 0.5!
        QLearning.setParameters(0.9, 0.5, 0.5);
        QLearning ql = new QLearning(nf);
        ql.setRandomSeed(0);

        return ql;
    }

    private void prnh(Object o) {
        System.out.println(o.toString());
        System.out.println("Hash: "+o.hashCode());
    }

    private void prn(Object o) {
        System.out.println(o.toString());
    }

    private void assertStrings(Object a, Object b) {
        assertTrue(b.toString().trim().equals(a.toString().trim()));
    }

    private void assertHash(Object a, int h) {
        prn(""+a.hashCode()+" ? "+h);
        assertTrue(a.hashCode() == h);
    }


}