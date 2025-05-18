package domain;

import exceptions.UngueltigeAuswahlException;
import persistence.NeuesSpielEinlesen;
import valueobjects.*;
import ui.cui.Menue;

import java.io.IOException;
import java.util.*;

public class Spiel {
    ArrayList<Spieler> spielerListe = new ArrayList<>();
    Welt welt = new Welt(spielerListe);
    HashSet<Karte> kartenStapel = new HashSet<>();

    NeuesSpielEinlesen einlesen = new NeuesSpielEinlesen();

    public Welt getWelt() {
        return welt;
    }

    public Spiel() throws IOException {
        System.out.println("Starte Spiel...");
    }

    public void starteSpiel(Menue menue) throws IOException, UngueltigeAuswahlException {
        try{
            menue.spielerAbfrage(spielerListe);
        } catch (UngueltigeAuswahlException e) {
            System.out.println("Fehler: " + e.getMessage());
            System.out.println("Nocheinmal: \n");
        }
        welt.printWorldMap();
        welt.verteileLaender(spielerListe);
        kartenStapel.addAll((einlesen.kartenstapelEinlesen(einlesen.alleLaenderEinlesen())));
        menue.zeigeAlleSpieler(spielerListe);
        do {
            spielRunde(menue);
        } while (spielRunde(menue));
    }

    public boolean spielRunde(Menue menue) throws UngueltigeAuswahlException {

        for (Spieler spieler : spielerListe) {
            menue.setSpieler(spieler);

            if (!spieler.isAlive()) {
                continue;
            }
            //Truppen erhalten
            spieler.neueArmee(welt.alleKontinente);
            if (!spieler.getKarten().isEmpty()) {
                menue.peruseCards(spieler);
            }
            spieler.setSchonErobert(false);
            boolean amZug = true;
            while (amZug) {
                amZug = menue.hauptMenue(welt, spieler);
            }
        }
        return true;
    }

    //region playing Cards
    public void zieheKarte(Spieler spieler) throws NoSuchElementException {//ToDo catch exception from orElseThrow @Maj: Nein
        Optional<Karte> optionalCard = kartenStapel.stream().findFirst();
        Karte karte = optionalCard.orElseThrow();
        spieler.getKarten().add(karte);
    }

    public void spieleKarte(Spieler spieler, Karte karte) {
        if (spieler.getKarten().contains(karte)) {
            spieler.getKarten().remove(karte);
            spieler.zuweisungEinheiten(karte.getStrength());
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