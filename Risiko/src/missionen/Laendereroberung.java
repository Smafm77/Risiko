package missionen;

import domain.Spiel;
import valueobjects.Spieler;

public class Laendereroberung extends Mission {
    private final int zielAnzahl;

    public Laendereroberung (int zielAnzahl) {
        super("Erobere " + zielAnzahl + " Länder!");
        this.zielAnzahl = zielAnzahl;
    }
    @Override
    public boolean istErfuellt(Spiel spiel, Spieler spieler) {
        return spieler.getBesetzteLaender().size() >= zielAnzahl;
    }
}
