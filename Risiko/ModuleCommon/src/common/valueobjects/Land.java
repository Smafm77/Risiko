package common.valueobjects;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Land implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String name;
    private Spieler besitzer;
    private int einheiten;
    private int farbe;
    private int id;
    private HashSet<Land> nachbarn = new HashSet<>();

    public Land(int strength, String name, int id) {
        this.name = name;
        this.einheiten = strength;
        this.id = id;
    }

    //region getters
    public int getId() {
        return id;
    }

    public int getFarbe() {
        return farbe;
    }

    public String getName() {
        return name;
    }

    public Spieler getBesitzer() {
        return besitzer;
    }

    public int getEinheiten() {
        return einheiten;
    }

    public HashSet<Land> getNachbarn() {
        return nachbarn;
    }

    public HashSet<Land> getFeindlicheNachbarn() {
        return (HashSet<Land>) nachbarn.stream().filter(l -> l.besitzer != this.besitzer).collect(Collectors.toSet());
    }
    //endregion

    //region setters

    public void setFarbe(int farbe){
        this.farbe = farbe;
    }

    public void setBesitzer(Spieler spieler) {
        this.besitzer = spieler;
    }

    public void wechselBesitzer(Spieler neuerBesitzer) {
        besitzer.verliereLand(this);
        besitzer = neuerBesitzer;
        neuerBesitzer.fuegeLandHinzu(this);
    }

    public void einheitenHinzufuegen(int einheiten) {
        this.einheiten += einheiten;
    }

    public void einheitenEntfernen(int einheiten) {
        this.einheiten -= einheiten;
        if (this.einheiten < 0) {
            this.einheiten = 0;
        }
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

    public boolean connectionPossible(Land ziel) {
        return connectionOptions().contains(ziel);
    }

    private HashSet<Land> connectionOptions() {
        HashSet<Land> neighborhood = new HashSet<>();
        directNeighbors(this, neighborhood);
        return neighborhood;
    }

    private void directNeighbors(Land currentStation, HashSet<Land> neighborhood) {
        for (Land land : currentStation.nachbarn) {
            if (!neighborhood.contains(land)) {
                if (land.besitzer == currentStation.besitzer) {
                    neighborhood.add(land);
                    directNeighbors(land, neighborhood);
                }
            }
        }
    }


    //endregion


    @Override
    public boolean equals(Object land) {
        return (land instanceof Land) && (((Land) land).name.equals(this.name));
    }
}
