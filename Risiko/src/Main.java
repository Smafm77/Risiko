import java.util.Scanner;

public class Main {
    public static void starteSpiel() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bitte die Anzahl an Spielern eingeben:");
        int anzahlSpieler = scanner.nextInt();
        scanner.nextLine(); //Weil scanner.nextInt immer mucken macht einfach nochmal nextLine "einlesen"
        for(int i = 1; i<= anzahlSpieler; i++){
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