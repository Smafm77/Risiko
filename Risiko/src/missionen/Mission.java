package missionen;

import domain.Spiel;
import valueobjects.Spieler;

public abstract class Mission {
    public abstract boolean istErfuellt(Spiel spiel, Spieler spieler);

    /**
     * Gibt einen Wert zwischen 0 und 100 zurück, welcher zeigt wieweit die Mission erfüllt wurde
     * @return Integer zwischen 0 & 100
     */
    public abstract int getFortschritt(Spiel spiel, Spieler spieler);
    protected String beschreibung;

    public String getBeschreibung() {
        return beschreibung;
    }
    public Mission(String beschreibung) {
        this.beschreibung = beschreibung;
    }
}