package ui.cui;

import domain.Spiel;

import java.io.IOException;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Willkommen zu Risiko");
        boolean running = true;
        while (running) {
            System.out.println("1. Spiel starten");
            System.out.println("666. Beenden");
            System.out.println("Bitte eine Option auswählen");
            String auswahl = scanner.nextLine();
            switch (auswahl) {
                case "1":
                    Spiel spiel = new Spiel();
                    Menue menue = new Menue();
                    spiel.starteSpiel(menue);
                    menue.setSpiel(spiel);
                    break;
                case "666":
                    System.out.println("Wird beendet");
                    running = false;
                    break;

                default:
                    System.out.println("Ungültige Eingabe");

            }
        }
    }

}
