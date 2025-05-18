package ui.cui;

import domain.Spiel;
import enums.Befehl;
import enums.Infos;
import exceptions.EinheitenAnzahlException;
import exceptions.FalscherBesitzerException;
import exceptions.UngueltigeAuswahlException;
import exceptions.UngueltigeBewegungException;
import valueobjects.*;

import java.util.*;
import java.util.stream.Collectors;

public class Menue {
    Scanner scanner = new Scanner(System.in);
    private Spieler aktuellerSpieler;
    private Spiel spiel;


    public void setSpieler(Spieler spieler) {
        this.aktuellerSpieler = spieler;
    }

    public void setSpiel(Spiel spiel) {
        this.spiel = spiel;
    }

    public void spielerAbfrage(ArrayList<Spieler> spielerListe) throws UngueltigeAuswahlException {
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
                for (int i = 1; i <= anzahlSpieler; i++) {
                    while (true) {
                        try {
                            System.out.println("Bitte Namen des Spielers eingeben Spieler Nr. " + i + " :");
                            String name = scanner.nextLine().trim();
                            if (name.isEmpty()) {
                                throw new UngueltigeAuswahlException("Spielername darf nicht leer sein!");
                            }
                            Spieler spieler = new Spieler(name, i);
                            spiel.getWelt().getSpielerListe().add(spieler);
                            break;
                        } catch (UngueltigeAuswahlException e) {
                            System.out.println("Fehler: " + e.getMessage());
                            System.out.println("Nocheinmal: \n");
                        }
                    }
                }
                break;
            } catch (UngueltigeAuswahlException e) {
                System.out.println("Fehler: " + e.getMessage());
                System.out.println("Nocheinmal: \n");
            }
        }
    }

    //region ausgabe
    public Befehl hauptAbfrage() throws UngueltigeAuswahlException {
        int auswahl;
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
        return Befehl.fromInt(auswahl);
    }

    public boolean hauptMenue(Welt welt, Spieler spieler) throws UngueltigeAuswahlException, FalscherBesitzerException, UngueltigeBewegungException {
        System.out.println("Du bist am Zug : " + aktuellerSpieler.getName());
        System.out.println("Was willst du tun? ");
        System.out.println("1: Angreifen");
        System.out.println("2: Truppen bewegen");
        System.out.println("3: Infos über...");
        System.out.println("4: Übersicht meiner Gebiete");
        System.out.println("5: Karte nutzen");
        System.out.println("6: Zug beenden");
        while (true) {
            try {
                return hauptAuswahl(hauptAbfrage(), welt, spieler);
            } catch (UngueltigeAuswahlException e) {
                System.out.println("Fehler: " + e.getMessage());
                System.out.println("Nocheinmal: \n");
            }
        }
    }

    public Infos infoAbfrage(Land auswahlLand) throws UngueltigeAuswahlException {
        infoMenue(auswahlLand);
        int auswahl;
        try {
            auswahl = scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.nextLine();
            throw new UngueltigeAuswahlException("Eingabe muss eine Zahl sein!");
        }
        scanner.nextLine();
        if (auswahl < 1 || auswahl > 4) {
            throw new UngueltigeAuswahlException("Bitte wähle eine Option von 1-4.");
        }
        return Infos.fromInt(auswahl);
    }

    private void infoMenue(Land auswahlLand) {
        System.out.println("Welche Informationen möchtest du über " + auswahlLand.getName() + " erhalten?");
        System.out.println("1: Besitzer");
        System.out.println("2: Einheiten auf Land");
        System.out.println("3: Nachbarländer von " + auswahlLand.getName());
        System.out.println("4: Zurück");
    }

    //endregion ausgabe
