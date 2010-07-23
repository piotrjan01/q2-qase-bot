package testenv;

import java.util.Random;

public enum Gun {

    WPN_BLASTER, WPN_SHOTGUN, WPN_SUPER_SHOTGUN, WPN_MACHINEGUN, WPN_CHAINGUN, WPN_GRENADES, WPN_GRENADE_LAUNCHER, WPN_ROCKET_LAUNCHER, WPN_HYPERBLASTER, WPN_RAILGUN, WPN_BFG10K;

    public static Gun getRandomGun() {
        Random r = new Random();
        int s = Gun.values().length;
        return Gun.values()[r.nextInt(s)];
    }

    public int getGunId() {
        for (int i=0; i<Gun.values().length; i++) {
            if (Gun.values()[i] == this)
                return i;
        }
        return -1;
    }

    public double getReloadingTime() {
        switch (this) {
            case WPN_BFG10K:
                return 30;
            case WPN_BLASTER:
                return 5;
            case WPN_CHAINGUN:
                return 1;
            case WPN_GRENADES:
                return 30;
            case WPN_GRENADE_LAUNCHER:
                return 10;
            case WPN_HYPERBLASTER:
                return 2;
            case WPN_MACHINEGUN:
                return 2;
            case WPN_RAILGUN:
                return 20;
            case WPN_ROCKET_LAUNCHER:
                return 20;
            case WPN_SHOTGUN:
                return 10;
            case WPN_SUPER_SHOTGUN:
                return 15;
            default:
                return 0;
        }
    }

    public double getAccuracy() {
        switch (this) {
            case WPN_BFG10K:
                return 0.3;
            case WPN_BLASTER:
                return 0.4;
            case WPN_CHAINGUN:
                return 0.7;
            case WPN_GRENADES:
                return 0.1;
            case WPN_GRENADE_LAUNCHER:
                return 0.2;
            case WPN_HYPERBLASTER:
                return 0.4;
            case WPN_MACHINEGUN:
                return 0.8;
            case WPN_RAILGUN:
                return 0.9;
            case WPN_ROCKET_LAUNCHER:
                return 0.4;
            case WPN_SHOTGUN:
                return 0.8;
            case WPN_SUPER_SHOTGUN:
                return 0.7;
            default:
                return 0;
        }
    }

    public double getDamage() {
        switch (this) {
            case WPN_BFG10K:
                return 150;
            case WPN_BLASTER:
                return 15;
            case WPN_CHAINGUN:
                return 5;
            case WPN_GRENADES:
                return 100;
            case WPN_GRENADE_LAUNCHER:
                return 100;
            case WPN_HYPERBLASTER:
                return 15;
            case WPN_MACHINEGUN:
                return 5;
            case WPN_RAILGUN:
                return 120;
            case WPN_ROCKET_LAUNCHER:
                return 80;
            case WPN_SHOTGUN:
                return 40;
            case WPN_SUPER_SHOTGUN:
                return 80;
            default:
                return 0;
        }
    }
}
