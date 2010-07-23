/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testenv;

import rll.RLState;

/**
 *
 * @author piotrrr
 */
public class WorldState extends RLState {

    Distance distance;
    Gun currentGun;
    OwnedGuns ownedGuns = new OwnedGuns();
    double lastReward = 0;
    int enemyHealth = 100;

    public WorldState(Distance distance, Gun currentGun) {
        this.distance = distance;
        this.currentGun = currentGun;
    }

    @Override
    public String toString() {
//        return "dist: "+distance+" gun: "+currentGun+" reloading: "+reloading+" reward: "+lastReward+" eh: "+enemyHealth;
        return " d=" + distance + " g=" + currentGun+" ownd="+ownedGuns;
    }

    public double getLastReward() {
        return lastReward;
    }

    public int getCurrentFrame() {
        return Environment.frameNumber;
    }

    public Distance getDistance() {
        return distance;
    }

    public Gun getCurrentGun() {
        return currentGun;
    }

    @Override
    public boolean equals(Object obj) {
        WorldState o = (WorldState) obj;
        if (currentGun.equals(o.currentGun)) {
            if (o.distance.equals(distance)) {
                if (ownedGuns.equals(o.ownedGuns))
                    return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return currentGun.hashCode() + distance.hashCode();
    }

    public OwnedGuns getOwnedGuns() {
        return ownedGuns;
    }

    


}
