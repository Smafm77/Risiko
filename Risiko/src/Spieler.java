import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Spieler {
    //region Basics
    private String name;
    private int id;
    private String farbe;
    private int einheiten;    //ToDo check every instance if einheiten being used, if it needs to impact a Land as well
    private boolean alive;
    private ArrayList<Land> besetzteLaender = new ArrayList<>();
    private HashSet<Karte> karten = new HashSet<>();

    public Spieler(String name, int id) {
        this.name = name.trim();
        this.id = id;
        this.einheiten = 0;
    }
    //endregion
    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }
    public String getFarbe() {
        return farbe;
    }
    public int getEinheiten() {
        return einheiten;
    }
    public boolean isAlive() {
        return alive;
    }
    public ArrayList<Land> getBesetzteLaender() {
        return besetzteLaender;
    }
    public HashSet<Karte> getKarten() {
        return karten;
    }

    //region Land Methoden

    //Todo Test this
    public HashSet<Land> getFeinde() {
        HashSet<Land> feinde = new HashSet<>();
        for (Land kolonie : besetzteLaender){
            feinde.addAll(kolonie.getFeindlicheNachbarn());
        }
        return feinde;
    }

    public void fuegeLandHinzu(Land land, int soldaten) {
        besetzteLaender.add(land);
        einheiten += soldaten; //ToDo checke ob es mit Kampf kompatibel ist soldaten mit land.strength zu ersetzen
    }

    public void verliereLand(Land land){
        einheiten -= land.getEinheiten(); //Kann Probleme machen, falls Einheitenzahl schon mit Gegner ersetzt wurde
        besetzteLaender.remove(land);
    }
    //endregion

    //region Einheiten
    public void neueArmee(ArrayList<Kontinent> alleKontinente) { //Bei neuem Spielzug dazu
        int neueEinheiten = 3;
        //Zuschuss besetzte Länder
        if (besetzteLaender.size() <= 9) {
            this.einheiten += 3;
        } else {
            this.einheiten += besetzteLaender.size() / 3;
        }
        //Zuschuss Kontinente
        Kontinent[] reiche = (Kontinent[]) alleKontinente.stream().filter(kontinent -> kontinent.getEinzigerBesitzer() == this).toArray();
        if (reiche.length > 0){
            neueEinheiten += Arrays.stream(reiche).mapToInt(Kontinent::getBuff).sum();
        }

        zuweisungEinheiten(neueEinheiten);
    }

    /**
     * Eine Methode die neue Truppen ihre Länder zuweist
     * @param truppen Anzahl Truppen die stationiert werden
     */
    public void zuweisungEinheiten(int truppen){
        for (int t = 1; t <= truppen; t++){
            //ToDO choose an owned country to put the unit in. > add 1 to einheiten here and in chosen land
        }
    }

    public void bewegeEinheiten(int truppen, Land herkunft, Land ziel){ //ToDo Wieso gibt es moveTroops sowohl in Spieler als auch in Spiel?
        //ToDO throw error
        if(herkunft.getBesitzer() != this) {
            //throw error (Diese Truppen gehören dir nicht!)
        }
        if(herkunft.getEinheiten() <= truppen) {
            //throw error (Es befinden sich zu wenig Einheiten auf diesem Feld!)
        }
        if(!herkunft.connectionPossible(ziel)) {
            //throw error (Die Länder sind nicht verbunden!)
        }
        int herkunftEinheiten = herkunft.getEinheiten();
        herkunft.setEinheiten(herkunftEinheiten - truppen);
        int zielEinheiten = ziel.getEinheiten();
        ziel.setEinheiten(zielEinheiten + truppen);
    }
    //endregion

    public void sterben() {
            this.alive = false;
    }

    //ToDO implement everytime Einheiten get changed
    public void zaehleEinheiten(){
        int soldaten = 0;
        for (Land land : besetzteLaender){

            soldaten += land.getEinheiten();
        }
        //Todo throw Error if soldaten don't match with this.einheiten
        einheiten = soldaten;
    }

    @Override
    public boolean equals(Object spieler){
        return (spieler instanceof Spieler) && ((Spieler) spieler).id == this.id;
    }
}
