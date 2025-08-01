package common.valueobjects;

import common.exceptions.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Spieler implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String name;
    private String farbe;
    private final int id;
    private boolean alive;
    private boolean schonErobert;
    private final ArrayList<Land> besetzteLaender = new ArrayList<>();
    private final HashSet<Karte> karten = new HashSet<>();

    public Spieler(String name, int id) {
        this.name = name.trim();
        this.id = id;
        alive = true;
    }

    public Spieler(String name, String farbe) {
        this.name = name.trim();
        this.farbe = farbe;
        this.id = farbeZuId(farbe);
        alive = true;
    }

    //region getters
    public String getName() {
        return name;
    }

    public String getFarbe(){
        return farbe;
    }

    public int getId() {
        return id;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean getSchonErobert() {
        return schonErobert;
    }

    public void setSchonErobert(boolean schonErobert) {
        this.schonErobert = schonErobert;
    }

    public ArrayList<Land> getBesetzteLaender() {
        return besetzteLaender;
    }

    public HashSet<Karte> getKarten() {
        return karten;
    }
    //endregion



    //region Land Methoden

    public void fuegeLandHinzu(Land land) {
        besetzteLaender.add(land);
    }

    public void verliereLand(Land land) {
        besetzteLaender.remove(land);
    }
    //endregion

    //region Einheiten

    public int berechneNeueEinheiten(ArrayList<Kontinent> alleKontinente) {
        int neueEinheiten = 0;
        //Zuschuss besetzte Länder
        if (besetzteLaender.size() <= 9) {
            neueEinheiten += 3;
        } else {
            neueEinheiten += (besetzteLaender.size() / 3);
        }
        //Zuschuss Kontinente
        Kontinent[] reiche = alleKontinente.stream().filter(kontinent -> kontinent.getEinzigerBesitzer() == this).toArray(Kontinent[]::new);
        if (reiche.length > 0) {
            neueEinheiten += Arrays.stream(reiche).mapToInt(Kontinent::getBuff).sum();
        }
        return neueEinheiten;
    }

    public void bewegeEinheiten(int truppen, Land herkunft, Land ziel) throws FalscherBesitzerException, UngueltigeBewegungException, EinheitenAnzahlException {
        if(herkunft.getBesitzer() != this || ziel.getBesitzer() != this){
            throw new FalscherBesitzerException("Dieses Land gehört dir nicht!");
        }
        if (!herkunft.connectionPossible(ziel)){
            throw new UngueltigeBewegungException(ziel.getName() + " ist von " + herkunft.getName() + " aus nicht erreichbar.");
        }
        if (truppen < 1 || herkunft.getEinheiten() - truppen <1){
            throw new EinheitenAnzahlException("Es muss immer mindestens eine Einheit im Herkunftsland verbleiben!");
        }
        herkunft.einheitenEntfernen(truppen);
        ziel.einheitenHinzufuegen(truppen);
    }

    //endregion
    public void sterben(Spieler moerder) {
        moerder.getKarten().addAll(this.karten);
        karten.removeAll(karten);
        this.alive = false;
    }

    @Override
    public boolean equals(Object spieler) {
        return ((spieler instanceof Spieler) && ((Spieler) spieler).id == this.id) || ((spieler instanceof SpielerDTO) && ((SpielerDTO) spieler).getId() == this.id);
    }


    public SpielerDTO toDTO(){
        return new SpielerDTO(this);
    }

    private int farbeZuId(String color) {
        return switch (color) {
            case "Rot" -> 1;
            case "Blau" -> 2;
            case "Gruen" -> 3;
            case "Gelb" -> 4;
            case "Orange" -> 5;
            case "Violett" -> 6;
            default -> 0;
        };
    }
}