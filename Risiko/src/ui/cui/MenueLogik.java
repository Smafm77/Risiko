package ui.cui;

import enums.Befehl;
import enums.Infos;
import exceptions.EinheitenAnzahlException;
import exceptions.FalscherBesitzerException;
import exceptions.UngueltigeAuswahlException;
import exceptions.UngueltigeBewegungException;
import valueobjects.Land;
import valueobjects.Spieler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MenueLogik {
    Menue menue;
    MenuePrint mPrint;
    MenueEingabe mEingabe;
    Scanner scanner = new Scanner(System.in);

    public MenueLogik(Menue menue, MenuePrint mPrint, MenueEingabe mEingabe) {
        this.menue = menue;
        this.mPrint = mPrint;
        this.mEingabe = mEingabe;
    }

    public boolean infoLand() {
        System.out.println("Über welches Land möchtest du Informationen erhalten?");
        Land auswahlLand = mEingabe.eingabeLand();
        while (true) {
            return menue.infoMenue(auswahlLand);
        }
    }

    public boolean infoAuswahl(Land auswahlLand) {

        Infos auswahl = mEingabe.infoAbfrage();
        switch (auswahl) {
            case BESITZER:
                System.out.println(auswahlLand.getName() + " befindet sich in " + auswahlLand.getBesitzer().getName() + "'s Besitz.");
                break;
            case EINHEITEN:
                System.out.println("In " + auswahlLand.getName() + " befinden sich aktuell " + auswahlLand.getEinheiten() + " Einheiten.");
                break;
            case NACHBARN:
                System.out.println("Die Nachbarländer von " + auswahlLand.getName() + " sind: ");
                for (Land nachbar : auswahlLand.getNachbarn()) {
                    System.out.println(nachbar.getName());
                }
                break;
            case ZURUECK:
                return false;
        }
        return true;

    }

    public boolean hauptAuswahl(Spieler spieler) {
        Befehl auswahl = mEingabe.hauptAbfrage();
        switch (auswahl) {
            case ANGRIFF:
                boolean ergebnis = kampfInterface(spieler);
                if (ergebnis && !spieler.getSchonErobert()) {
                    menue.getSpiel().zieheKarte(spieler);
                    spieler.setSchonErobert(true);
                }
                break;
            case BEWEGEN:
                moveTroopsInterface(spieler);
                break;
            case INFO:
                infoLand();
                break;
            case UEBERSICHT:
                mPrint.printTheseLaender(spieler.getBesetzteLaender());
                break;
            case KARTE:
                menue.peruseCards(spieler);
                break;
            case ZUGBEENDEN:
                return false;
        }
        return true;

    }

    public void moveTroopsInterface(Spieler spieler) {

        try {
            mPrint.zeigeEigeneGebiete(spieler);
            while (true) {
                System.out.println("Aus welchem Land sollen Einheiten entzogen werden?");
                Land herkunft = mEingabe.eingabeLand();
                if (herkunft.getBesitzer() != spieler) {
                    throw new FalscherBesitzerException("Dieses Land gehört dir nicht!");
                }
                while (true) {
                    System.out.println("In welches Land sollen die Einheiten geschickt werden?");
                    Land ziel = mEingabe.eingabeLand();
                    if (ziel.getBesitzer() != spieler) {
                        throw new FalscherBesitzerException("Dieses Land gehört dir nicht!");
                    }
                    if (!herkunft.connectionPossible(ziel)) {
                        throw new UngueltigeBewegungException(ziel.getName() + " ist von " + herkunft.getName() + " aus nicht erreichbar.");
                    }
                    System.out.println("Wie viele der " + herkunft.getEinheiten() + " Einheiten aus " + herkunft.getName() + " sollen nach " + ziel.getName() + " entsendet werden?");
                    int anzahl;
                    while (true) {
                        try {
                            anzahl = scanner.nextInt();
                        } catch (InputMismatchException e) {
                            throw new UngueltigeAuswahlException("Eingabe muss eine Zahl sein!");
                        }
                        if (herkunft.getEinheiten() - anzahl < 1) {
                            throw new EinheitenAnzahlException("Du hast nicht genug Einheiten in " + herkunft.getName() + " für diese Aktion. Es muss immer mindestens 1 Einheit im Herkunftsland verbleiben.");
                        }
                        break;
                    }
                    spieler.bewegeEinheiten(anzahl, herkunft, ziel);
                    System.out.println("Neue Anzahl Einheiten: " + herkunft.getName() + ": " + herkunft.getEinheiten() + " , " + ziel.getName() + ": " + ziel.getEinheiten());
                    break;
                }
                break;
            }
        } catch (EinheitenAnzahlException | FalscherBesitzerException | UngueltigeBewegungException |
                 UngueltigeAuswahlException e) {
            System.out.println("Fehler: " + e.getMessage());
            System.out.println("Noch einmal: \n");
        }
    }


    public boolean kampfInterface(Spieler angreifer) {
        HashSet<Land> volleKasernen = angreifer.getBesetzteLaender().stream().filter(land -> land.getEinheiten() > 1).collect(Collectors.toCollection(HashSet::new));
        boolean ergebnis = false;
        Land herkunft;
        Land ziel;
        while (!volleKasernen.isEmpty()) {
            try {
                mPrint.printTheseLaender(volleKasernen);
                ArrayList<Land> relevanteLaender = new ArrayList<>();
                System.out.println("Aus welchem Land willst du angreifen?");
                while (true) {
                    herkunft = mEingabe.eingabeLand();
                    if (!volleKasernen.contains(herkunft)) {

                        throw new FalscherBesitzerException("Dieses Land gehört dir nicht!");
                    }
                    relevanteLaender.add(herkunft);
                    mPrint.printTheseLaenderNamen(herkunft.getFeindlicheNachbarn());
                    System.out.println("Welches Land möchtest du angreifen?");
                    while (true) {
                        ziel = mEingabe.eingabeLand();
                        if (!herkunft.getFeindlicheNachbarn().contains(ziel)) {
                            throw new UngueltigeBewegungException("Du kannst nicht von " + herkunft + " aus " + ziel + " angreifen.");
                        }
                        break;
                    }

                    relevanteLaender.add(ziel);
                    Spieler verteidiger = ziel.getBesitzer();

                    System.out.println("Mit wie vielen Truppen möchtest du angreifen (1 - " + Math.min((herkunft.getEinheiten() - 1), 3) + ")?");
                    int truppenA;
                    while (true) {
                        try {
                            truppenA = scanner.nextInt();
                        } catch (InputMismatchException e) {
                            throw new UngueltigeAuswahlException("Eingabe muss eine Zahl sein!");
                        }
                        if (truppenA >= herkunft.getEinheiten()) {
                            throw new EinheitenAnzahlException("Es muss immer mindestens 1 Truppe in " + herkunft + " verbleiben.");
                        } else if (truppenA < 1) {
                            throw new EinheitenAnzahlException("Du brauchst mindestens 1 Truppe zum Angreifen.");

                        } else if (truppenA > 3) {
                            throw new EinheitenAnzahlException("Du darfst nur maximal 3 Truppen zum Angriff nutzen.");
                        }
                        break;
                    }
                    System.out.println();
                    System.out.println(verteidiger.getName() + " mit wie vielen Truppen möchtest du dich verteidigen (1 - " + Math.min((ziel.getEinheiten()), 2) + ")?");
                    int truppenV;

                    while (true) {
                        try {
                            try {
                                truppenV = scanner.nextInt();
                            } catch (InputMismatchException e) {
                                scanner.nextLine();
                                throw new UngueltigeAuswahlException("Eingabe muss eine Zahl sein!");
                            }

                            if (truppenV > ziel.getEinheiten()) {
                                throw new EinheitenAnzahlException(ziel.getName() + " hat nur " + ziel.getEinheiten() + " Einheiten zur Verfügung.");
                            } else if (truppenV < 1) {
                                throw new EinheitenAnzahlException("Du brauchst mindestens 1 Truppe zum Verteidigen.");

                            } else if (truppenV > 2) {
                                throw new EinheitenAnzahlException("Du darfst nur maximal 2 Truppen zum Angriff nutzen.");
                            }
                            break;
                        } catch (UngueltigeAuswahlException | EinheitenAnzahlException | InputMismatchException e) {
                            System.out.println("Fehler: " + e.getMessage());
                            System.out.println("Noch einmal: \n");
                        }
                    }
                    System.out.println();
                    ergebnis = menue.getSpiel().kampf(herkunft, ziel, truppenA, truppenV);
                    String sieger = ergebnis ? angreifer.getName() : verteidiger.getName();
                    System.out.println(sieger + " hat gewonnen:");
                    mPrint.printTheseLaender(relevanteLaender);

                    break;
                }
            } catch (UngueltigeBewegungException | FalscherBesitzerException | UngueltigeAuswahlException |
                     EinheitenAnzahlException e) {
                System.out.println("Fehler: " + e.getMessage());
                System.out.println("Noch einmal: \n");
            }
            break;
        }
        return ergebnis;
    }

    public boolean weiterSpielen() {
        int auswahl = mEingabe.inGameMenue();
        while(true){
            switch (auswahl) {
                case 1:
                    return true;
                case 2:
                    return false;
            }
        }
    }
}
