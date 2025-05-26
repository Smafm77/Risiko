package missionen;

import valueobjects.Spieler;
import domain.Spiel;

public class Spielervernichtung extends Mission {
    private final Spieler opfer;

    public Spielervernichtung (Spieler opfer) {
        super("Eliminiere " + opfer.getName() + "!");
        this.opfer = opfer;
    }
    @Override
    public boolean istErfuellt(Spiel spiel) {
        return !opfer.isAlive();
    }
}
