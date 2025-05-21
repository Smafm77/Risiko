package ui.cui;

import domain.Spiel;
import exceptions.FalscherBesitzerException;
import exceptions.UngueltigeAuswahlException;
import exceptions.UngueltigeBewegungException;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        int auswahl;

        System.out.println("Willkommen zu Risiko");
        System.out.println("1. Spiel starten");
        System.out.println("2. Beenden");
        System.out.println("Bitte eine Option auswählen");
        while (true) {
            try {
                try {
                    auswahl = scanner.nextInt();
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                    throw new UngueltigeAuswahlException("Eingabe muss eine Zahl sein!");
                }
                if (auswahl < 1 || auswahl > 2) {
                    throw new UngueltigeAuswahlException("Wähle zwischen 1 und 2.");
                }
                switch (auswahl) {
                    case 1:
                        Spiel spiel = new Spiel();
                        Menue menue = new Menue();
                        menue.setSpiel(spiel);
                        spiel.starteSpiel(menue);
                    case 2:
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