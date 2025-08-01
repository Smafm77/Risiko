package client.ui.cui;

import common.enums.Befehl;
import common.enums.Infos;
import common.exceptions.FalscherBesitzerException;
import common.exceptions.UngueltigeAuswahlException;
import common.valueobjects.Land;
import common.valueobjects.Spieler;

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

                    if (auswahl < 1 || auswahl > 4) {
                        scanner.nextLine();
                        throw new UngueltigeAuswahlException("Bitte wähle eine Option von 1-4.");
                    }
                } catch (InputMismatchException e) {
                    throw new UngueltigeAuswahlException("Eingabe muss eine Zahl sein!");
                }
                scanner.nextLine();
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
        ArrayList<Spieler> spielerListe = new ArrayList<>();
        for (int i = 1; i <= anzahlSpieler; i++) {
            while (true) {
                try {
                    System.out.println("Bitte Namen des Spielers eingeben Spieler Nr. " + i + " :");
                    String name = scanner.nextLine().trim();
                    if (name.isEmpty()) {
                        throw new UngueltigeAuswahlException("Spielername darf nicht leer sein!");
                    }
                    Spieler spieler = new Spieler(name, i);
                    spielerListe.add(spieler);
                    break;
                } catch (UngueltigeAuswahlException e) {
                    System.out.println("Fehler: " + e.getMessage());
                    System.out.println("Nocheinmal: \n");
                }
            }
        }
        menue.getWelt().setSpielerListe(spielerListe);
        menue.getSpiel().weiseMissionenZu();
    }

    public void spielerAbfrage() {
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
                scanner.nextLine();
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

    public Land istEigenesLand(Spieler spieler) throws FalscherBesitzerException {
        Land auswahlLand = eingabeLand();
        if (auswahlLand.getBesitzer() != spieler) {
            throw new FalscherBesitzerException("Dieses Land gehört dir nicht!");
        }
        return auswahlLand;
    }

    public Land istFeind(Spieler spieler) throws FalscherBesitzerException {
        Land auswahlLand = eingabeLand();
        if (auswahlLand.getBesitzer() == spieler) {
            throw new FalscherBesitzerException("Dieses Land gehört dir!");
        }
        return auswahlLand;
    }

    public int eingabeTruppen() throws UngueltigeAuswahlException {
        int auswahl;
        try {
            auswahl = scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.nextLine();
            throw new UngueltigeAuswahlException("Eingabe muss eine Zahl sein!");
        }
        scanner.nextLine();
        return auswahl;
    }

    public void zuweisungEinheiten(int truppen, Spieler spieler) {
        for (int t = 1; t <= truppen; t++) {
            Land basis;
            while (true) {
                try {
                    System.out.println("(" + t + "/" + truppen + ")Wohin soll diese Einheit gesetzt werden ?");
                    menue.mPrint.zeigeSpieler(spieler);

                    basis = eingabeLand();
                    if (basis.getBesitzer() != spieler) {
                        throw new FalscherBesitzerException("Dieses Land gehört dir nicht!");
                    }
                } catch (FalscherBesitzerException e) {
                    System.out.println("Fehler: " + e.getMessage());
                    System.out.println("Noch einmal: \n");
                    continue;
                }
                basis.einheitenHinzufuegen(1);
                break;
            }
        }
    }


    public int inGameMenue() {

        while (true) {
            System.out.println("1. Weiterspielen");
            System.out.println("2. Spiel beenden");
            System.out.println("Was willst du tun?");
            int auswahl;
            try {
                try {
                    auswahl = scanner.nextInt();
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                    throw new UngueltigeAuswahlException("Eingabe muss eine Zahl sein!");
                }
                if (auswahl < 1 || auswahl > 2) {
                    throw new UngueltigeAuswahlException("Bitte wähle zwischen 1. und 2.!");
                }
                scanner.nextLine();
                return auswahl;

            } catch (UngueltigeAuswahlException e) {
                System.out.println("Fehler: " + e.getMessage());
                System.out.println("Nocheinmal: \n");
            }

        }
    }
}
