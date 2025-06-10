package missionen;

import valueobjects.Kontinent;
import domain.Spiel;
import valueobjects.Spieler;

public class Kontinenteroberung extends Mission {
    protected final Kontinent kontinent1;
    protected final Kontinent kontinent2;

    public Kontinenteroberung(Kontinent kontinent1, Kontinent kontinent2) {
        super("Erobere " + kontinent1.getName() + " und " + kontinent2.getName() + "!");
        this.kontinent1 = kontinent1;
        this.kontinent2 = kontinent2;
    }
    @Override
    public boolean istErfuellt(Spiel spiel, Spieler spieler) {
        return kontinent1.getEinzigerBesitzer() == spieler && kontinent2.getEinzigerBesitzer() == spieler;
    }

    @Override
    public int getFortschritt(Spiel spiel, Spieler spieler){
       return kontinent1.getBesetzungsFortschritt(spieler, 50) + kontinent2.getBesetzungsFortschritt(spieler, 50);
    }
}
