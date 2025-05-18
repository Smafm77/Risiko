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
    private Land auswahlLand;
    private Spiel spiel;


    public void setSpieler(Spieler spieler) {
        this.aktuellerSpieler = spieler;
    }


    public void spielerAbfrage() throws UngueltigeAuswahlException {
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
        if (auswahl < 1 || auswahl > 5) {
            throw new UngueltigeAuswahlException("Bitte wähle eine Option von 1-5.");
        }
        return Befehl.fromInt(auswahl);
    }

    public boolean hauptMenue(Welt welt, Spieler spieler) throws UngueltigeAuswahlException {
        System.out.println("Du bist am Zug : " + aktuellerSpieler.getName());
        System.out.println("Was willst du tun? ");
        System.out.println("1: Angreifen");
        System.out.println("2: Truppen bewegen");
        System.out.println("3: Infos über...");
        System.out.println("4: Übersicht meiner Gebiete");
        System.out.println("5: Zug beenden");
        while (true) {
            try {
                return hauptAuswahl(hauptAbfrage(), welt, spieler);
            } catch (UngueltigeAuswahlException e) {
                System.out.println("Fehler: " + e.getMessage());
                System.out.println("Nocheinmal: \n");
            }
        }
    }

    public Infos infoAbfrage() throws UngueltigeAuswahlException {
        infoMenue();
        int auswahl;
        try {
            auswahl = scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.nextInt();
            throw new UngueltigeAuswahlException("Eingabe muss eine Zahl sein!");
        }
        scanner.nextLine();
        if (auswahl < 1 || auswahl > 4) {
            throw new UngueltigeAuswahlException("Bitte wähle eine Option von 1-4.");
        }
        scanner.nextLine();
        return Infos.fromInt(auswahl);
    }

    private void infoMenue() {
        System.out.println("Welche Informationen möchtest du über " + auswahlLand + " erhalten?");
        System.out.println("1: Besitzer");
        System.out.println("2: Einheiten auf Land");
        System.out.println("3: Nachbarländer von " + auswahlLand);
        System.out.println("4: Zurück");
    }

    //endregion ausgabe
