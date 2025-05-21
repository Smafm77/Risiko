package ui.cui;

import enums.Befehl;
import enums.Infos;
import exceptions.UngueltigeAuswahlException;
import valueobjects.Land;
import valueobjects.Spieler;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MenueEingabe {
    Scanner scanner = new Scanner(System.in);
    Menue menue;

    public MenueEingabe(Menue menue) {
        this.menue = menue;
    }

    public Befehl hauptAbfrage() {
        int auswahl;
        while (true) {
            try {
                try {
                    auswahl = scanner.nextInt();
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                    throw new UngueltigeAuswahlException("Eingabe muss eine Zahl sein!");
                }


                scanner.nextLine();
                if (auswahl < 1 || auswahl > 6) {
                    throw new UngueltigeAuswahlException("Bitte wähle eine Option von 1-6.");
                }
                break;
            } catch (UngueltigeAuswahlException e) {
                System.out.println("Fehler: " + e.getMessage());
                System.out.println("Noch einmal: /n");
            }
        }

        return Befehl.fromInt(auswahl);
    }

    public Infos infoAbfrage() {
        int auswahl;
        while (true) {
            try {
                try {
                    auswahl = scanner.nextInt();
                    scanner.nextLine();
                    if (auswahl < 1 || auswahl > 4) {
                        throw new UngueltigeAuswahlException("Bitte wähle eine Option von 1-4.");
                    }
                } catch (InputMismatchException e) {
                    throw new UngueltigeAuswahlException("Eingabe muss eine Zahl sein!");
                }
            } catch (UngueltigeAuswahlException e) {
                System.out.println("Fehler: " + e.getMessage());
                System.out.println("Noch einmal: /n");
                continue;
            }
            break;
        }
        return Infos.fromInt(auswahl);
    }

    public void spielerAnlegen(int anzahlSpieler) {
        for (int i = 1; i <= anzahlSpieler; i++) {
            while (true) {
                try {
                    System.out.println("Bitte Namen des Spielers eingeben Spieler Nr. " + i + " :");
                    String name = scanner.nextLine().trim();
                    if (name.isEmpty()) {
                        throw new UngueltigeAuswahlException("Spielername darf nicht leer sein!");
                    }
                    Spieler spieler = new Spieler(name, i);
                    menue.getWelt().getSpielerListe().add(spieler);
                    break;
                } catch (UngueltigeAuswahlException e) {
                    System.out.println("Fehler: " + e.getMessage());
                    System.out.println("Nocheinmal: \n");
                }
            }
        }
    }

    public void spielerAbfrage()  {
        System.out.println("Bitte die Anzahl an Spielern eingeben:");
        int anzahlSpieler;
        while (true) {
            try {
                try {
                    anzahlSpieler = scanner.nextInt();
                } catch (InputMismatchException e) {
                    scanner.nextLine(); //falsche Eingabe verwerfen, sonst endlosschleife
                    throw new UngueltigeAuswahlException("Eingabe muss eine Zahl sein!");
                }
                if (anzahlSpieler <= 2 || anzahlSpieler > 6) {
                    throw new UngueltigeAuswahlException("Spieleranzahl muss zwischen 3-6 liegen.");
                }
                scanner.nextLine(); //Weil scanner.nextInt immer mucken macht einfach nochmal nextLine "einlesen"
                spielerAnlegen(anzahlSpieler);
                break;
            } catch (UngueltigeAuswahlException e) {
                System.out.println("Fehler: " + e.getMessage());
                System.out.println("Nocheinmal: \n");
            }
        }
    }

    public Land eingabeLand() {
        Land auswahlLand;
        while (true) {
            System.out.println("Land: ");
            String eingabe = scanner.nextLine();
            try {
                if (eingabe.isEmpty()) {
                    throw new UngueltigeAuswahlException("Land darf nicht leer sein!");
                }

                auswahlLand = menue.getWelt().findeLand(eingabe);
                return auswahlLand;
            } catch (UngueltigeAuswahlException e) {
                System.out.println("Fehler: " + e.getMessage());
                System.out.println("Nocheinmal: \n");
            }
        }
    }

    public void zuweisungEinheiten(int truppen, Spieler spieler) {
        for (int t = 1; t <= truppen; t++) {
            while (true) {
                System.out.println("(" + t + "/" + truppen + ")Wohin soll diese Einheit gesetzt werden ?");
                System.out.println();
                spieler.zeigeSpieler();

                Land basis = eingabeLand();
                basis.einheitenHinzufuegen(1);
                break;
            }
        }
    }
}
