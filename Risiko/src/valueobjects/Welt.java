package valueobjects;

import exceptions.UngueltigeAuswahlException;
import missionen.Mission;
import persistence.NeuesSpielEinlesen;

import java.io.*;
import java.util.*;

public class Welt implements Serializable {
    //private & getter? Jo
    //Todo change auf private und erstelle getter
    @Serial
    private static final long serialVersionUID = 1L;
    private ArrayList<Land> alleLaender;
    public ArrayList<Kontinent> alleKontinente;
    public ArrayList<Spieler> spielerListe;
    private HashSet<Mission> moeglicheMissionen;

    public ArrayList<Land> getAlleLaender() {
        return alleLaender;
    }

    public ArrayList<Spieler> getSpielerListe() {
        return spielerListe;
    }


    public Welt(ArrayList<Spieler> spieler) throws IOException {
        NeuesSpielEinlesen spielmaterial = new NeuesSpielEinlesen();
        alleLaender = spielmaterial.alleLaenderEinlesen();
        alleKontinente = spielmaterial.alleKontinenteEinlesen(alleLaender);
        spielerListe = spieler;
        moeglicheMissionen = spielmaterial.missionenErstellen(alleKontinente);
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
            aktuellerSpieler.fuegeLandHinzu(aktuellesLand);

            index++;
        }
    }

    public void weiseMissionenZu(){
        for (Spieler spieler : spielerListe){
            Mission mission = moeglicheMissionen.stream().findFirst().orElseThrow();
            moeglicheMissionen.remove(mission);
            spieler.teileMissionZu(mission);
        }
    }

    public Land findeLand(String name) throws UngueltigeAuswahlException {
        String suche = name.trim().toLowerCase();

        for (Land land : alleLaender) {
            if (land.getName().toLowerCase().equals(suche)) {
                return land;
            }

        }
        throw new UngueltigeAuswahlException("Land " + name + " existiert nicht.");
    }

    public Kontinent findeKontinentenzugehoerigkeit(Land land) throws NullPointerException {
        for (Kontinent kontinent : alleKontinente) {
            if (kontinent.beinhaltetLand(land)) {
                return kontinent;
            }
        }
        return null; //sollte nie vorkommen
    }

}