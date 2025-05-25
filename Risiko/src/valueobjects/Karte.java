package valueobjects;

import java.io.Serializable;

public class Karte implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int strength;
    private final Land land;

    public Karte(Land land, int strength) {
        this.land = land;
        this.strength = strength;
    }

    public int getStrength() {
        return strength;
    }

    public Land getLand() {
        return land;
    }
}
