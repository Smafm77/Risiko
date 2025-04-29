import java.util.Collections;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;


public class Welt {
    public List<Land> alleLaender;

    public Welt() {
        alleLaender = new ArrayList<>();
        alleLaender.add(new Land("Land1", "Land2"));
        alleLaender.add(new Land("Land2", "Land1", "Land3"));
        alleLaender.add(new Land("Land3", "Land2", "Land4", "Land5"));
        alleLaender.add(new Land("Land4", "Land3"));
        alleLaender.add(new Land("Land5", "Land3"));
        alleLaender.add(new Land("Land6"));
        alleLaender.add(new Land("Land7"));
        alleLaender.add(new Land("Land8"));
        alleLaender.add(new Land("Land9"));
        alleLaender.add(new Land("Land10"));
        alleLaender.add(new Land("Land11"));
        alleLaender.add(new Land("Land12"));
        alleLaender.add(new Land("Land13"));
        alleLaender.add(new Land("Land14"));
        alleLaender.add(new Land("Land15"));
        alleLaender.add(new Land("Land16"));
        alleLaender.add(new Land("Land17"));
        alleLaender.add(new Land("Land18"));
        alleLaender.add(new Land("Land19"));
        alleLaender.add(new Land("Land20"));
        alleLaender.add(new Land("Land21"));
        alleLaender.add(new Land("Land22"));
        alleLaender.add(new Land("Land23"));
        alleLaender.add(new Land("Land24"));
        alleLaender.add(new Land("Land25"));
        alleLaender.add(new Land("Land26"));
        alleLaender.add(new Land("Land27"));
        alleLaender.add(new Land("Land28"));
        alleLaender.add(new Land("Land29"));
        alleLaender.add(new Land("Land30"));
        alleLaender.add(new Land("Land31"));
        alleLaender.add(new Land("Land32"));
        alleLaender.add(new Land("Land33"));
        alleLaender.add(new Land("Land34"));
        alleLaender.add(new Land("Land35"));
        alleLaender.add(new Land("Land36"));
        alleLaender.add(new Land("Land37"));
        alleLaender.add(new Land("Land38"));
        alleLaender.add(new Land("Land39"));
        alleLaender.add(new Land("Land40"));
        alleLaender.add(new Land("Land41"));
        alleLaender.add(new Land("Land42"));
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
            aktuellerSpieler.fuegeLandHinzu(aktuellesLand, 2); //ToDO anpassen, dass Einheiten wie im Spiel variieren & updaten von Spieler.einheiten

            index++;
        }
    }

}
