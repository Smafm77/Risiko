package ui.cui;

import domain.Spiel;
import exceptions.FalscherBesitzerException;
import exceptions.UngueltigeAuswahlException;
import exceptions.UngueltigeBewegungException;
import persistence.SpielSpeichern;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.InputMismatchException;
import java.util.Scanner;


public class Main implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        int auswahl;
        Spiel spiel = null;
        Menue menue = new Menue();
        while (true) {
            System.out.println("Willkommen zu Risiko");
            System.out.println("1. Spiel starten");
            System.out.println("2. Spiel speichern");
            System.out.println("3. Spiel laden");
            System.out.println("4. Beenden");
            System.out.println("Bitte eine Option auswählen");
            try {
                try {
                    auswahl = scanner.nextInt();
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                    throw new UngueltigeAuswahlException("Eingabe muss eine Zahl sein!");
                }
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
                        if (spiel == null) {
                            System.out.println("Kein Spiel zum speichern gefunden");
                        } else {
                            try {
                                SpielSpeichern.speichern(spiel, "spielstand.risiko");
                                System.out.println("Spiel erfolgreich gespeichert!");
                            } catch (IOException e) {
                                System.out.println("Fehler beim Speichern: " + e.getMessage());
                            }
                        }
                        break;
                    case 3:
                        try {
                            spiel = SpielSpeichern.laden("spielstand.risiko");
                            System.out.println("Spiel erfolgreich geladen!");
                            if(spiel != null){
                                menue.setSpiel(spiel);
                                spiel.starteSpiel(menue);
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            System.out.println("Fehler beim Laden: " + e.getMessage());
                        }
                        break;
                    case 4:
                        System.out.println("Wird beendet");
                        return;
                }

            } catch (UngueltigeAuswahlException | FalscherBesitzerException | UngueltigeBewegungException e) {
                System.out.println("Fehler: " + e.getMessage());
                System.out.println("Nocheinmal: \n");
            }

        }

    }


}