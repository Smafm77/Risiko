package server.domain;

import common.exceptions.*;
import common.valueobjects.*;
import server.domain.missionen.Mission;
import server.persistence.*;
import common.enums.Spielphase;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.util.*;

public class Spiel implements Serializable, ISpiel {
    @Serial
    private static final long serialVersionUID = 1L;
    Welt welt = buildWelt();
    ArrayList<Spieler> spielerListe = welt.getSpielerListe();
    HashSet<Karte> kartenStapel;
    public final Map<Spieler, Mission> missionen = new HashMap<>();
    private Spieler aktuellerSpieler = null;
    private Spielphase phase;
    private static Spiel instance;

    public static synchronized Spiel getInstance() {
        if (instance == null) {
            try {
                instance = new Spiel();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return instance;
    }

    public Spieler getAktuellerSpieler() {
        return aktuellerSpieler;
    }

    public Welt getWelt() {
        return welt;
    }

    public Spielphase getPhase() {
        return phase;
    }

    private Spiel() throws IOException {
        System.out.println("Starte Spiel...");
        NeuesSpielEinlesen einlesen = new NeuesSpielEinlesen();
        kartenStapel = einlesen.kartenstapelEinlesen(welt.getAlleLaender());
    }

    public ArrayList<Spieler> getSpielerListe() {
        return spielerListe;
    }

    public void setSpielerliste (ArrayList<Spieler> spielerListe){
        welt.setSpielerListe(spielerListe);
    }

    public void init() {
        if (spielerListe.isEmpty()) {
            throw new IllegalStateException("Keine Spieler angelegt");
        }
        aktuellerSpieler = spielerListe.getFirst();
        phase = Spielphase.VERTEILEN;
    }

    private void naechsterSpieler() {
        do {
            int spielerIndex = spielerListe.indexOf(aktuellerSpieler);
            aktuellerSpieler = spielerListe.get((spielerIndex + 1) % spielerListe.size());
        } while (!aktuellerSpieler.isAlive());
        AktiverSpielerListener.fire(aktuellerSpieler);
    }

    public void naechstePhase() {
        spielSpeichern();
        switch (phase) {
            case VERTEILEN -> {
                aktuellerSpieler.setSchonErobert(false);
                phase = Spielphase.ANGRIFF;
            }
            case ANGRIFF -> phase = Spielphase.VERSCHIEBEN;
            case VERSCHIEBEN -> {
                naechsterSpieler();
                phase = Spielphase.VERTEILEN;
            }
        }
    }
    public void spielSpeichern (){
        try {
            SpielSpeichern.speichern(this, "spielstand.risiko");
            System.out.println("Spiel erfolgreich gespeichert!");
        } catch (IOException e) {
            System.out.println("Speichern fehlgeschlagen: " + e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }


    //region playing Cards
    private void zieheKarte(Spieler spieler) {
        try {
            Optional<Karte> optionalCard = kartenStapel.stream().findFirst();
            Karte karte = optionalCard.orElseThrow();
            kartenStapel.remove(karte);
            spieler.getKarten().add(karte);
        } catch (NoSuchElementException e) {
            System.out.println("Fehler: ");
        }
    }

    public int spieleKarte(int spielerId, Karte karte) {
        Spieler spieler = findSpielerById(spielerId);
        if (spieler.getKarten().contains(karte)) {
            spieler.getKarten().remove(karte);
            kartenStapel.add(karte);
            return karte.getStrength();
        }
        return 0;
    }
    //endregion

    //region kampf
    private void kampfMoeglich(Land herkunft, Land ziel, int truppenA, int truppenV) throws FalscherBesitzerException, UngueltigeBewegungException {
        Spieler angreifer = herkunft.getBesitzer();
        if (angreifer != aktuellerSpieler) {
            throw new FalscherBesitzerException("Du bist nicht der Besitzer von " + herkunft.getName());
        }
        if (ziel.getBesitzer() == angreifer) {
            throw new FalscherBesitzerException(ziel.getName() + " gehört dir bereits.");
        }
        if (!herkunft.getFeindlicheNachbarn().contains(ziel)) {
            throw new UngueltigeBewegungException("Kein direkter Angriffspfad zwischen den Ländern!");
        }
        if (truppenA < 1 || truppenA > 3 || truppenA >= herkunft.getEinheiten()) {
            throw new EinheitenAnzahlException("Angriffstruppenzahl ungültig!");
        }
        if (truppenV < 1 || truppenV > 2 || truppenV > ziel.getEinheiten()) {
            throw new EinheitenAnzahlException("Verteidigungstruppenzahl ungültig!");
        }
    }

    public boolean kampf(int herkunftId, int zielId, int truppenA, int truppenV) throws FalscherBesitzerException, UngueltigeBewegungException {
        Land herkunft = welt.findeLand(herkunftId);
        Land ziel = welt.findeLand(zielId);
        kampfMoeglich(herkunft, ziel, truppenA, truppenV);
        int ueberlebende = schlacht(herkunft, ziel, truppenA, truppenV);
        if (ueberlebende == -1) {
            return false;
        } //Verteidiger hat gewonnen
        erobern(herkunft, ziel, truppenA);
        return true;
    }

    private int schlacht(Land herkunft, Land ziel, int truppenA, int truppenV) {
        Integer[] angriff = new Integer[truppenA];
        Integer[] verteidigung = new Integer[truppenV];
        for (int i = 0; i < angriff.length; i++) {
            angriff[i] = rolleWuerfel();
        }
        for (int i = 0; i < verteidigung.length; i++) {
            verteidigung[i] = rolleWuerfel();
        }
        Arrays.sort(angriff, Collections.reverseOrder());
        Arrays.sort(verteidigung, Collections.reverseOrder());

        for (int i = 0; i < Math.min(truppenA, truppenV); i++) {
            if (angriff[i] > verteidigung[i]) {
                ziel.einheitenEntfernen(1);
            } else {
                herkunft.einheitenEntfernen(1);
            }
        }

        return ziel.getEinheiten() > 0 ? -1 : truppenA;
    }

    private void erobern(Land herkunft, Land ziel, int besatzer) throws FalscherBesitzerException, UngueltigeBewegungException {
        Spieler angreifer = herkunft.getBesitzer();
        Spieler verteidiger = ziel.getBesitzer();
        ziel.wechselBesitzer(angreifer);
        angreifer.bewegeEinheiten(besatzer, herkunft, ziel);
        welt.findeKontinentenzugehoerigkeit(ziel).getEinzigerBesitzer();

        if (!angreifer.getSchonErobert()){
            zieheKarte(angreifer);
            angreifer.setSchonErobert(true);
        }

        if (verteidiger.getBesetzteLaender().isEmpty()) {
            verteidiger.sterben(herkunft.getBesitzer());
        }
    }

    private int rolleWuerfel() {
        return (int) (Math.random() * 6) + 1;
    }
    //endregion

    public void setPhase(Spielphase spielphase) {
        this.phase = spielphase;
    }

    private Welt buildWelt() throws IOException {
        NeuesSpielEinlesen spielmaterial = new NeuesSpielEinlesen();
        ArrayList<Land> laender = spielmaterial.alleLaenderEinlesen();
        ArrayList<Kontinent> kontinente = spielmaterial.alleKontinenteEinlesen(laender);
        return new Welt(laender, kontinente);
    }

    //region missionen
    public void weiseMissionenZu() {
        NeuesSpielEinlesen spielmaterial = new NeuesSpielEinlesen();
        HashSet<Mission> alleMissionen = spielmaterial.missionenErstellen(welt.alleKontinente);
        for (Spieler spieler : welt.getSpielerListe()) {
            Mission mission = alleMissionen.stream().findFirst().orElseThrow();
            alleMissionen.remove(mission);
            missionen.put(spieler, mission);
        }
    }

    public String getMissionBeschreibung(int spielerId) {
        Spieler spieler = findSpielerById(spielerId);
        return missionen.get(spieler).getBeschreibung();
    }

    public boolean hatMissionErfuellt(int spielerId) {
        Spieler spieler = findSpielerById(spielerId);
        return missionen.get(spieler).istErfuellt(this, spieler);
    }

    public int getMissionProgress(int spielerId) {
        Spieler spieler = findSpielerById(spielerId);
        return missionen.get(spieler).getFortschritt(this, spieler);
    }
    //endregion

    private Spieler findSpielerById(int spielerId){
        return spielerListe.stream().filter(spieler -> spieler.getId() == spielerId).findAny().orElseThrow();
    }

    //region CommonMethoden
    public int berechneSpielerEinheiten(int spielerId){
        Spieler spieler = findSpielerById(spielerId);
        return spieler.berechneNeueEinheiten(welt.alleKontinente);
    }
    public HashSet<Karte> getSpielerKarten(int spielerId){
        Spieler spieler = findSpielerById(spielerId);
        return spieler.getKarten();
    }
    public void einheitenStationieren(int landId, int einheiten){
        welt.findeLand(landId).einheitenHinzufuegen(einheiten);
    }
    public void bewegeEinheiten(int spielerId, int truppen, int herkunftId, int zielId) throws FalscherBesitzerException, UngueltigeBewegungException {
        Spieler spieler = findSpielerById(spielerId);
        Land herkunft = welt.findeLand(herkunftId);
        Land ziel = welt.findeLand(zielId);
        spieler.bewegeEinheiten(truppen, herkunft, ziel);
    }
    public Spieler getLandbesitzer (int landId){
        return welt.findeLand(landId).getBesitzer();
    }
    public int getLandTruppen (int landId){
        return welt.findeLand(landId).getEinheiten();
    }
    //endregion
}