//region logik
    public void infoAuswahl(Welt welt) throws UngueltigeAuswahlException { //In menue
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Über welches Land möchtest du Informationen erhalten? - Abbrechen mit Enter");
            String eingabe;
            eingabe = scanner.nextLine().trim();
            if (eingabe.isEmpty()) {
                throw new UngueltigeAuswahlException("Land darf nicht leer sein!");
            }
            try {
                Land auswahlLand = welt.findeLand(eingabe);
                if (auswahlLand == null) {
                    throw new UngueltigeAuswahlException("Das Land " + eingabe + " existiert nicht.");
                }

                switch (infoAbfrage()) {
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
                        return;
                }
                return;
            } catch (UngueltigeAuswahlException e) {
                System.out.println("Fehler: " + e.getMessage());
                System.out.println("Nocheinmal: \n");
            }
        }
    }

    public boolean hauptAuswahl(Befehl auswahl, Welt welt, Spieler spieler) throws UngueltigeAuswahlException {

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
            case ZUGBEENDEN:
                return false;
        }
        return true;
    }

    public boolean kampfInterface(Spieler angreifer) throws UngueltigeBewegungException { //Interface = ins ui.cui.Menue? oder UI? oder beides?
        Scanner scanner = new Scanner(System.in);
        HashSet<Land> volleKasernen = angreifer.getBesetzteLaender().stream().filter(land -> land.getEinheiten() > 1).collect(Collectors.toCollection(HashSet::new));

        while (!volleKasernen.isEmpty()) {
            printTheseLaender(volleKasernen);
            ArrayList<Land> relevanteLaender = new ArrayList<>();

            System.out.println("Aus welchem Land willst du angreifen?");
            while (true) {
                Land herkunft;
                String name;
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
                    Land ziel = spiel.getWelt().findeLand(scanner.nextLine());
                    if (volleKasernen.contains(herkunft)) {
                        throw new FalscherBesitzerException("Dieses Land gehört dir!");
                    } else if (!herkunft.getFeindlicheNachbarn().contains(ziel)) {
                        throw new UngueltigeBewegungException("Du kannst nicht von " + herkunft + " aus " + ziel + " angreifen.");
                    }

                    relevanteLaender.add(ziel);
                    Spieler verteidiger = ziel.getBesitzer();

                    System.out.println();
                    System.out.println("Mit wie vielen Truppen möchtest du angreifen (1 - " + Math.min((herkunft.getEinheiten() - 1), 3) + ")?");
                    int truppenA;
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

                    System.out.println();
                    System.out.println(verteidiger.getName() + " mit wie vielen Truppen möchtest du dich verteidigen (1 - " + Math.min((ziel.getEinheiten()), 2) + ")?");
                    int truppenV;
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

                    System.out.println();
                    boolean ergebnis = spiel.kampf(herkunft, ziel, truppenA, truppenV);
                    String sieger = ergebnis ? angreifer.getName() : verteidiger.getName();
                    System.out.println(sieger + " hat gewonnen:");
                    spiel.getWelt().printTheseLaender(relevanteLaender);

                    return ergebnis;
                } catch(FalscherBesitzerException | UngueltigeAuswahlException e){
                    System.out.println("Fehler: " + e.getMessage());
                    System.out.println("Nocheinmal: \n");
                }
                return false;
            } catch(FalscherBesitzerException e){
                throw new RuntimeException(e);
            }
        }

        public void moveTroopsInterface (Spieler spieler){

            zeigeEigeneGebiete(spieler);
            System.out.println("Aus welchem valueobjects.Land sollen Einheiten entzogen werden?");
            String herkunft = scanner.nextLine();
            Land herLand = spiel.getWelt().findeLand(herkunft);
            while (true) {
                if (herLand.getBesitzer() != spieler) {
                    System.out.println("Du bist nicht der Besitzer von " + herLand.getName());
                    break;
                }
                System.out.println("In welches valueobjects.Land sollen die Einheiten geschickt werden?");
                String ziel = scanner.nextLine();
                Land zielLand = spiel.getWelt().findeLand(ziel);
                if (zielLand.getBesitzer() != spieler) {
                    System.out.println("Du bist nicht der Besitzer von " + zielLand.getName());
                    break;
                }
                boolean istNachbar = false;
                for (Land land : zielLand.getNachbarn()) {
                    if (land == herLand) {
                        istNachbar = true;
                        break;
                    }
                }
                if (!istNachbar) {
                    System.out.println(herLand.getName() + " ist kein Nachbar von " + zielLand.getName());
                    break;
                }

                System.out.println("Wie viele der " + herLand.getEinheiten() + " Einheiten aus " + herLand.getName() + " sollen nach " + zielLand.getName() + " entsendet werden?");
                int anzahl = scanner.nextInt();
                if (herLand.getEinheiten() - anzahl < 1) {
                    System.out.println("Du hast nicht genug Einheiten in " + herLand.getName() + " für diese Aktion. Es muss immer mindestens 1 Einheit im Herkunftsland verbleiben.");
                    break;
                }
                spieler.bewegeEinheiten(anzahl, herLand, zielLand);
                System.out.println("Neue Anzahl Einheiten: " + herLand.getName() + ": " + herLand.getEinheiten() + " , " + zielLand.getName() + ": " + zielLand.getEinheiten());
                break;
            }
        }

        public void zeigeEigeneGebiete (Spieler spieler){  //UI?
            System.out.println("All deine Gebiete:");
            //Wenn es möglich ist Nachbarn zu erörtern auch diese hinzufügen (anzahl angrenzender Gebiete und Einheiten)
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

        public void zeigeAlleSpieler (ArrayList < Spieler > spielerListe) {
            for (Spieler spieler : spielerListe) {
                spieler.zeigeSpieler();
            }
            System.out.println();
            System.out.println();
        }

        public void peruseCards (Spieler spieler) throws NoSuchElementException { //ToDO catch exception
            //Name Options
            Scanner scanner = new Scanner(System.in);
            while (!spieler.getKarten().isEmpty()) {
                spiel.getWelt().printTheseLaender(spieler.getBesetzteLaender());
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
                    Karte chosenCard = optChosenCard.orElseThrow();
                    spiel.spieleKarte(spieler, chosenCard);
                }
            }
        }

        public void printWorldMap () {
            System.out.println("Weltkarte:");
            System.out.println();
            for (Land land : welt.getAlleLaender()) {
                String nachbarn = land.getNachbarn().stream().map(Land::getName).collect(Collectors.joining(", "));
                System.out.println(land.getName() + " | Angrenzend:" + nachbarn);
            }
            System.out.println();
        }

        public void printTheseLaender (Collection < Land > laender) {
            for (Land land : laender) {
                String fNachbarn = "";
                for (Land fLand : land.getFeindlicheNachbarn()) {
                    fNachbarn += " [" + fLand.getName() + " - " + fLand.getBesitzer().getName() + "(" + fLand.getEinheiten() + ")]";
                }
                System.out.println(land.getName() + " ist im Besitz von " + land.getBesitzer().getName() + "(" + land.getEinheiten() + ") und bedroht von " + fNachbarn);
            }
        }

        public void printTheseLaenderNamen (Collection < Land > laender) {
            for (Land land : laender) {
                System.out.println(land.getName() + " ist im Besitz von " + land.getBesitzer().getName() + "(" + land.getEinheiten() + ")");
            }
        }

    }

