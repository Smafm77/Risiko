package missionen;

import domain.Spiel;

public abstract class Mission {
    public abstract boolean istErfuellt(Spiel spiel);
    private final String beschreibung;

    public String getBeschreibung() {
        return beschreibung;
    }
    public Mission(String beschreibung) {
        this.beschreibung = beschreibung;
    }
}