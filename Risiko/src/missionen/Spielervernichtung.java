package missionen;

import valueobjects.Spieler;
import domain.Spiel;

public class Spielervernichtung extends Laendereroberung {
    private final int opferId;

    public Spielervernichtung(int opferId, int zielAnzahl) {
        super(zielAnzahl);
        this.opferId = opferId;
    }
    @Override
    public boolean istErfuellt(Spiel spiel, Spieler spieler) {
        if (spiel.getSpielerListe().size() > opferId && opferId != spieler.getId()){
            Spieler opfer = spiel.getSpielerListe().get(opferId);
            beschreibung = "Vernichte alle Truppen von " + opfer.getName() + "!";
            return opfer.isAlive();
        } else {
            return super.istErfuellt(spiel, spieler);
        }
    }

    @Override
    public int getFortschritt(Spiel spiel, Spieler spieler){
        if (spiel.getSpielerListe().size() > opferId && opferId != spieler.getId()){
            Spieler opfer = spiel.getSpielerListe().get(opferId);
            return opfer.getBesetzteLaender().size() * 100 / 41;
        } else {
            return super.getFortschritt(spiel, spieler);
        }
    }
}
