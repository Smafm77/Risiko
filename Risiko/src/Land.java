import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Land {
    public String name;
    public Spieler besitzer;
    public int einheiten;
    public int strength;
    public HashSet<Land> nachbarn = new HashSet<>();

    public Land(int strength, String name, Land... nachbarn) {
        this.strength = strength;
        this.name = name;
        this.nachbarn.addAll(Arrays.asList(nachbarn));
    }

    //ToDo Teste ob connectionOptions & directNeighbors funktioniert, sobald Karte feststeht (sorge um Beibehalten von Veränderungen der Sets)
    public boolean connectionPossible(Land ziel){
        return connectionOptions().contains(ziel);
    }
    public HashSet<Land> connectionOptions(){
        HashSet<Land> neighborhood = new HashSet<>();
        HashSet<Land> barbarians = new HashSet<>();
        directNeighbors(this, neighborhood, barbarians);
        return neighborhood;
    }
    private void directNeighbors(Land currentStation, HashSet<Land> neighborhood, HashSet<Land> barbarians){
        for (Land land : currentStation.nachbarn){
            if (!neighborhood.contains(land) && !barbarians.contains(land)){
                if (land.besitzer == currentStation.besitzer){
                    neighborhood.add(land);
                    directNeighbors(land, neighborhood, barbarians);
                } else {
                    barbarians.add(land);
                }
            }
        }
    }

    //region Getters and Setters
    public void setNachbarn(Collection<Land> nachbarn) {
        this.nachbarn = null;
        addNachbarn(nachbarn);
    }
    public void addNachbarn(Land[] nachbarn){
        addNachbarn(Arrays.asList(nachbarn));
    }
    public void addNachbarn(Collection<Land> nachbarn) {
        for (Land nachbar : nachbarn){
            nachbar.addNachbar(this);
        }
        this.nachbarn.addAll(nachbarn);
    }
    public void addNachbar(Land nachbar){
        nachbarn.add(nachbar);
    }
    public HashSet<Land> getFeindlicheNachbarn(){//Todo Test this
        return (HashSet<Land>) nachbarn.stream().filter(l -> l.besitzer != this.besitzer).collect(Collectors.toSet());
    }

    //Wir brauchen wahrscheinlich erstmal unsere Map damit wir wissen welches Land welchen Nachbarn hat.
    public void setBesitzer(Spieler spieler) {
        this.besitzer = spieler;
    }
    //endregion

    //ToDo schreib eine Methode die Checkt ob es eine nutzbare verbindung mit einem anderen Land über bereits besetzte Länder gibt
    // --> separiert ob ziel eigenes, oder benachbartes fremdes Terretorium ist

}
