/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testenv;

import java.util.Random;

/**
 *
 * @author piotrrr
 */
public class Environment {

    public static final boolean debug = false;

    private static Random r = new Random();

    public static int frameNumber = 0;

    public static int reloadingTillFrame = -1;

    static WorldState lastState = new WorldState(Distance.Close, Gun.getRandomGun());

    public static void resetWorld() {
        reloadingTillFrame = frameNumber = 0;
        lastState = new WorldState(Distance.Close, Gun.getRandomGun());
    }

    public static WorldState getNextState(TestAction act) {
        WorldState newState = new WorldState(lastState.distance, lastState.currentGun);
        prn("got action "+act.act);
        frameNumber++;
        newState.lastReward = 0;

        Actions a = act.act;

        switch (a) {
            case changeWpn:
                newState.currentGun = act.actioner.changeWeapon(newState);
                break;
            case fire:
                if ( ! isReloading() ) {
                    prn("Firing!");
                    doFiringRoutine(newState);
                }
                else prn("Gun is reloading!");
                break;
            case nofire:
                break;
        }
        if (getRandBool(0.1)) {
            prn("Env changes");
            randomizeState(newState);
        }
        lastState = newState;
        prn("moved to state "+newState);
        prn("reloading for next "+(reloadingTillFrame-frameNumber)+" frames");
//        if (newState.lastReward != 0) System.out.println("env>>> rewarded!");
        return newState;
    }

    public static boolean isReloading() {
        return reloadingTillFrame > frameNumber;
    }

    static double getRandValFromRange(double min, double max) {
        return (min+r.nextDouble()*(max-min));
    }

    static boolean getRandBool(double trueProbability) {
        double v = r.nextDouble();
        return v < trueProbability;
    }

    static void randomizeState(WorldState state) {
        state.enemyHealth = 100;
        double v = r.nextDouble();
        if (v < 0.3) {
            state.distance = Distance.Close;
        }
        else if (v < 0.6) {
            state.distance = Distance.Medium;
        }
        else {
            state.distance = Distance.Far;
        }
        state.currentGun = Gun.getRandomGun();
    }

    private static void doFiringRoutine(WorldState state) {
        reloadingTillFrame = frameNumber+(int)state.currentGun.getReloadingTime();
        double acc = state.currentGun.getAccuracy();
        double distFactor;
        switch (state.distance) {
            case Close:
                distFactor = 0.8;
                break;
            case Medium:
                distFactor = 0.7;
                break;
            case Far:
            default:
                distFactor = 0.3;
        }
        if (getRandBool(acc * distFactor)) {
            state.enemyHealth -= state.currentGun.getDamage();
            state.lastReward += state.currentGun.getDamage()/100d;
        }
        if (state.enemyHealth < 0) {
            state.lastReward += 10;
        }
    }

    private static void prn(String s) {
        if (debug) System.out.println("env>>> "+s);
    }



}
