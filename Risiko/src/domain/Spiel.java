package domain;

import exceptions.FalscherBesitzerException;
import exceptions.UngueltigeAuswahlException;
import exceptions.UngueltigeBewegungException;
import valueobjects.*;
import ui.cui.Menue;
import enums.Spielphase;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class Spiel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    ArrayList<Spieler> spielerListe = new ArrayList<>();
    Welt welt = new Welt(spielerListe);
    HashSet<Karte> kartenStapel = new HashSet<>();
    private final transient Menue menue = new Menue(); //am besten Menue aus Spiel raus nehmen aber grade bin ich zu müde
    private Spieler aktuellerSpieler;
    private Spielphase phase;

    public Welt getWelt() {
        return welt;
    }

    public Spielphase getPhase(){
        return phase;
    }

    public Spiel() throws IOException {
        System.out.println("Starte Spiel...");
        menue.buildWelt(welt);
    }

    public HashSet<Karte> getKartenStapel() {
        return kartenStapel;
    }

    public ArrayList<Spieler> getSpielerListe() {
        return spielerListe;
    }

    public void starteSpiel(Menue menue) throws IOException, UngueltigeAuswahlException, FalscherBesitzerException, UngueltigeBewegungException {
        boolean nochEinmal;
        do {
            nochEinmal = spielRunde(menue);
        } while (nochEinmal);
    }

    public void naechsterSpieler() {
        int aktuelleID = aktuellerSpieler.getId();
        if (aktuelleID < spielerListe.size()) {
            aktuellerSpieler = spielerListe.get(aktuellerSpieler.getId());

        } else {
            aktuellerSpieler = spielerListe.getFirst();

        }
    }

    private void naechstePhase(){
        switch (phase){
            case VERTEILEN -> phase = Spielphase.ANGRIFF;
            case ANGRIFF -> phase = Spielphase.VERSCHIEBEN;
            case VERSCHIEBEN -> phase = Spielphase.VERTEILEN;
        }
    }

    public boolean spielRunde(Menue menue) {
        menue.setSpieler(aktuellerSpieler);
        if (aktuellerSpieler == null) {
            aktuellerSpieler = spielerListe.getFirst();
        }
        if (!menue.getmLogik().weiterSpielen()) {
            return false;
        }
        if (!aktuellerSpieler.isAlive()) {
            naechsterSpieler();
            return true;
        }
        phase = Spielphase.VERTEILEN;
        //Truppen erhalten
        int neueEinheiten = aktuellerSpieler.berechneNeueEinheiten(welt.alleKontinente);
        menue.getmEingabe().zuweisungEinheiten(neueEinheiten, aktuellerSpieler);
        aktuellerSpieler.setSchonErobert(false);
        naechstePhase();
        boolean weiterAngreifen = true;
        while(weiterAngreifen){
            weiterAngreifen = menue.hauptMenue(aktuellerSpieler);
        }
        naechstePhase();
        boolean weiterVerschieben = true;
        while(weiterVerschieben){
            weiterVerschieben = menue.hauptMenue(aktuellerSpieler);
        }

        naechstePhase();
        naechsterSpieler();
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
    public boolean kampf(Land herkunft, Land ziel, int truppenA, int truppenV) {
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

    public void erobern(Land herkunft, Land ziel, int besatzer) {
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


}