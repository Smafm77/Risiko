package ui;

import domain.Spiel;
import exceptions.FalscherBesitzerException;
import exceptions.UngueltigeAuswahlException;
import exceptions.UngueltigeBewegungException;
import persistence.SpielSpeichern;
import ui.cui.Menue;

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
    private Spiel spiel = null;
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
                        spiel = new Spiel();
                        menue.setSpiel(spiel);
                        spiel.starteSpiel(menue);
                        break;
                    case 2:
                        spielSpeichern();
                        break;
                    case 3:
                        spielLaden();
                        break;
                    case 4:
                        System.out.println("Wird beendet");
                        return;
                }

            } catch (UngueltigeAuswahlException | FalscherBesitzerException | UngueltigeBewegungException e) {
                System.out.println("Fehler: " + e.getMessage());
            } catch (IOException | ClassNotFoundException e) {
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

    private void spielSpeichern() throws IOException {
        if (spiel == null) {
            System.out.println("Kein Spiel zum Speichern gefunden!");
        } else {
            try {
                SpielSpeichern.speichern(spiel, "spielstand.risiko");
                System.out.println("Spiel erfolgreich gespeichert!");
            } catch (IOException e) {
                throw new IOException("Speichern fehlgeschlagen: " + e.getMessage(), e);
            }
        }
    }

    private void spielLaden() throws IOException, ClassNotFoundException, FalscherBesitzerException, UngueltigeBewegungException, UngueltigeAuswahlException {
        spiel = SpielSpeichern.laden("spielstand.risiko");
        System.out.println("Spiel erfolgreich geladen!");
        menue.setSpiel(spiel);
        spiel.continueSpiel(menue);

    }
}
