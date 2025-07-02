package common.missionen;

import server.domain.Spiel;
import common.valueobjects.Spieler;

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

    @Override
    public int getFortschritt(Spiel spiel, Spieler spieler){
        int progress = (spieler.getBesetzteLaender().size() * 100) / zielAnzahl;
        return Math.min(progress, 100);
    }
}
