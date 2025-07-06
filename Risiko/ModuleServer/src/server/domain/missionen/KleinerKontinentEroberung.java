package server.domain.missionen;

import server.domain.Spiel;
import common.valueobjects.Kontinent;
import common.valueobjects.Spieler;

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

    @Override
    public int getFortschritt(Spiel spiel, Spieler spieler){
        int prog = 0;
        prog += kontinent1.getBesetzungsFortschritt(spieler, 33) + kontinent2.getBesetzungsFortschritt(spieler, 33);
        if(spiel.getWelt().alleKontinente.stream().anyMatch(kontinent -> (!kontinent.equals(kontinent1) && !kontinent.equals(kontinent2) && kontinent.getEinzigerBesitzer() != null && kontinent.getEinzigerBesitzer().equals(spieler)))){
            prog +=34;
        }
        return prog;
    }
}
