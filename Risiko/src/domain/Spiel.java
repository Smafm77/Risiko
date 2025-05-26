package domain;

import exceptions.FalscherBesitzerException;
import exceptions.UngueltigeAuswahlException;
import exceptions.UngueltigeBewegungException;
import valueobjects.*;
import ui.cui.Menue;

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


    public Welt getWelt() {
        return welt;
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

    public boolean spielRunde(Menue menue) {

        for (Spieler spieler : spielerListe) {
            menue.setSpieler(spieler);
            if(!menue.getmLogik().weiterSpielen()){
                return false;
            }
            if (!spieler.isAlive()) {
                continue;
            }
            //Truppen erhalten
            int neueEinheiten = spieler.berechneNeueEinheiten(welt.alleKontinente);
            menue.getmEingabe().zuweisungEinheiten(neueEinheiten, spieler);
            spieler.setSchonErobert(false);
            boolean amZug = true;
            while (amZug) {
                amZug = menue.hauptMenue(spieler);
            }
        }
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