import java.util.*;

public class Spiel {
    ArrayList<Spieler> spielerListe = new ArrayList<>();
    HashSet<Karte> kartenStapel = new HashSet<>();

    public Spiel() {
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
            spielerListe.add(spieler); //ToDO Spieler unterscheiden (z.B. Farben aus einem Array/Enum zuordnen)
        }

        //Create board
        Welt welt = new Welt();
        welt.printWorldMap();
        welt.verteileLaender(spielerListe);
        kartenStapel.addAll(welt.createCardStack());

        printPlayers(spielerListe);
    }

    public void spielRunde() {
        for (Spieler spieler : spielerListe) {
            //Truppen erhalten
            spieler.neueArmee();
            if (!spieler.karten.isEmpty()){
                peruseCards(spieler);
            }
            //Optional: Kampf
            //Ich würde Karte ziehen schon hier mit einbinden

            //Optional: Truppen bewegen

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
            spieler.karten.remove(card);
            kartenStapel.add(card);
        } else {
            //ToDo throw Error that Player doesn't own the card. This should not happen because player should only be able to choose from their own already owned cards, but better safe than sorry
        }
    }
    //endregion

    public int rollDice6(){
        return (int) (Math.random() * 6);
    }

    //region temporary Visualisation
    public void peruseCards(Spieler spieler) throws NoSuchElementException{ //ToDO catch exception
        //Name Options
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose any card you want to play by Name");
        printYourTerretorries(spieler);
        System.out.println("N to leave");

        while (!spieler.karten.isEmpty()){
            String input = scanner.next();
            if (input.equals("N")){break;}
            Karte chosenCard = spieler.karten.stream().filter(c -> c.land.name.equals(input.trim())).findFirst().orElseThrow(); //finds the chosen Card by it's name and throws an Error if it doesn't exist
            playCard(spieler, chosenCard);
        }
    }


    public void printYourTerretorries(Spieler spieler){
        System.out.println("All deine Gebiete:");
        //Wenn es möglich ist Nachbarn zu erörtern auch diese hinzufügen (anzahl angrenzender Gebiete und Einheiten)
        for (Land land : spieler.besetzteLaender){
            System.out.println(spieler.id + " - " + land.name + ": " + land.einheiten + " | ");
        }
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
    //endregion

}
