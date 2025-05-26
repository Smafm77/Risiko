package missionen;

import valueobjects.Kontinent;
import domain.Spiel;
import valueobjects.Spieler;

public class Kontinenteroberung extends Mission {
    private final Kontinent kontinent;

    public Kontinenteroberung(Kontinent kontinent) {
        super("Erobere " + kontinent.getName() + "!");
        this.kontinent = kontinent;
    }
    @Override
    public boolean istErfuellt(Spiel spiel) {
        Spieler spieler = spiel.getAktuellerSpieler();
        return kontinent.getEinzigerBesitzer() == spiel.getAktuellerSpieler();
    }
}
