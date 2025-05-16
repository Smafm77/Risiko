import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Spiel {

    public enum Spielphase {
        VERTEILEN, ANGRIFF, VERSCHIEBEN;
    }

    Welt welt = new Welt();
    ArrayList<Spieler> spielerListe = new ArrayList<>();
    HashSet<Karte> kartenStapel = new HashSet<>();
    Menue menue = new Menue();

    public Spiel() throws IOException {
        starteSpiel();
    }

    public void starteSpiel() {
        //Create Players
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bitte die Anzahl an Spielern eingeben:");
        int anzahlSpieler = scanner.nextInt();
        scanner.nextLine(); //Weil scanner.nextInt immer mucken macht einfach nochmal nextLine "einlesen"
        for (int i = 1; i <= anzahlSpieler; i++) {
            System.out.println("Bitte Namen des Spielers eingeben Spieler Nr. " + i + " :");
            Spieler spieler = new Spieler(scanner.nextLine(), i);
            spielerListe.add(spieler);
        }

        //Create board
        welt.printWorldMap();
        welt.verteileLaender(spielerListe);
        kartenStapel.addAll(welt.createCardStack());
        zeigeAlleSpieler(spielerListe);
        do {
            spielRunde();
        } while (spielRunde());
    }

    public boolean spielRunde() {

        for (Spieler spieler : spielerListe) {
            menue.setSpieler(spieler);

            if (!spieler.isAlive()) {
                continue;
            }
            //Truppen erhalten
            spieler.neueArmee(welt.alleKontinente);
            if (!spieler.getKarten().isEmpty()) {
                peruseCards(spieler);
            }
            boolean amZug = true;
            boolean schonErobert = false;
            while (amZug) {
                switch (menue.eingabeEinlesen()) {
                    case ANGRIFF:
                        boolean ergebnis = kampfInterface(spieler);
                        if (ergebnis && !schonErobert) {
                            zieheKarte(spieler);
                            schonErobert = true;
                        }
                        break;
                    case BEWEGEN:
                        moveTroopsInterface(spieler);
                        break;
                    case INFO:
                        infoAuswahl();
                        break;
                    case UEBERSICHT:
                        welt.printTheseLaender(spieler.getBesetzteLaender());
                        break;
                    case ZUGBEENDEN:
                        amZug = false;
                        break;
                    case SPIELBEENDEN:
                        return false;
                    default:
                        System.out.println("Fehlerhafte Eingabe");
                }
            }
        }
        return true;
    }

    public void infoAuswahl() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Über welches Land möchtest du Informationen erhalten? - Abbrechen mit Enter");
        String eingabe = scanner.nextLine();
        if (!Objects.equals(eingabe, "")) {
            Land auswahlLand = welt.findeLand(eingabe);
            if (auswahlLand == null) {
                System.out.println("Fehlerhafte Eingabe: " + eingabe + " ist kein Land.");
                infoAuswahl();
                return;
            }

            switch (menue.infoAbfrage()) {
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

    //region playing Cards
    public void zieheKarte(Spieler spieler) throws NoSuchElementException {//ToDo catch exception from orElseThrow @Maj: Nein
        Optional<Karte> optionalCard = kartenStapel.stream().findFirst();
        Karte karte = optionalCard.orElseThrow();
        spieler.getKarten().add(karte);
    }

    public void spieleKarte(Spieler spieler, Karte karte) {
        if (spieler.getKarten().contains(karte)) {
            spieler.getKarten().remove(karte);
            spieler.zuweisungEinheiten(karte.getStrength());
            kartenStapel.add(karte);
        }
    }
    //endregion

    //region kampf
    public boolean kampfInterface(Spieler angreifer) {
        Scanner scanner = new Scanner(System.in);
        HashSet<Land> volleKasernen = angreifer.getBesetzteLaender().stream().filter(land -> land.getEinheiten() > 1).collect(Collectors.toCollection(HashSet::new));

        while (!volleKasernen.isEmpty()) {
            welt.printTheseLaender(volleKasernen);
            ArrayList<Land> relevanteLaender = new ArrayList<>();

            System.out.println("Aus welchem Land willst du angreifen?");
            Land herkunft = welt.findeLand(scanner.nextLine());
            if (!volleKasernen.contains(herkunft)) {
                System.out.println("Das Land " + herkunft.getName() + "gehört dir nicht und oder hat zu wenig stationierte Soldaten, kann somit nicht als Angriffsbasis genutzt werden");
                break;
            }
            relevanteLaender.add(herkunft);

            System.out.println();
            welt.printTheseLaenderNamen(herkunft.getFeindlicheNachbarn());
            System.out.println("Welches Land möchtest du angreifen?");
            Land ziel = welt.findeLand(scanner.nextLine());
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
            boolean ergebnis = kampf(herkunft, ziel, truppenA, truppenV);
            String sieger = ergebnis ? angreifer.getName() : verteidiger.getName();
            System.out.println(sieger + " hat gewonnen:");
            welt.printTheseLaender(relevanteLaender);

            return ergebnis;
        }
        return false;
    }

    private boolean kampf(Land herkunft, Land ziel, int truppenA, int truppenV) {
        int ueberlebende = schlacht(herkunft, ziel, truppenA, truppenV);
        if (ueberlebende == -1) {
            return false;
        } //Verteidiger hat gewonnen
        erobern(herkunft, ziel, truppenA);
        return true;
    }

    private int schlacht(Land herkunft, Land ziel, int truppenA, int truppenV) {
        Integer[] angriff = new Integer[truppenA];
        Integer[] verteidigung = new Integer[truppenV];
        for (int i = 0; i < angriff.length; i++) {
            angriff[i] = rolleWuerfel();
        }
        for (int i = 0; i < verteidigung.length; i++) {
            verteidigung[i] = rolleWuerfel();
        }
        Arrays.sort(angriff, Collections.reverseOrder());
        Arrays.sort(verteidigung, Collections.reverseOrder());

        for (int i = 0; i < Math.min(truppenA, truppenV); i++) {
            if (angriff[i] > verteidigung[i]) {
                ziel.einheitGestorben();
            } else {
                herkunft.einheitGestorben();
            }
        }

        return ziel.getEinheiten() > 0 ? -1 : truppenA;
    }

    public void erobern(Land herkunft, Land ziel, int besatzer) {
        Spieler verteidiger = ziel.getBesitzer();
        ziel.wechselBesitzer(herkunft.getBesitzer());
        herkunft.getBesitzer().bewegeEinheiten(besatzer, herkunft, ziel);
        welt.findeKontinentenzugehoerigkeit(ziel).checkBesitzer();

        if (verteidiger.getBesetzteLaender().isEmpty()) {
            verteidiger.sterben(herkunft.getBesitzer());
        }
    }
    //endregion

    public int rolleWuerfel() {
        return (int) (Math.random() * 6) + 1;
    }

    //region temporary Visualisation
    public void peruseCards(Spieler spieler) throws NoSuchElementException { //ToDO catch exception  (@maj nein, kein ToDo exceptions bisher!)
        //Name Options
        Scanner scanner = new Scanner(System.in);
        while (!spieler.getKarten().isEmpty()) {
            welt.printTheseLaender(spieler.getBesetzteLaender());
            System.out.println();
            System.out.println("Karten:");
            System.out.println(spieler.eigeneKartenToString());
            System.out.println("Choose any card you want to play by Name");
            System.out.println("N to leave");

            String input = scanner.next();
            if (input.equals("N")) {
                break;
            }
            Optional<Karte> optChosenCard = spieler.getKarten().stream().filter(c -> c.getLand().getName().equalsIgnoreCase(input.trim())).findFirst(); //finds the chosen Card by it's name and throws an Error if it doesn't exist
            if (optChosenCard.isPresent()) {
                Karte chosenCard = optChosenCard.orElseThrow();
                spieleKarte(spieler, chosenCard);
            }
        }
    }

    public void moveTroopsInterface(Spieler spieler) {
        Scanner scanner = new Scanner(System.in);
        zeigeEigeneGebiete(spieler);
        System.out.println("Aus welchem Land sollen Einheiten entzogen werden?");
        String herkunft = scanner.nextLine();
        Land herLand = welt.findeLand(herkunft);
        while (true) {
            if (herLand.getBesitzer() != spieler) {
                System.out.println("Du bist nicht der Besitzer von " + herLand.getName());
                break;
            }
            System.out.println("In welches Land sollen die Einheiten geschickt werden?");
            String ziel = scanner.nextLine();
            Land zielLand = welt.findeLand(ziel);
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


    public void zeigeEigeneGebiete(Spieler spieler) {
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
//endregion

}