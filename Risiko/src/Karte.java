public class Karte {
    public int strength;
    public Land land;

    public Karte(Land land) {
        this.land = land;
        this.strength = land.getStrength();
    }
}
