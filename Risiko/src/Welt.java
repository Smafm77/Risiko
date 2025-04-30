import java.util.*;


public class Welt {
    public List<Land> alleLaender;

    public Welt() {
        alleLaender = new ArrayList<>();
        alleLaender.add(new Land(2,"Land1", "Land2"));
        alleLaender.add(new Land(2,"Land2", "Land1", "Land3"));
        alleLaender.add(new Land(2,"Land3", "Land2", "Land4", "Land5"));
        alleLaender.add(new Land(2,"Land4", "Land3"));
        alleLaender.add(new Land(2,"Land5", "Land3"));
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

        while (!alleLaender.isEmpty()) {
            int playerIndex = spielerAnzahl - 1 - (index % spielerAnzahl);

            Land aktuellesLand = alleLaender.removeFirst();

            Spieler aktuellerSpieler = spielerListe.get(playerIndex);
            aktuellesLand.setBesitzer(aktuellerSpieler);
            aktuellerSpieler.fuegeLandHinzu(aktuellesLand, aktuellesLand.strength);

            index++;
        }
    }

}
