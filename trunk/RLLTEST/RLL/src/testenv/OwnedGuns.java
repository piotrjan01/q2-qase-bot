/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testenv;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author piotrrr
 */
public class OwnedGuns {

    HashMap<Gun, Boolean> ownedGuns = new HashMap<Gun, Boolean>();

    public OwnedGuns() {
        for (Gun g : Gun.values())
            ownedGuns.put(g, false);
        ownedGuns.put(Gun.WPN_BLASTER, true);
    }

    public boolean ownsGun(Gun g) {
        return ownedGuns.get(g);
    }

    public OwnedGuns getRandomizedGuns() {
        OwnedGuns ng = new OwnedGuns();
         for (Map.Entry<Gun, Boolean> e : ownedGuns.entrySet()) {
            ng.ownedGuns.put(e.getKey(), e.getValue()==true);
        }
        Random r = new Random();
        Gun g = Gun.getRandomGun();
        ng.ownedGuns.put(g, r.nextBoolean());
        return ng;
    }

    @Override
    public int hashCode() {
        int h = 111;
        for (Map.Entry<Gun, Boolean> e : ownedGuns.entrySet()) {
            h += e.getKey().hashCode() + e.getKey().hashCode();
        }
        return h;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OwnedGuns other = (OwnedGuns) obj;
        if (this.ownedGuns != other.ownedGuns && (this.ownedGuns == null || !this.ownedGuns.equals(other.ownedGuns))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String s = "";
        for (Map.Entry<Gun, Boolean> e : ownedGuns.entrySet()) {
            if (e.getValue()) s+=e.getKey()+" ";
        }
        return s;
    }

    public double getSimilarity(OwnedGuns other) {
        double c = 0;
        for (Gun g : Gun.values()) {
            if (ownsGun(g) && other.ownsGun(g)) c++;
        }
        return c / Gun.values().length;
    }



    

}
