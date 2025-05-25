package valueobjects;

import exceptions.FalscherBesitzerException;
import exceptions.UngueltigeAuswahlException;
import ui.cui.Menue;
import ui.cui.MenueEingabe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class Spieler implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final int id;
    private boolean alive;
    private boolean schonErobert;
    private ArrayList<Land> besetzteLaender = new ArrayList<>();
    private HashSet<Karte> karten = new HashSet<>();

    public Spieler(String name, int id) {
        this.name = name.trim();
        this.id = id;
        alive = true;
    }

    //region getters
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    } //Wird das irgendwo außerhalb von Spieler genutzt? Ja einmal Spiel-zeige eigene Gebiete und das wo es hier genutzt wird würde ich ins UI verschieben

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

    public HashSet<Land> getFeinde() {  //todo wird nie genutzt! Nochmal in gruen //Kann gelöscht werden. Momentan brauchen wir es nicht und notfalls können wir es neu schreiben
        HashSet<Land> feinde = new HashSet<>();
        for (Land kolonie : besetzteLaender) {
            feinde.addAll(kolonie.getFeindlicheNachbarn());
        }
        return feinde;
    }

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


    public void bewegeEinheiten(int truppen, Land herkunft, Land ziel) {
        herkunft.einheitenEntfernen(truppen);
        ziel.einheitenHinzufuegen(truppen);
    }

    //endregion
    public void sterben(Spieler moerder) {  //In Domain, da Aufruf eines anderen Spielers. Replace/Reduce hier mit alive=false
        moerder.getKarten().addAll(this.karten);
        karten.removeAll(karten);
        this.alive = false;
    }


    @Override
    public boolean equals(Object spieler) {
        return (spieler instanceof Spieler) && ((Spieler) spieler).id == this.id;
    }

    public void zeigeSpieler() { //UI
        System.out.println(id + " - " + name + " - " + besetzteLaender.size());
        for (Land land : besetzteLaender) {
            System.out.println(land.getBesitzer().getId() + ": " + land.getName() + " (" + land.getEinheiten() + ")");
        }
        System.out.println();
    }

    public String eigeneKartenToString() { //Print? UI? - sollte UI sein, muss nochmal gucken wofür das überhaupt aufgerufen wurde. Glaube Peruse Cards
        String kartenTxt = "";
        for (Karte karte : karten) {
            kartenTxt += "[" + karte.getStrength() + " - " + karte.getLand().getName() + "]  ";
        }
        return kartenTxt;
    }
}