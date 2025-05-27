package missionen;

import domain.Spiel;
import valueobjects.Spieler;

public abstract class Mission {
    public abstract boolean istErfuellt(Spiel spiel, Spieler spieler);
    protected String beschreibung;

    public String getBeschreibung() {
        return beschreibung;
    }
    public Mission(String beschreibung) {
        this.beschreibung = beschreibung;
    }
}