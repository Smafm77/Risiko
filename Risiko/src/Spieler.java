import java.util.ArrayList;
import java.util.List;

public class Spieler {
    public String name;
    public int id;
    public String farbe;
    public int einheiten;
    public boolean alive;
    public List<Land> besetzteLaender = new ArrayList<>();

    public Spieler(String name, int id) {
        this.name = name;
        this.id = id;
        this.einheiten = 0;
    }

    public void fuegeLandHinzu(Land land) {
        besetzteLaender.add(land);
        this.einheiten++;
    }

    public void neueArmee(Spieler spieler) { //Bei neuem Spielzug dazu
        if (besetzteLaender.size() <= 9) {
            this.einheiten = +3;
        } else {
            this.einheiten = +besetzteLaender.size() / 3;
        }
    }

    public void sterben() {
            this.alive = false;
    }

    // void assignTroops(int troops){Füge 'troops' von Spieler ausgewählten Ländern zu}
    // void moveTroops(int troops, Land herkunft, Land ziel){Prüfe ob Zug möglich; Herkunft - troops; ziel + troops}
}
