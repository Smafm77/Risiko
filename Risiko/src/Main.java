import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void starteSpiel() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bitte die Anzahl an Spielern eingeben:");
        int anzahlSpieler = scanner.nextInt();
        scanner.nextLine(); //Weil scanner.nextInt immer mucken macht einfach nochmal nextLine "einlesen"
        List<Land> alleLaender = new ArrayList<>();
        alleLaender.add(new Land("Land1", "Land2"));
        alleLaender.add(new Land("Land2", "Land1", "Land3"));
        alleLaender.add(new Land("Land3", "Land2", "Land4", "Land5"));
        alleLaender.add(new Land("Land4", "Land3"));
        alleLaender.add(new Land("Land5", "Land3"));
        alleLaender.add(new Land("Land6"));
        alleLaender.add(new Land("Land7"));
        alleLaender.add(new Land("Land8"));
        alleLaender.add(new Land("Land9"));
        alleLaender.add(new Land("Land10"));
        for (int i = 1; i <= anzahlSpieler; i++) {
            Spieler spieler = new Spieler();
        }


    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        System.out.println("Willkommen zu Risiko");

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