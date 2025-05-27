package missionen;

import domain.Spiel;
import valueobjects.Kontinent;
import valueobjects.Spieler;

public class KleinerKontinentEroberung extends Kontinenteroberung{
    public KleinerKontinentEroberung(Kontinent kontinent1, Kontinent kontinent2) {
        super(kontinent1, kontinent2);
        beschreibung = "Erobere 3 Kontinente, darunter " + kontinent1.getName() + " und " + kontinent2.getName() + "!";
    }
    @Override
    public boolean istErfuellt(Spiel spiel, Spieler spieler){
        boolean owns3 = spiel.getWelt().alleKontinente.stream().filter(kontinent -> kontinent.getEinzigerBesitzer() == spieler).count() >= 3;
        return super.istErfuellt(spiel, spieler) && owns3;
    }
}
