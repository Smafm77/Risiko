import java.util.*;

public class Spiel {
    ArrayList<Spieler> spielerListe = new ArrayList<>();
    HashSet<Karte> kartenStapel;

    public Spiel() {
        starteSpiel();
    }

    public void starteSpiel() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bitte die Anzahl an Spielern eingeben:");
        int anzahlSpieler = scanner.nextInt();
        scanner.nextLine(); //Weil scanner.nextInt immer mucken macht einfach nochmal nextLine "einlesen"

        for (int i = 1; i <= anzahlSpieler; i++) {
            System.out.println("Bitte Namen des Spielers eingeben Spieler Nr. " + i + " :");
            Spieler spieler = new Spieler(scanner.nextLine(), i);
            spielerListe.add(spieler); //ToDO Spieler unterscheiden (z.B. Farben aus einem Array/Enum zuordnen)
        }
        Welt welt = new Welt();
        welt.verteileLaender(spielerListe);
        kartenStapel = (HashSet<Karte>) welt.createCardStack();

        printPlayers(spielerListe);
    }

    public void spielRunde() {
        for (int j = 1; j <=spielerListe.size() ; j++) {
            spielerListe.get(j).neueArmee();
            //Todo abfrage was getan werden soll
        }
    }

    //region playing Cards
    public void drawCard(Spieler spieler) throws NoSuchElementException {//ToDo catch exception from orElseThrow
        Optional<Karte> optionalCard = kartenStapel.stream().findFirst();
        Karte card = optionalCard.orElseThrow();
        spieler.karten.add(card);
    }

    public void playCard(Spieler spieler, Karte card){
        if (!spieler.karten.contains(card)){
            //Todo Test if player owns card (should not happen because player should only be able to choose from their own already owned cards, but better safe than sorry)
        }
        spieler.karten.remove(card);
        kartenStapel.add(card);
    }
    //endregion

    public int rollDice6(){
        return (int) (Math.random() * 6);
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
