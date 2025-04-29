import java.util.ArrayList;
import java.util.Scanner;

public class Spiel {
    public static void starteSpiel() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bitte die Anzahl an Spielern eingeben:");
        int anzahlSpieler = scanner.nextInt();
        scanner.nextLine(); //Weil scanner.nextInt immer mucken macht einfach nochmal nextLine "einlesen"
        ArrayList<Spieler> spielerListe = new ArrayList<Spieler>();
        for (int i = 1; i <= anzahlSpieler; i++) {
            System.out.println("Bitte Namen des Spielers eingeben Spieler Nr. " + i + " :");
            Spieler spieler = new Spieler(scanner.nextLine(), i);
            spielerListe.add(spieler);
        }
        Welt welt = new Welt();
        welt.verteileLaender(spielerListe);

        printPlayers(spielerListe);
    }

    public static void printPlayers(ArrayList<Spieler> spielerListe){
        for (Spieler spieler : spielerListe){
            System.out.println(spieler.id + " - " + spieler.name + " - " + spieler.besetzteLaender.size());
            for (Land land : spieler.besetzteLaender){
                System.out.println(land.besitzer.id + ": " + land.name);
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
    }

}
