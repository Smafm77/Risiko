package common.valueobjects;

import common.exceptions.UngueltigeAuswahlException;

import java.io.*;
import java.util.*;

public class Welt implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final ArrayList<Land> alleLaender;
    public ArrayList<Kontinent> alleKontinente;
    public ArrayList<Spieler> spielerListe = new ArrayList<>();

    public ArrayList<Land> getAlleLaender() {
        return alleLaender;
    }

    public ArrayList<Spieler> getSpielerListe() {
        return spielerListe;
    }

    public void setSpielerListe(ArrayList<Spieler> spielerListe) {
        this.spielerListe.clear();
        this.spielerListe.addAll(spielerListe);
        verteileLaender();
    }

    public Welt(ArrayList<Land> laender, ArrayList<Kontinent> kontinente) {
        alleLaender = laender;
        alleKontinente = kontinente;
    }

    private void verteileLaender() {
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
    public Land findeLand(int landId) {
        for (Land land : alleLaender) {
            if (land.getId() == landId) {
                return land;
            }

        }
        System.out.println("Land mit ID " + landId + " existiert nicht.");
        return null;
    }

    public Kontinent findeKontinentenzugehoerigkeit(Land land) {
        for (Kontinent kontinent : alleKontinente) {
            if (kontinent.beinhaltetLand(land)) {
                return kontinent;
            }
        }
        return null;
    }

    public Spieler findeSpieler(int id) {
        return spielerListe.stream().filter(spieler -> spieler.getId() == id).findAny().orElseThrow();
    }

}