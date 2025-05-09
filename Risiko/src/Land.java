import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Land {
    private String name;
    private Spieler besitzer;
    private int einheiten;
    private final int strength; //ToDo check mal bitte kopftechnisch ob wir wirklich eine eigene Variable dafür brauchen oder aber ob das nicht auch anders geht, irgwndwie finde ich die hässlich
    private HashSet<Land> nachbarn = new HashSet<>();

    public Land(int strength, String name) {
        this.strength = strength; //ToDo kann man das nicht hier ersetzen mit this.einheiten = 2; Bzw wieso überhaupt 2? Ich finde in den Regeln nichts dazu
        this.name = name;
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

    public boolean isName(String name) {
        return this.name.equals(name);
    }
}
