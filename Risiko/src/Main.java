import java.awt.*;
import java.util.Scanner;

public class Main {
    public static void starteSpiel() {
        System.out.println("Dummy damit das Programm nicht abstürzt");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        System.out.println("Risiko");

        while (running) {
            System.out.println("1. Spiel starten");
            System.out.println("2. Beenden");
            System.out.println("Bitte eine Option auswählen");
            String auswahl = scanner.nextLine();

            switch (auswahl) {
                case "1":
                    starteSpiel();
                    break;
                case "2":
                    System.out.println("Wird beendet");
                    running = false;
                    break;

                default:
                    System.out.println("Ungültige Eingabe");

            }
        }
    }
}