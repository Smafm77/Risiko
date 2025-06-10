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
        int counter = 0;
        for(int i = 0; spieler.getBesetzteLaender().size()<2; i++) {
            if(spieler.getBesetzteLaender().get(i).getEinheiten()>=2) {
                counter++;
            }
        }
        return counter>=zielAnzahl;
    }
}
