package client.ui;

import common.enums.Spielphase;
import common.valueobjects.ISpiel;
import common.valueobjects.Spieler;
import common.exceptions.*;
import client.ui.cui.Menue;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Risiko implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Scanner scanner = new Scanner(System.in);
    private ISpiel spiel = null;
    private final Menue menue = new Menue();

    public void start() {
        try {
            starteMenue();
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
            System.out.println("Nocheinmal: \n");
        }
    }

    private void starteMenue() {
        while (true) {
            System.out.println("Willkommen zu Risiko");
            System.out.println("1. Spiel starten");
            System.out.println("2. Spiel speichern");
            System.out.println("3. Spiel laden");
            System.out.println("4. Beenden");
            System.out.println("Bitte eine Option auswählen");
            try {

                int auswahl = zahlEinlesen();

                if (auswahl < 1 || auswahl > 4) {
                    throw new UngueltigeAuswahlException("Wähle zwischen 1 und 4.");
                }
                switch (auswahl) {
                    case 1:
                        //spiel = new RisikoClient();
                        menue.setSpiel(spiel);
                        menue.buildWelt();
                        spiel.init();
                        continueSpiel(menue);

                        break;
                    case 2:
                        //spielSpeichern();
                        System.out.println("Hier wird eigentlich gespeichert");
                        break;
                    case 3:
                        //spielLaden();
                        System.out.println("Hier wird eigentlich geladen");
                        break;
                    case 4:
                        System.out.println("Wird beendet");
                        return;
                }

            } catch (UngueltigeAuswahlException | FalscherBesitzerException | UngueltigeBewegungException e) {
                System.out.println("Fehler: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Dateifehler: " + e.getMessage());
            }
        }
    }

    private int zahlEinlesen() throws UngueltigeAuswahlException {
        try {
            int eingabe = scanner.nextInt();
            scanner.nextLine();
            return eingabe;
        } catch (InputMismatchException e) {
            scanner.nextLine();
            throw new UngueltigeAuswahlException("Eingabe muss eine Zahl sein!");
        }
    }

    public void continueSpiel(Menue menue) throws IOException, UngueltigeAuswahlException, FalscherBesitzerException, UngueltigeBewegungException {
        boolean nochEinmal;
        do {
            nochEinmal = spielRunde(menue);
        } while (nochEinmal);
    }

    private boolean spielRunde(Menue menue) {
        menue.setSpieler(spiel.getAktuellerSpieler());
        Spieler spieler = menue.getAktuellerSpieler();
        if (!menue.getmLogik().weiterSpielen()) {
            return false;
        }

        //Truppen erhalten
        if(spiel.getPhase().equals(Spielphase.VERTEILEN)) {
            System.out.println("Du bist dran " + spieler.getName() + " [" + spiel.getMissionBeschreibung(spieler.getId()) + "]");
            int neueEinheiten = spieler.berechneNeueEinheiten(spiel.getWelt().alleKontinente);
            menue.getmEingabe().zuweisungEinheiten(neueEinheiten, spieler);
            spiel.naechstePhase();
        }
        if(spiel.getPhase().equals(Spielphase.ANGRIFF)) {
            boolean weiterAngreifen = true;
            while (weiterAngreifen) {
                weiterAngreifen = menue.hauptMenue(spieler);
            }
            spiel.naechstePhase();
        }
        if(spiel.getPhase().equals(Spielphase.VERSCHIEBEN)) {
            boolean weiterVerschieben = true;
            while (weiterVerschieben) {
                weiterVerschieben = menue.hauptMenue(spieler);
            }
        }

        if (spiel.hatMissionErfuellt(spieler.getId())) {
            System.out.println("Herzlichen Glückwunsch! Mission erfüllt: " + spiel.getMissionBeschreibung(spieler.getId()));
        }

        spiel.naechstePhase();

        return true;
    }
}
