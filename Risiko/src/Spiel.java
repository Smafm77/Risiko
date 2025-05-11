import java.awt.*;
import java.io.IOException;
import java.util.*;

public class Spiel {
    Welt welt = new Welt();
    ArrayList<Spieler> spielerListe = new ArrayList<>();
    HashSet<Karte> kartenStapel = new HashSet<>();


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
        zeigeSpieler(spielerListe);
        do {
            spielRunde();
        } while (spielRunde());

    }

    public boolean spielRunde() {
        for (Spieler spieler : spielerListe) {
            //Truppen erhalten
            spieler.neueArmee(welt.alleKontinente);
            if (!spieler.getKarten().isEmpty()) {
                peruseCards(spieler);
            }
            boolean amZug = true;
            while (amZug) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Du bist am Zug :" + spieler.getName());
                System.out.println("Was willst du tun? ");
                System.out.println("1: Karte ziehen");
                System.out.println("2: Angreifen");
                System.out.println("3: Truppen bewegen");
                System.out.println("4: Infos über...");
                System.out.println("5: Zug beenden");
                System.out.println("666: Spiel beenden");
                int auswahl = scanner.nextInt();
                scanner.nextLine();
                switch (auswahl) {
                    case 1:
                        zieheKarte(spieler);
                        break;
                    case 2:
                        //Angreifen
                        break;
                    case 3:
                        moveTroopsInterface(spieler);
                        break;
                    case 4:
                        infoAuswahl();
                        break;
                    case 5:
                        amZug = false;
                        break;
                    case 666:
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
            boolean zurueck = false;
            while (!zurueck) {
                System.out.println("Welche Informationen möchtest du über " + eingabe + " erhalten?");
                System.out.println("1: Besitzer");
                System.out.println("2: Einheiten auf Land");
                System.out.println("3: Nachbarländer von " + eingabe);
                System.out.println("666: Zurück");

                int auswahl = scanner.nextInt();
                scanner.nextLine();

                switch (auswahl) {
                    case 1:
                        System.out.println(auswahlLand.getName() + " befindet sich in " + auswahlLand.getBesitzer().getName() + "'s Besitz.");
                        break;
                    case 2:
                        System.out.println("In " + auswahlLand.getName() + " befinden sich aktuell " + auswahlLand.getEinheiten() + " Einheiten.");
                        break;
                    case 3:
                        System.out.println("Die Nachbarländer von " + auswahlLand.getName() + " sind: ");
                        for (Land nachbar : auswahlLand.getNachbarn()) {
                            System.out.println(nachbar.getName());
                        }
                        break;
                    case 666:
                        zurueck = true;
                        break;
                    default:
                        System.out.println("Fehlerhafte Eingabe.");
                        break;
                }
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
        if (!spieler.getKarten().contains(karte)) {
            spieler.getKarten().remove(karte);
            kartenStapel.add(karte);
        } else {
            //ToDo throw Error that Player doesn't own the karte. This should not happen because player should only be able to choose from their own already owned cards, but better safe than sorry
            //ToDo @majbritt: Nein

        }
    }
    //endregion

    public int rolleWuerfel() {
        return (int) (Math.random() * 6);
    }

    //region temporary Visualisation
    public void peruseCards(Spieler spieler) throws NoSuchElementException { //ToDO catch exception  (@maj nein, kein ToDo exceptions bisher!)
        //Name Options
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose any card you want to play by Name");
        zeigeEigeneGebiete(spieler);
        System.out.println("N to leave");

        while (!spieler.getKarten().isEmpty()) {
            String input = scanner.next();
            if (input.equals("N")) {
                break;
            }
            Karte chosenCard = spieler.getKarten().stream().filter(c -> c.land.getName().equals(input.trim())).findFirst().orElseThrow(); //finds the chosen Card by it's name and throws an Error if it doesn't exist
            spieleKarte(spieler, chosenCard);
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
            if(!istNachbar) {
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

            break;
        }
    }


    public void zeigeEigeneGebiete(Spieler spieler) {
        System.out.println("All deine Gebiete:");
        //Wenn es möglich ist Nachbarn zu erörtern auch diese hinzufügen (anzahl angrenzender Gebiete und Einheiten)
        for (Land land : spieler.getBesetzteLaender()) {
            System.out.print(spieler.getId() + " - " + land.getName() + ": " + land.getEinheiten() + " | ");
            for(Land nachbar : land.getNachbarn()) {
                System.out.print(nachbar.getName() + " ");
            }
            System.out.println();
        }
    }

    public void zeigeSpieler(ArrayList<Spieler> spielerListe) {
        for (Spieler spieler : spielerListe) {
            System.out.println(spieler.getId() + " - " + spieler.getName() + " - " + spieler.getBesetzteLaender().size());
            for (Land land : spieler.getBesetzteLaender()) {
                System.out.println(land.getBesitzer().getId() + ": " + land.getName());
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
    }
//endregion

}
