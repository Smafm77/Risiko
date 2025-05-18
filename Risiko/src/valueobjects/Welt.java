package valueobjects;

import persistence.NeuesSpielEinlesen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Welt {
    //private & getter? Jo
    //Todo change auf private und erstelle getter
    public ArrayList<Land> alleLaender;
    public ArrayList<Kontinent> alleKontinente;
    public ArrayList<Spieler> spielerListe;

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

    //region temporary Visualisation -- !!UI!!
    public void printWorldMap() {
        System.out.println("Weltkarte:");
        System.out.println();
        for (Land land : alleLaender) {
            String nachbarn = land.getNachbarn().stream().map(Land::getName).collect(Collectors.joining(", "));
            System.out.println(land.getName() + " | Angrenzend:" + nachbarn);
        }
        System.out.println();
    }

    public void printTheseLaender(Collection<Land> laender) {
        for (Land land : laender) {
            String fNachbarn = "";
            for (Land fLand : land.getFeindlicheNachbarn()) {
                fNachbarn += " [" + fLand.getName() + " - " + fLand.getBesitzer().getName() + "(" + fLand.getEinheiten() + ")]";
            }
            System.out.println(land.getName() + " ist im Besitz von " + land.getBesitzer().getName() + "(" + land.getEinheiten() + ") und bedroht von " + fNachbarn);
        }
    }

    public void printTheseLaenderNamen(Collection<Land> laender) {
        for (Land land : laender) {
            System.out.println(land.getName() + " ist im Besitz von " + land.getBesitzer().getName() + "(" + land.getEinheiten() + ")");
        }
    }

    public Land findeLand(String name) {
        String suche = name.trim().toLowerCase();
        for (Land land : alleLaender) {
            if (land.getName().toLowerCase().equals(suche)) {
                return land;
            }
        }
        return null;
    }
    //endregion


    public Kontinent findeKontinentenzugehoerigkeit(Land land) throws NullPointerException { //Wie gesagt, ich bin unsicher ob das hier schon korrekt ist oder erst wenn diese Methode aufgerufen wird
        for (Kontinent kontinent : alleKontinente) {
            if (kontinent.beinhaltetLand(land)) {
                return kontinent;
            }
        }
        return null; //sollte nie vorkommen
    }

}