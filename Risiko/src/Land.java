import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Land {
    private final String name;
    private Spieler besitzer;
    private int einheiten;
    private final int strength;
    private HashSet<Land> nachbarn = new HashSet<>();

    public Land(int strength, String name) {
        this.strength = strength;
        this.name = name;
        this.einheiten = strength;
    }

    //ToDo Teste ob connectionOptions & directNeighbors funktioniert, sobald Karte feststeht (sorge um Beibehalten von Veränderungen der Sets)
    public boolean connectionPossible(Land ziel) {
        return connectionOptions().contains(ziel);
    }

    public HashSet<Land> connectionOptions() {
        HashSet<Land> neighborhood = new HashSet<>();
        HashSet<Land> barbarians = new HashSet<>();
        directNeighbors(this, neighborhood, barbarians);
        return neighborhood;
    }

    private void directNeighbors(Land currentStation, HashSet<Land> neighborhood, HashSet<Land> barbarians) {
        for (Land land : currentStation.nachbarn) {
            if (!neighborhood.contains(land) && !barbarians.contains(land)) {
                if (land.besitzer == currentStation.besitzer) {
                    neighborhood.add(land);
                    directNeighbors(land, neighborhood, barbarians);
                } else {
                    barbarians.add(land);
                }
            }
        }
    }

    public void einheitGestorben(){ //Nur dann nutzen, wenn garantiert ist das einheiten > 0
        einheiten--;
    }
    public void einheitRekrutiert(){
        einheiten++;
    }
    public void wechselBesitzer(Spieler neuerBesitzer){
        besitzer.verliereLand(this);
        besitzer = neuerBesitzer;
        neuerBesitzer.fuegeLandHinzu(this);
    }

    //region Getters and Setters

    public String getName() {
        return name;
    }

    public Spieler getBesitzer() {
        return besitzer;
    }

    public int getEinheiten() {
        return einheiten;
    }

    public int getStrength() {
        return strength;
    }

    public HashSet<Land> getNachbarn() {
        return nachbarn;
    }

    public void setBesitzer(Spieler spieler) {
        this.besitzer = spieler;
    }

    public void setEinheiten(int einheiten) {
        this.einheiten = einheiten; //ToDo Das überschreibt ja jetzt nur die Einheitenzahl. Theoretisch wenn wir verschieben, addieren/subtrahieren wir aber Einheiten von der bestehenden Anzahl. Wie machen wir das?
    }

    public void setNachbarn(Collection<Land> nachbarn) {
        this.nachbarn = null;
        addNachbarn(nachbarn);
    }

    public void addNachbarn(Land[] nachbarn) {
        addNachbarn(Arrays.asList(nachbarn));
    }

    public void addNachbarn(Collection<Land> nachbarn) {
        for (Land nachbar : nachbarn) {
            nachbar.addNachbar(this);
        }
        this.nachbarn.addAll(nachbarn);
    }

    public void addNachbar(Land nachbar) {
        nachbarn.add(nachbar);
    }

    public HashSet<Land> getFeindlicheNachbarn() {//Todo Test this
        return (HashSet<Land>) nachbarn.stream().filter(l -> l.besitzer != this.besitzer).collect(Collectors.toSet());
    }
    //endregion


    @Override
    public boolean equals(Object land) {
        return (land instanceof Land) && (((Land) land).name.equals(this.name));
    }

    public boolean isName(String name) { //todo nie benutzt nochmal in gruen
        return this.name.equals(name);
    }
}
