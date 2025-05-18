package ui.cui;

import domain.Spiel;
import enums.Befehl;
import enums.Infos;
import exceptions.UngueltigeAuswahlException;
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
                            String name = scanner.nextLine();
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
        System.out.println("Über welches Land möchtest du Informationen erhalten? - Abbrechen mit Enter");
        String eingabe = scanner.nextLine();
        if (!Objects.equals(eingabe, "")) {
            Land auswahlLand = welt.findeLand(eingabe);
            if (auswahlLand == null) {
                System.out.println("Fehlerhafte Eingabe: " + eingabe + " ist kein Land.");
                infoAuswahl(welt);
                return;
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
                default:
                    System.out.println("Fehlerhafte Eingabe.");
                    break;
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
                welt.printTheseLaender(spieler.getBesetzteLaender());
                break;
            case ZUGBEENDEN:
                return false;
            default:
                System.out.println("Fehlerhafte Eingabe");
        }
        return true;
    }

    public boolean kampfInterface(Spieler angreifer) { //Interface = ins ui.cui.Menue? oder UI? oder beides?
        Scanner scanner = new Scanner(System.in);
        HashSet<Land> volleKasernen = angreifer.getBesetzteLaender().stream().filter(land -> land.getEinheiten() > 1).collect(Collectors.toCollection(HashSet::new));

        while (!volleKasernen.isEmpty()) {
            spiel.getWelt().printTheseLaender(volleKasernen);
            ArrayList<Land> relevanteLaender = new ArrayList<>();

            System.out.println("Aus welchem valueobjects.Land willst du angreifen?");
            Land herkunft = spiel.getWelt().findeLand(scanner.nextLine());
            if (!volleKasernen.contains(herkunft)) {
                System.out.println("Das valueobjects.Land " + herkunft.getName() + "gehört dir nicht und oder hat zu wenig stationierte Soldaten, kann somit nicht als Angriffsbasis genutzt werden");
                break;
            }
            relevanteLaender.add(herkunft);

            System.out.println();
            spiel.getWelt().printTheseLaenderNamen(herkunft.getFeindlicheNachbarn());
            System.out.println("Welches valueobjects.Land möchtest du angreifen?");
            Land ziel = spiel.getWelt().findeLand(scanner.nextLine());
            if (!herkunft.getFeindlicheNachbarn().contains(ziel)) {
                System.out.println("Das Lander " + ziel.getName() + " ist von " + herkunft.getName() + " aus nicht angreifbar");
                break;
            }
            relevanteLaender.add(ziel);
            Spieler verteidiger = ziel.getBesitzer();

            System.out.println();
            System.out.println("Mit wie vielen Truppen möchtest du angreifen (1 - " + Math.min((herkunft.getEinheiten() - 1), 3) + ")?");
            int truppenA = Integer.parseInt(scanner.nextLine());
            if (truppenA >= herkunft.getEinheiten() || !(truppenA > 0 && truppenA <= 3)) {
                System.out.println(truppenA + "ist eine ungeeignete Anzahl an Truppen in den Agriff zu senden");
                break;
            }

            System.out.println();
            System.out.println(verteidiger.getName() + " mit wie vielen Truppen möchtest du dich verteidigen (1 - " + Math.min((ziel.getEinheiten()), 2) + ")?");
            int truppenV = Integer.parseInt(scanner.nextLine());
            if (truppenV > ziel.getEinheiten() || !(truppenV > 0 && truppenV <= 2)) {
                System.out.println(truppenV + "ist eine ungeeignete Anzahl an Truppen für die Verteidigung zu stationieren.");
                break;
            }

            System.out.println();
            boolean ergebnis = spiel.kampf(herkunft, ziel, truppenA, truppenV);
            String sieger = ergebnis ? angreifer.getName() : verteidiger.getName();
            System.out.println(sieger + " hat gewonnen:");
            spiel.getWelt().printTheseLaender(relevanteLaender);

            return ergebnis;
        }
        return false;
    }

    public void moveTroopsInterface(Spieler spieler) { //Mix aus ui.cui.Menue und UI?
        Scanner scanner = new Scanner(System.in);
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

    public void zeigeEigeneGebiete(Spieler spieler) {  //UI?
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

    public void zeigeAlleSpieler(ArrayList<Spieler> spielerListe) {
        for (Spieler spieler : spielerListe) {
            spieler.zeigeSpieler();
        }
        System.out.println();
        System.out.println();
    }

    public void peruseCards(Spieler spieler) throws NoSuchElementException { //ToDO catch exception
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
}

