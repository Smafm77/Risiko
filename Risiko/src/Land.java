import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

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

    //Wir brauchen wahrscheinlich erstmal unsere Map damit wir wissen welches Land welchen Nachbarn hat.
    public void setBesitzer(Spieler spieler) {
        this.besitzer = spieler;
    }
    //endregion

    //ToDo schreib eine Methode die Checkt ob es eine nutzbare verbindung mit einem anderen Land über bereits besetzte Länder gibt
    // --> separiert ob ziel eigenes, oder benachbartes fremdes Terretorium ist

}
