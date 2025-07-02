package server.domain;

import common.exceptions.FalscherBesitzerException;
import common.exceptions.UngueltigeAuswahlException;
import common.exceptions.UngueltigeBewegungException;
import common.valueobjects.Karte;
import common.valueobjects.Spieler;
import common.valueobjects.Welt;
import server.persistence.NeuesSpielEinlesen;
import server.persistence.SpielSpeichern;
import common.enums.Spielphase;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.util.*;

public class Spiel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    Welt welt = new Welt();
    ArrayList<Spieler> spielerListe = welt.getSpielerListe();
    HashSet<Karte> kartenStapel = new HashSet<>();
    //private final transient Menue menue = new Menue(); //am besten Menue aus Spiel raus nehmen aber grade bin ich zu müde
    private Spieler aktuellerSpieler = null;
    private Spielphase phase = Spielphase.VERTEILEN;
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

    public HashSet<Karte> getKartenStapel() {
        return kartenStapel;
    }

    public ArrayList<Spieler> getSpielerListe() {
        return spielerListe;
    }

    public void init() {
        if (spielerListe.isEmpty()) {
            throw new IllegalStateException("Keine Spieler angelegt");
        }
        aktuellerSpieler = spielerListe.getFirst();
        phase = Spielphase.VERTEILEN;
    }

    public void starteSpiel(Menue menue) throws IOException, UngueltigeAuswahlException, FalscherBesitzerException, UngueltigeBewegungException {
        menue.buildWelt();
        boolean nochEinmal;
        do {
            nochEinmal = spielRunde(menue);
        } while (nochEinmal);
    }

    public void continueSpiel(Menue menue) throws IOException, UngueltigeAuswahlException, FalscherBesitzerException, UngueltigeBewegungException {
        boolean nochEinmal;
        do {
            nochEinmal = spielRunde(menue);
        } while (nochEinmal);
    }

    public void naechsterSpieler() {
        do{
            int spielerIndex = spielerListe.indexOf(aktuellerSpieler);
            aktuellerSpieler = spielerListe.get((spielerIndex + 1) % spielerListe.size());
        } while (!aktuellerSpieler.isAlive());
        AktiverSpielerListener.fire(aktuellerSpieler);
    }

    public void naechstePhase() {
        try {
            SpielSpeichern.speichern(this, "spielstand.risiko");
            System.out.println("Spiel erfolgreich gespeichert!");
        } catch (IOException e) {
            System.out.println("Speichern fehlgeschlagen: " + e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        switch (phase) {
            case VERTEILEN -> phase = Spielphase.ANGRIFF;
            case ANGRIFF -> phase = Spielphase.VERSCHIEBEN;
            case VERSCHIEBEN -> {
                naechsterSpieler();
                phase = Spielphase.VERTEILEN;
            }
        }
    }

    public boolean spielRunde(Menue menue) {
        menue.setSpieler(aktuellerSpieler);
        if (!menue.getmLogik().weiterSpielen()) {
            return false;
        }
        phase = Spielphase.VERTEILEN;
        //Truppen erhalten
        int neueEinheiten = aktuellerSpieler.berechneNeueEinheiten(welt.alleKontinente);
        menue.getmEingabe().zuweisungEinheiten(neueEinheiten, aktuellerSpieler);
        aktuellerSpieler.setSchonErobert(false);
        naechstePhase();
        boolean weiterAngreifen = true;
        while (weiterAngreifen) {
            weiterAngreifen = menue.hauptMenue(aktuellerSpieler);
        }
        naechstePhase();
        boolean weiterVerschieben = true;
        while (weiterVerschieben) {
            weiterVerschieben = menue.hauptMenue(aktuellerSpieler);
        }

        //ToDo Kontrolliere ob dies die richtige Stelle im Ablauf der Runde ist
        // Methode für Spiel zu Ende schreiben
        if (aktuellerSpieler.hatMissionErfuellt(this)) {
            System.out.println("Herzlichen Glückwunsch! Mission erfüllt: " + aktuellerSpieler.getMissionBeschreibung());
        }

        naechstePhase();

        return true;
    }

    //region playing Cards
    public void zieheKarte(Spieler spieler) {
        try {
            Optional<Karte> optionalCard = kartenStapel.stream().findFirst();
            Karte karte = optionalCard.orElseThrow();
            spieler.getKarten().add(karte);
        } catch (NoSuchElementException e) {
            System.out.println("Fehler: ");
        }
    }

    public void spieleKarte(Spieler spieler, Karte karte) {
        if (spieler.getKarten().contains(karte)) {
            spieler.getKarten().remove(karte);
            menue.getmEingabe().zuweisungEinheiten(karte.getStrength(), spieler);
            kartenStapel.add(karte);
        }
    }
    //endregion

    //region kampf
    public void kampfMoeglich(Land herkunft, Land ziel, int truppenA, int truppenV) throws FalscherBesitzerException, UngueltigeBewegungException {
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

    public boolean kampf(Land herkunft, Land ziel, int truppenA, int truppenV) throws FalscherBesitzerException, UngueltigeBewegungException {
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

    public void erobern(Land herkunft, Land ziel, int besatzer) throws FalscherBesitzerException, UngueltigeBewegungException {
        Spieler verteidiger = ziel.getBesitzer();
        ziel.wechselBesitzer(herkunft.getBesitzer());
        herkunft.getBesitzer().bewegeEinheiten(besatzer, herkunft, ziel);
        welt.findeKontinentenzugehoerigkeit(ziel).getEinzigerBesitzer();

        if (verteidiger.getBesetzteLaender().isEmpty()) {
            verteidiger.sterben(herkunft.getBesitzer());
        }
    }


    //endregion
    public int rolleWuerfel() {
        return (int) (Math.random() * 6) + 1;
    }


    public void addSpieler(Spieler spieler) {
        this.spielerListe.add(spieler);
    }

    public void setPhase(Spielphase spielphase) {
        this.phase = spielphase;
    }
}