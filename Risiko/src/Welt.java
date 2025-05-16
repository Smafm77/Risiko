import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Welt {
    public ArrayList<Land> alleLaender;
    public ArrayList<Kontinent> alleKontinente;

    public Welt() throws IOException {
        alleLaender = new ArrayList<>();
        alleKontinente = new ArrayList<>();
        //Einlesen der Länder  -- das in persistence?
        BufferedReader brsl = new BufferedReader(new FileReader("Staatenliste.txt")); //ToDo Kontrolle ob relative Pfade funktionieren
        String inputsl;
        while ((inputsl = brsl.readLine()) != null) {
            String[] values = inputsl.trim().split(" "); //Array der Werte einer Zeile - müssen durch exakt ein Leerzeichen getrennt sein
            String landName = values[0];
            int staerke = Integer.parseInt(values[1]); //NumberFormatException
            if (!inputsl.isEmpty()) {
                alleLaender.add(new Land(staerke, landName));
            }
        }

        //Einlesen der Nachbarn -- das in persistence?
        BufferedReader brnl = new BufferedReader(new FileReader("Nachbarliste.txt"));
        String inputnl;
        int index = 0;
        while ((inputnl = brnl.readLine()) != null) {
            String[] values = inputnl.trim().split(" "); //inputnl beginnt in Zeile 2
            Land[] nachbarn = new Land[values.length - 1];
            for (int i = 0; i < nachbarn.length; i++) {
                nachbarn[i] = alleLaender.get(Integer.parseInt(values[i + 1])); //NumberFormatException
            }
            alleLaender.get(index++).addNachbarn(nachbarn);
        }

        //Einlesen der Kontinente -- das in persistence?
        BufferedReader brk = new BufferedReader(new FileReader("Kontinentliste.txt"));
        String inputk;
        while ((inputk = brk.readLine()) != null) {
            String[] values = inputk.trim().split(" ");
            int buff = Integer.parseInt(values[0]);
            String kontinentName = values[1];
            Land[] gebiete = new Land[values.length - 2];
            for (int i = 0; i < gebiete.length; i++) {
                gebiete[i] = alleLaender.get(Integer.parseInt(values[i + 2]));
            }
            alleKontinente.add(new Kontinent(kontinentName, gebiete, buff));
        }
    }

    public Collection<Karte> createCardStack() { //Warum hier und nicht in Karte?
        Collection<Karte> stapel = new ArrayList<>();
        for (Land land : alleLaender) {
            stapel.add(new Karte(land));
        }
        return stapel;
    }


    public void verteileLaender(List<Spieler> spielerListe) {  //Vielleicht eher in Spiel? Ist so glaube ich auch auf deiner Zeichnung wenn ich das richtig sehe :)

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

    //region temporary Visualisation -- UI?
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

    //endregion
    public Land findeLand(String name) {
        String suche = name.trim().toLowerCase();
        for (Land land : alleLaender) {
            if (land.getName().toLowerCase().equals(suche)) {
                return land;
            }
        }
        return null;
    }

    public Kontinent findeKontinentenzugehoerigkeit(Land land) {
        for (Kontinent kontinent : alleKontinente) {
            if (kontinent.beinhaltetLand(land)) {
                return kontinent;
            }
        }
        return null; //sollte nie vorkommen
    }

}