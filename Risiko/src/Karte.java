public class Karte {
    private final int strength;
    private final Land land;

    public Karte(Land land) {
        this.land = land;
        this.strength = land.getStrength();
    }

    public int getStrength() {
        return strength;
    }

    public Land getLand() {
        return land;
    }
}