//region logik
    public void infoAuswahl(Welt welt) throws UngueltigeAuswahlException {
        Land auswahlLand;
        String eingabe;
        while (true) {
            System.out.println("Über welches Land möchtest du Informationen erhalten?");
            try {
                eingabe = scanner.nextLine();
                if (eingabe.isEmpty()) {
                    throw new UngueltigeAuswahlException("Land darf nicht leer sein!");
                }
                auswahlLand = welt.findeLand(eingabe);
                if (auswahlLand == null) {
                    throw new UngueltigeAuswahlException("Das Land " + eingabe + " existiert nicht.");
                }

            } catch (UngueltigeAuswahlException e) {
                System.out.println("Fehler: " + e.getMessage());
                System.out.println("Nocheinmal: \n");
                continue;
            }

            boolean infoFertig = false;
            while (!infoFertig) {
                switch (infoAbfrage(auswahlLand)) {
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
                        infoFertig = true;
                }
            }
        break;
        }
    }

    public boolean hauptAuswahl(Befehl auswahl, Welt welt, Spieler spieler) throws UngueltigeAuswahlException, FalscherBesitzerException, UngueltigeBewegungException {

        switch (auswahl) {
            case ANGRIFF:
                boolean ergebnis = kampfInterface(spieler);
                if (ergebnis && !spieler.getSchonErobert()) {
                    spiel.zieheKarte(spieler);
                    spieler.setSchonErobert(true);
                }
                break;
            case BEWEGEN:
                moveTroopsInterface(spieler);
                break;
            case INFO:
                infoAuswahl(welt);
                break;
            case UEBERSICHT:
                printTheseLaender(spieler.getBesetzteLaender());
                break;
            case KARTE:
                peruseCards(spieler);
                break;
            case ZUGBEENDEN:
                return false;
        }
        return true;
    }

    public boolean kampfInterface(Spieler angreifer) throws UngueltigeBewegungException {
        Scanner scanner = new Scanner(System.in);
        HashSet<Land> volleKasernen = angreifer.getBesetzteLaender().stream().filter(land -> land.getEinheiten() > 1).collect(Collectors.toCollection(HashSet::new));
        boolean ergebnis = false;
        Land herkunft;
        Land ziel;
        String name;
        while (!volleKasernen.isEmpty()) {
            printTheseLaender(volleKasernen);
            ArrayList<Land> relevanteLaender = new ArrayList<>();
            System.out.println("Aus welchem Land willst du angreifen?");
            while (true) {

                try {
                    name = scanner.nextLine();
                    if (name.isEmpty()) {
                        throw new UngueltigeAuswahlException("Das Land darf nicht leer sein!");
                    }
                    herkunft = spiel.getWelt().findeLand(name);
                    if (!volleKasernen.contains(herkunft)) {
                        throw new FalscherBesitzerException("Dieses Land gehört dir nicht!");
                    }
                    relevanteLaender.add(herkunft);
                    System.out.println();
                    printTheseLaenderNamen(herkunft.getFeindlicheNachbarn());
                    System.out.println("Welches Land möchtest du angreifen?");
                    while (true) {
                        try {
                            name = scanner.nextLine();
                            if (name.isEmpty()) {
                                throw new UngueltigeAuswahlException("Das Land darf nicht leer sein!");
                            }
                            ziel = spiel.getWelt().findeLand(name);
                            if (volleKasernen.contains(ziel)) {
                                throw new FalscherBesitzerException("Dieses Land gehört dir!");
                            } else if (!herkunft.getFeindlicheNachbarn().contains(ziel)) {
                                throw new UngueltigeBewegungException("Du kannst nicht von " + herkunft + " aus " + ziel + " angreifen.");
                            }
                        } catch (UngueltigeAuswahlException | UngueltigeBewegungException e) {
                            System.out.println("Fehler: " + e.getMessage());
                            System.out.println("Nocheinmal: \n");
                        }
                        ziel = spiel.getWelt().findeLand(name);
                        break;
                    }

                    relevanteLaender.add(ziel);
                    Spieler verteidiger = ziel.getBesitzer();

                    System.out.println();
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
                            truppenV = scanner.nextInt();
                        } catch (InputMismatchException e) {
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
                    }
                    System.out.println();
                    ergebnis = spiel.kampf(herkunft, ziel, truppenA, truppenV);
                    String sieger = ergebnis ? angreifer.getName() : verteidiger.getName();
                    System.out.println(sieger + " hat gewonnen:");
                    printTheseLaender(relevanteLaender);
                } catch (FalscherBesitzerException | UngueltigeAuswahlException e) {
                    System.out.println("Fehler: " + e.getMessage());
                    System.out.println("Nocheinmal: \n");
                }
                break;
            }
        }
        return ergebnis;
    }

    public void moveTroopsInterface(Spieler spieler) throws
            UngueltigeAuswahlException, FalscherBesitzerException {

        zeigeEigeneGebiete(spieler);
        System.out.println("Aus welchem Land sollen Einheiten entzogen werden?");
        String name;
        Land herkunft;
        Land ziel;
        while (true) {
            try {
                name = scanner.nextLine();
                if (name.isEmpty()) {
                    throw new UngueltigeAuswahlException("Das Land darf nicht leer sein!");
                }

                herkunft = spiel.getWelt().findeLand(name);
                if (herkunft.getBesitzer() != spieler) {
                    throw new FalscherBesitzerException("Dieses Land gehört dir nicht!");
                }
                while (true) {
                    System.out.println("In welches Land sollen die Einheiten geschickt werden?");
                    name = scanner.nextLine();

                    if (name.isEmpty()) {
                        throw new UngueltigeAuswahlException("Das Land darf nicht leer sein!");
                    }
                    ziel = spiel.getWelt().findeLand(name);
                    if (ziel.getBesitzer() != spieler) {
                        throw new FalscherBesitzerException("Dieses Land gehört dir nicht!");
                    }
                    if (!herkunft.connectionPossible(ziel)) {
                        throw new UngueltigeBewegungException(ziel.getName() + " ist von " + herkunft.getName() + " aus nicht erreichbar.");
                    }
                    break;
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


            } catch (UngueltigeAuswahlException | FalscherBesitzerException | UngueltigeBewegungException e) {
                System.out.println("Fehler: " + e.getMessage());
                System.out.println("Nocheinmal: \n");
            }
        }
    }


    public void zeigeEigeneGebiete(Spieler spieler) {  //UI?
        System.out.println("All deine Gebiete:");
        for (Land land : spieler.getBesetzteLaender()) {
            System.out.print(spieler.getId() + " - " + land.getName() + ": " + land.getEinheiten() + " | ");
            for (Land nachbar : land.getNachbarn()) {
                for (Land besetzt : spieler.getBesetzteLaender()) {
                    if (besetzt == nachbar) {
                        System.out.print(nachbar.getName() + " ");
                    }
                }
            }
            System.out.println();
        }
    }

    public void zeigeAlleSpieler(ArrayList<Spieler> spielerListe) {
        for (Spieler spieler : spielerListe) {
            spieler.zeigeSpieler();
        }
        System.out.println();
        System.out.println();
    }

    public void peruseCards(Spieler spieler) throws NoSuchElementException, UngueltigeAuswahlException {
        if (!spieler.getKarten().isEmpty()) {
            throw new UngueltigeAuswahlException("Du hast keine Karten zum ausspielen.");
        }
        while (!spieler.getKarten().isEmpty()) {
            printTheseLaender(spieler.getBesetzteLaender());
            System.out.println();
            System.out.println("Karten:");
            System.out.println(spieler.eigeneKartenToString());
            System.out.println("Welche Karte willst du ausspielen?");
            System.out.println("Zum Abbrechen wähle N");

            String input = scanner.next();
            if (input.equals("N")) {
                break;
            }
            Optional<Karte> optChosenCard = spieler.getKarten().stream().filter(c -> c.getLand().getName().equalsIgnoreCase(input.trim())).findFirst(); //finds the chosen Card by it's name and throws an Error if it doesn't exist
            if (optChosenCard.isPresent()) {
                Karte chosenCard = optChosenCard.orElseThrow(); //Todo: Wird das denn gefangen?
                spiel.spieleKarte(spieler, chosenCard);
            }
        }
    }

    public void printWorldMap() {
        System.out.println("Weltkarte:");
        System.out.println();
        for (Land land : spiel.getWelt().getAlleLaender()) {
            String nachbarn = land.getNachbarn().stream().map(Land::getName).collect(Collectors.joining(", "));
            System.out.println(land.getName() + " | Angrenzend:" + nachbarn);
        }
        System.out.println();
    }

    public void printTheseLaender(Collection<Land> laender) {
        for (Land land : laender) {
            String fNachbarn = "";
            for (Land fLand : land.getFeindlicheNachbarn()) {
                fNachbarn += " [" + fLand.getName() + " - " + fLand.getBesitzer().getName() + "(" + fLand.getEinheiten() + ")]";
            }
            System.out.println(land.getName() + " ist im Besitz von " + land.getBesitzer().getName() + "(" + land.getEinheiten() + ") und bedroht von " + fNachbarn);
        }
    }

    public void printTheseLaenderNamen(Collection<Land> laender) {
        for (Land land : laender) {
            System.out.println(land.getName() + " ist im Besitz von " + land.getBesitzer().getName() + "(" + land.getEinheiten() + ")");
        }
    }

}

