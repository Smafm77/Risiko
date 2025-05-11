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
            spielerListe.add(spieler); //ToDO Spieler unterscheiden (z.B. Farben aus einem Array/Enum zuordnen)
        }

        //Create board
        welt.printWorldMap();
        welt.verteileLaender(spielerListe);
        kartenStapel.addAll(welt.createCardStack());

        printPlayers(spielerListe);
    }

    public void spielRunde() {
        for (Spieler spieler : spielerListe) {
            //Truppen erhalten
            spieler.neueArmee(welt.alleKontinente);
            if (!spieler.getKarten().isEmpty()){
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
        spieler.getKarten().add(card);
    }

    public void playCard(Spieler spieler, Karte card){
        if (!spieler.getKarten().contains(card)){
            spieler.getKarten().remove(card);
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

        while (!spieler.getKarten().isEmpty()){
            String input = scanner.next();
            if (input.equals("N")){break;}
            Karte chosenCard = spieler.getKarten().stream().filter(c -> c.land.getName().equals(input.trim())).findFirst().orElseThrow(); //finds the chosen Card by it's name and throws an Error if it doesn't exist
            playCard(spieler, chosenCard);
        }
    }
    public void moveTroopsInterface(Spieler spieler){
        Scanner scanner = new Scanner(System.in);

        while (true){
            printYourTerretorries(spieler);
            System.out.println("Do you wish to continue? (Y/N)");
            char cont = scanner.next().trim().toUpperCase().charAt(0);

            //Todo check if break works as intended & catch Errors
            if (cont == 'N'){
                break;
            } else if (cont == 'Y') {
                System.out.println("From where?");
                Land her = spieler.getBesetzteLaender().stream().filter(land -> land.isName(scanner.next())).findFirst().orElseThrow(); //catch orElseThrow
                System.out.println("To where?");
                Land ziel = spieler.getBesetzteLaender().stream().filter(land -> land.isName(scanner.next())).findFirst().orElseThrow(); //catch orElseThrow
                System.out.println("How many?");
                int troops = scanner.nextInt();
                einheitenBewegen(spieler, her, ziel, troops);
            } else {
                System.out.println("Ungültige Eingabe");
            }
        }
    }


    public void printYourTerretorries(Spieler spieler){
        System.out.println("All deine Gebiete:");
        //Wenn es möglich ist Nachbarn zu erörtern auch diese hinzufügen (anzahl angrenzender Gebiete und Einheiten)
        for (Land land : spieler.getBesetzteLaender()){
            System.out.println(spieler.getId() + " - " + land.getName() + ": " + land.getEinheiten() + " | ");
        }
    }

    public void printPlayers(ArrayList<Spieler> spielerListe) {
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
