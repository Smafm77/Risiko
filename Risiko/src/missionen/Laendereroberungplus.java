package missionen;

import domain.Spiel;
import valueobjects.Spieler;

public class Laendereroberungplus extends Laendereroberung {
    private final int zielAnzahl;

    public Laendereroberungplus(int zielAnzahl) {
        super(zielAnzahl);
        beschreibung = "Erobere " + zielAnzahl + " Länder und setzen Sie in jedes Land mindestens 2 Armeen!";
        this.zielAnzahl = zielAnzahl;
    }
    @Override
    public boolean istErfuellt(Spiel spiel, Spieler spieler) {
        long counter = spieler.getBesetzteLaender().stream().filter(land -> land.getEinheiten() >= 2).count();
        return counter>=zielAnzahl;
    }

    @Override
    public int getFortschritt(Spiel spiel, Spieler spieler){
        int progress = Math.toIntExact(spieler.getBesetzteLaender().stream().filter(land -> land.getEinheiten() >= 2).count()) * 100 / zielAnzahl;
        return Math.min(progress, 100);
    }
}
