package ui.cui;

import valueobjects.Land;

import java.util.Scanner;

public class Menue {
    Scanner scanner = new Scanner(System.in);
    private Spieler aktuellerSpieler;
    private Land auswahlLand;


    public void setSpieler(Spieler spieler) {
        this.aktuellerSpieler = spieler;
    }

    public void setLand(Land auswahlLand) {
        this.auswahlLand = auswahlLand;
    }

    public enum Befehl { //Eigenes Enum
        ANGRIFF(1),
        BEWEGEN(2),
        INFO(3),
        UEBERSICHT(4),
        ZUGBEENDEN(5),
        SPIELBEENDEN(666);

        private final int auswahl;

        Befehl(int auswahl) {
            this.auswahl = auswahl;
        }

        public int getAuswahl() {
            return auswahl;
        }

        public static Befehl fromInt(int auswahl) {
            for (Befehl b : values()) {
                if (b.getAuswahl() == auswahl) {
                    return b;
                }
            }
            return null;
        }
    }

    public Befehl eingabeEinlesen() {
            hauptMenue();
            int auswahl = scanner.nextInt();
            scanner.nextLine();
            return Befehl.fromInt(auswahl);
    }

    private void hauptMenue() {
        System.out.println("Du bist am Zug : " + aktuellerSpieler.getName());
        System.out.println("Was willst du tun? ");
        System.out.println("1: Angreifen");
        System.out.println("2: Truppen bewegen");
        System.out.println("3: Infos über...");
        System.out.println("4: Übersicht meiner Gebiete");
        System.out.println("5: Zug beenden");
        System.out.println("666: domain.Spiel beenden");
    }

    public enum Infos { //Eigenes Enum
        BESITZER(1),
        EINHEITEN(2),
        NACHBARN(3),
        ZURUECK(666);

        private final int auswahl;

        Infos(int auswahl) {
            this.auswahl = auswahl;
        }

        public int getAuswahl() {
            return auswahl;
        }

        public static Infos fromInt(int auswahl) {
            for (Infos i : values()) {
                if (i.getAuswahl() == auswahl) {
                    return i;
                }
            }
            return null;
        }
    }

    public Infos infoAbfrage() {
            infoMenue();
            int auswahl = scanner.nextInt();
            scanner.nextLine();
            return Infos.fromInt(auswahl);
    }

    private void infoMenue() {
        System.out.println("Welche Informationen möchtest du über " + auswahlLand + " erhalten?");
        System.out.println("1: Besitzer");
        System.out.println("2: Einheiten auf valueobjects.Land");
        System.out.println("3: Nachbarländer von " + auswahlLand);
        System.out.println("666: Zurück");
    }
}
