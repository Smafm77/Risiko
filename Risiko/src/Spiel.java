import java.util.ArrayList;
import java.util.Scanner;

public class Spiel {
    ArrayList<Spieler> spielerListe = new ArrayList<Spieler>();

    public void starteSpiel() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bitte die Anzahl an Spielern eingeben:");
        int anzahlSpieler = scanner.nextInt();
        scanner.nextLine(); //Weil scanner.nextInt immer mucken macht einfach nochmal nextLine "einlesen"

        for (int i = 1; i <= anzahlSpieler; i++) {
            System.out.println("Bitte Namen des Spielers eingeben Spieler Nr. " + i + " :");
            Spieler spieler = new Spieler(scanner.nextLine(), i);
            spielerListe.add(spieler);
        }
        Welt welt = new Welt();
        welt.verteileLaender(spielerListe);

        printPlayers(spielerListe);
    }

    public void spielRunde() {
        for (int j = 1; j <=spielerListe.size() ; j++) {
            spielerListe.get(j).neueArmee();
            //Todo abfrage was getan werden soll
        }
    }

    public void printPlayers(ArrayList<Spieler> spielerListe) {
        for (Spieler spieler : spielerListe) {
            System.out.println(spieler.id + " - " + spieler.name + " - " + spieler.besetzteLaender.size());
            for (Land land : spieler.besetzteLaender) {
                System.out.println(land.besitzer.id + ": " + land.name);
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
    }

}
