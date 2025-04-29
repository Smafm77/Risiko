import java.util.ArrayList;
import java.util.List;

public class Spieler {
    public String name;
    public int id;
    public String farbe;
    public int einheiten;
    public boolean alive;
    public Spieler(String name, int id){
        this.name = name;
        this.id = id;
    }
    public List<Land> besetzteLaender = new ArrayList<>();

    public void fuegeLandHinzu(Land land) {
        besetzteLaender.add(land);
    }

    //List<Land> terretory; Liste aller besetzten Länder

    public void sterben() {
        this.alive = false;
    }

    // void recieveTroops(){ermittelt zum Zuganfang zustehende Truppen; assignTroops();}
    // void assignTroops(int troops){Füge 'troops' von Spieler ausgewählten Ländern zu}
    // void moveTroops(int troops, Land herkunft, Land ziel){Prüfe ob Zug möglich; Herkunft - troops; ziel + troops}
    // int getTroops (){int sum = 0; for (Land land : terretory){sum += land.troops} return sum;}
}
