package valueobjects;

import exceptions.UngueltigeAuswahlException;
import persistence.NeuesSpielEinlesen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Welt implements Serializable {
    //private & getter? Jo
    //Todo change auf private und erstelle getter
    private static final long serialVersionUID = 1L;
    private ArrayList<Land> alleLaender;
    public ArrayList<Kontinent> alleKontinente;
    private ArrayList<Spieler> spielerListe;

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
    }

    public void verteileLaender(List<Spieler> spielerListe) {  //Vielleicht eher in domain.Spiel? Ist so glaube ich auch auf deiner Zeichnung wenn ich das richtig sehe :) JA

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