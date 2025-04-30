import java.util.ArrayList;
import java.util.HashSet;

public class Spieler {
    public String name;
    public int id;
    public String farbe;
    public int einheiten;
    public boolean alive;
    public ArrayList<Land> besetzteLaender = new ArrayList<>();
    public HashSet<Karte> karten = new HashSet<>();

    public Spieler(String name, int id) {
        this.name = name.trim();
        this.id = id;
        this.einheiten = 0;
    }

    public void fuegeLandHinzu(Land land, int soldaten) {
        besetzteLaender.add(land);
        einheiten += soldaten; //ToDo checke ob es mit Kampf kompatibel ist soldaten mit land.strength zu ersetzen
    }

    public void verliereLand(Land land){
        einheiten -= land.einheiten; //Kann Probleme machen, falls Einheitenzahl schon mit Gegner ersetzt wurde
        besetzteLaender.remove(land);
    }

    public void neueArmee() { //Bei neuem Spielzug dazu
        int neueEinheiten = 3;

        //Zuschuss besetzte Länder
        if (besetzteLaender.size() >= 40){
            neueEinheiten += 10;
        } else if (besetzteLaender.size() >= 36) {
            neueEinheiten += 9;
        } else if (besetzteLaender.size() >=12) {
            neueEinheiten += (besetzteLaender.size() - 9) / 3;
        }
        /*if (besetzteLaender.size() <= 9) {
            this.einheiten += 3;
        } else {
            this.einheiten += besetzteLaender.size() / 3;
        }*/
        //ToDo addiere Kontinent Bonus
        //ToDo Hannah, du scheinst den Bonus anders gerechnet zu haben als ich, dass müssten wir mal vergleichen
    }

    //ToDo check every instance if einheiten being used if they need to be assigned to a post as well
    public void assignTroops(int troops){
        for (int t = 1; t <= troops; t++){
            //ToDO choose an owned country to put the unit in. > add 1 to einheiten here and in chosen land
        }
    }

    public void moveTroops(int troops, Land herkunft, Land ziel){
        //ToDO throw error if either Land is not in possession of the player, they're not connected or herkunft doesn't have enough troops
        herkunft.einheiten -= troops;
        ziel.einheiten += troops;
    }

    public void sterben() {
            this.alive = false;
    }

    public void countTroops(){
        int soldaten = 0;
        for (Land land : besetzteLaender){
            soldaten += land.einheiten;
        }
        //Todo throw Error if soldaten don't match with this.einheiten
        einheiten = soldaten;
    }

    @Override
    public boolean equals(Object spieler){
        return (spieler instanceof Spieler) && ((Spieler) spieler).id == this.id;
    }
}
