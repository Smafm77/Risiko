package client.ui.cui;

import common.exceptions.*;
import common.valueobjects.*;
import common.enums.Spielphase;

import java.io.IOException;
import java.util.*;


public class Menue {
    Scanner scanner = new Scanner(System.in);
    private Spieler aktuellerSpieler;
    private ISpiel spiel;
    private Welt welt;
    MenuePrint mPrint = new MenuePrint(this);
    MenueEingabe mEingabe = new MenueEingabe(this);
    MenueLogik mLogik = new MenueLogik(this, mPrint, mEingabe);

    public void setSpieler(Spieler spieler) {
        this.aktuellerSpieler = spieler;
    }

    public void setSpiel(ISpiel spiel) {
        this.spiel = spiel;
        this.welt = spiel.getWelt();
    }

    public Spieler getAktuellerSpieler() {
        return aktuellerSpieler;
    }

    public Welt getWelt() {
        return welt;
    }

    public ISpiel getSpiel() {
        return spiel;
    }

    public MenueEingabe getmEingabe() {
        return mEingabe;
    }

    public MenueLogik getmLogik() {
        return mLogik;
    }

    public void buildWelt() throws IOException {
        mEingabe.spielerAbfrage();
        mPrint.printWorldMap();
        mPrint.zeigeAlleSpieler(spiel.getSpielerListe());
    }

    public boolean hauptMenue(Spieler spieler) {
        while (true) {
            try {
                Spielphase phase = spiel.getPhase();
                System.out.println("Du bist am Zug : " + spieler.getName() + " Phase: " + phase);
                if (phase == Spielphase.ANGRIFF) {
                    System.out.println("1: Angreifen");
                }
                if (phase == Spielphase.VERSCHIEBEN) {
                    System.out.println("2: Truppen bewegen");
                }
                System.out.println("3: Infos über...");
                System.out.println("4: Übersicht meiner Gebiete");
                System.out.println("5: Karte nutzen");
                if (phase == Spielphase.ANGRIFF) {
                    System.out.println("6: Angriff beenden");
                } else {
                    System.out.println("6: Zug beenden");
                }
                return mLogik.hauptAuswahl(spieler, spiel.getPhase());
            } catch (EinheitenAnzahlException | FalscherBesitzerException | UngueltigeBewegungException |
                     UngueltigeAuswahlException | SpielPhaseException e) {
                System.out.println("Fehler: " + e.getMessage());
                System.out.println("Noch einmal: \n");
            }
        }
    }

    public boolean infoMenue(Land auswahlLand) {
        System.out.println("Welche Informationen möchtest du über " + auswahlLand.getName() + " erhalten?");
        System.out.println("1: Besitzer");
        System.out.println("2: Einheiten auf Land");
        System.out.println("3: Nachbarländer von " + auswahlLand.getName());
        System.out.println("4: Zurück");
        return mLogik.infoAuswahl(auswahlLand);
    }

    public void peruseCards(Spieler spieler) {
        try {
            if (spieler.getKarten().isEmpty()) {
                throw new UngueltigeAuswahlException("Du hast keine Karten zum ausspielen.");
            }

            while (!spieler.getKarten().isEmpty()) {
                mPrint.printTheseLaender(spieler.getBesetzteLaender());
                System.out.println();
                System.out.println("Karten:");
                System.out.println(spielerKartenToString(spieler));
                System.out.println("Welche Karte willst du ausspielen?");
                System.out.println("Zum Abbrechen wähle N");

                String input = scanner.next();
                if (input.equals("N")) {
                    break;
                }
                Optional<Karte> optChosenCard = spieler.getKarten().stream().filter(c -> c.getLand().getName().equalsIgnoreCase(input.trim())).findFirst(); //finds the chosen Card by it's name and throws an Error if it doesn't exist
                if (optChosenCard.isPresent()) {
                    Karte chosenCard = optChosenCard.orElseThrow();
                    mEingabe.zuweisungEinheiten(spiel.spieleKarte(spieler.getId(), chosenCard), spieler);
                }
            }
        } catch (NoSuchElementException | UngueltigeAuswahlException e) {
            System.out.println("Fehler: " + e.getMessage());
            System.out.println("Nocheinmal: \n");
        }
    }
    private String spielerKartenToString(Spieler spieler) {
        StringBuilder kartenTxt = new StringBuilder();
        for (Karte karte : spieler.getKarten()) {
            kartenTxt.append("[").append(karte.getStrength()).append(" - ").append(karte.getLand().getName()).append("]  ");
        }
        return kartenTxt.toString();
    }


}


