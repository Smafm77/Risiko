import java.util.*;
import java.util.stream.Collectors;


public class Welt {
    public ArrayList<Land> alleLaender;

    public Welt() {
        alleLaender = new ArrayList<>();
        alleLaender.add(new Land(2,"Land1"));
        alleLaender.add(new Land(2,"Land2"));
        alleLaender.add(new Land(2,"Land3"));
        alleLaender.add(new Land(2,"Land4"));
        alleLaender.add(new Land(2,"Land5"));
        alleLaender.add(new Land(2,"Land6"));
        alleLaender.add(new Land(2,"Land7"));
        alleLaender.add(new Land(2,"Land8"));
        alleLaender.add(new Land(2,"Land9"));
        alleLaender.add(new Land(2,"Land10"));
        alleLaender.add(new Land(2,"Land11"));
        alleLaender.add(new Land(2,"Land12"));
        alleLaender.add(new Land(2,"Land13"));
        alleLaender.add(new Land(2,"Land14"));
        alleLaender.add(new Land(2,"Land15"));
        alleLaender.add(new Land(2,"Land16"));
        alleLaender.add(new Land(2,"Land17"));
        alleLaender.add(new Land(2,"Land18"));
        alleLaender.add(new Land(2,"Land19"));
        alleLaender.add(new Land(2,"Land20"));
        alleLaender.add(new Land(2,"Land21"));
        alleLaender.add(new Land(2,"Land22"));
        alleLaender.add(new Land(2,"Land23"));
        alleLaender.add(new Land(2,"Land24"));
        alleLaender.add(new Land(2,"Land25"));
        alleLaender.add(new Land(2,"Land26"));
        alleLaender.add(new Land(2,"Land27"));
        alleLaender.add(new Land(2,"Land28"));
        alleLaender.add(new Land(2,"Land29"));
        alleLaender.add(new Land(2,"Land30"));
        alleLaender.add(new Land(2,"Land31"));
        alleLaender.add(new Land(2,"Land32"));
        alleLaender.add(new Land(2,"Land33"));
        alleLaender.add(new Land(2,"Land34"));
        alleLaender.add(new Land(2,"Land35"));
        alleLaender.add(new Land(2,"Land36"));
        alleLaender.add(new Land(2,"Land37"));
        alleLaender.add(new Land(2,"Land38"));
        alleLaender.add(new Land(2,"Land39"));
        alleLaender.add(new Land(2,"Land40"));
        alleLaender.add(new Land(2,"Land41"));
        alleLaender.add(new Land(2,"Land42"));


        //füge in dieses Array die Nachbarn ein
        Land[][] diplomatischeBeziehungen= {
            {alleLaender.get(1)},
            {alleLaender.get(0), alleLaender.get(2)},
            {alleLaender.get(1), alleLaender.get(3), alleLaender.get(4)},
            {alleLaender.get(2)},
            {alleLaender.get(27)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
            {alleLaender.get(0)},
        };


        for (int i = 0; i < diplomatischeBeziehungen.length; i++){
            alleLaender.get(i).addNachbarn(diplomatischeBeziehungen[i]);
        }
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
            aktuellerSpieler.fuegeLandHinzu(aktuellesLand, aktuellesLand.strength);

            index++;
        }
    }

    //region Description
    public void printWorldMap(){
        System.out.println("Worldmap:");
        System.out.println();
        for (Land land : alleLaender){
            String nachbarn = land.nachbarn.stream().map(land1 -> land1.name + ", ").collect(Collectors.joining());
            System.out.println(land.name + " | Next to:" + nachbarn);
        }
        System.out.println();
    }
    //endregion

}
