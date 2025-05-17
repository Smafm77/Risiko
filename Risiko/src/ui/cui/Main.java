package ui.cui;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        System.out.println("Willkommen zu Risiko");

        while (running) {       //Inhalt der schleife ins ui.cui.Menue?
            System.out.println("1. domain.Spiel starten");
            System.out.println("666. Beenden");
            System.out.println("Bitte eine Option auswählen");
            String auswahl = scanner.nextLine();

            switch (auswahl) {
                case "1":
                    Spiel spiel = new Spiel();
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