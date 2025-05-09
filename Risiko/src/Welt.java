import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class Welt {
    public ArrayList<Land> alleLaender;
    public ArrayList<Kontinent> alleKontinente;


    public Welt() throws IOException {
        alleLaender = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader("Staatenliste.txt"));
        String input;
        while((input = br.readLine()) != null){
            input = input.trim();
            if(!input.isEmpty()){
                alleLaender.add(new Land(2, input));
            }
        }


        //ToDO Nachbarn.txt einlesen

        //Todo Kontinente erstellen & Länder zuweisen mit alleKontinente.add(new Kontinent(...))
    }

    public Collection<Karte> createCardStack(){
        Collection<Karte> stapel = new ArrayList<>();
        for (Land land : alleLaender){
            stapel.add(new Karte(land));
        }
        return stapel;
    }


    public void verteileLaender(List<Spieler> spielerListe) {

        Collections.shuffle(alleLaender);

        int spielerAnzahl = spielerListe.size();
        int index = 0;

        while (index < alleLaender.size()) {
            int playerIndex = spielerAnzahl - 1 - (index % spielerAnzahl);

            Land aktuellesLand = alleLaender.get(index);

            Spieler aktuellerSpieler = spielerListe.get(playerIndex);
            aktuellesLand.setBesitzer(aktuellerSpieler);
            aktuellerSpieler.fuegeLandHinzu(aktuellesLand, aktuellesLand.getStrength());

            index++;
        }
    }

    //region temporary Visualisation
    public void printWorldMap(){
        System.out.println("Weltkarte:");
        System.out.println();
        for (Land land : alleLaender){
            String nachbarn = land.getNachbarn().stream().map(land1 -> land1.getNachbarn() + ", ").collect(Collectors.joining());
            System.out.println(land.getName() + " | Angrenzend:" + nachbarn);
        }
        System.out.println();
    }
    //endregion

}
