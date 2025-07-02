package missionen;

import valueobjects.Spieler;
import domain.Spiel;

public class Spielervernichtung extends Laendereroberung {
    private final int opferId;
    private Boolean missionPossible = null;
    private Spieler opfer = null;

    public Spielervernichtung(int opferId, int zielAnzahl) {
        super(zielAnzahl);
        this.opferId = opferId;
    }
    @Override
    public boolean istErfuellt(Spiel spiel, Spieler spieler) {
        if (missionPossible == null){
            firstMission(spiel, spieler);
        }
        if (missionPossible){
            return !opfer.isAlive();
        } else {
            return super.istErfuellt(spiel, spieler);
        }
    }

    @Override
    public int getFortschritt(Spiel spiel, Spieler spieler){
        if (missionPossible == null){
            firstMission(spiel, spieler);
        }
        if (missionPossible){
            return 100 - (opfer.getBesetzteLaender().size() * 100 / 42);
        } else {
            return super.getFortschritt(spiel, spieler);
        }
    }

    private void firstMission(Spiel spiel, Spieler spieler){
        missionPossible = opferId != spieler.getId() && spiel.getSpielerListe().stream().anyMatch(spieler1 -> spieler1.getId() == opferId);
        if (missionPossible){
            opfer = spiel.getSpielerListe().stream().filter(spieler1 -> spieler1.getId() == opferId).findFirst().orElseThrow();
            beschreibung = "Vernichte alle Truppen von " + opfer.getName() + "!";
        }
    }
}
