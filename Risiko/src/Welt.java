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

        //Einlesen der Länder
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

        //Einlesen der Nachbarn
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

        //Kontinente erstellen+Länder zuweisen; Hinweis: Falls Probleme, Zahlen checken
        alleKontinente = new ArrayList<>();
        Land[] europaL = {
                alleLaender.get(6),
                alleLaender.get(33),
                alleLaender.get(34),
                alleLaender.get(35),
                alleLaender.get(36),
                alleLaender.get(40),
                alleLaender.get(41),
        };
        Land[] nordAmerikaL = {
                alleLaender.get(8),
                alleLaender.get(12),
                alleLaender.get(16),
                alleLaender.get(22),
                alleLaender.get(25),
                alleLaender.get(26),
                alleLaender.get(30),
                alleLaender.get(31),
                alleLaender.get(32),
        };
        Land[] suedAmerikaL = {
                alleLaender.get(1),
                alleLaender.get(14),
                alleLaender.get(18),
                alleLaender.get(23),
        };
        Land[] afrikaL = {
                alleLaender.get(0),
                alleLaender.get(4),
                alleLaender.get(5),
                alleLaender.get(11),
                alleLaender.get(20),
                alleLaender.get(38),
        };
        Land[] asienL = {
                alleLaender.get(2),
                alleLaender.get(3),
                alleLaender.get(7),
                alleLaender.get(10),
                alleLaender.get(13),
                alleLaender.get(17),
                alleLaender.get(19),
                alleLaender.get(21),
                alleLaender.get(24),
                alleLaender.get(27),
                alleLaender.get(28),
                alleLaender.get(39),
        };
        Land[] australienL = {
                alleLaender.get(9),
                alleLaender.get(15),
                alleLaender.get(29),
                alleLaender.get(37),
        };

        //Kontinente erstellen (Zahlen = Boni)
        alleKontinente.add(new Kontinent("Europa", europaL, 5));
        alleKontinente.add(new Kontinent("Nord-Amerika", nordAmerikaL, 5));
        alleKontinente.add(new Kontinent("Süd-Amerika", suedAmerikaL, 2));
        alleKontinente.add(new Kontinent("Afrika", afrikaL, 3));
        alleKontinente.add(new Kontinent("Asien", asienL, 7));
        alleKontinente.add(new Kontinent("Australien", australienL, 2));
    }

    public Collection<Karte> createCardStack() {
        Collection<Karte> stapel = new ArrayList<>();
        for (Land land : alleLaender) {
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
    public void printWorldMap() {
        System.out.println("Weltkarte:");
        System.out.println();
        for (Land land : alleLaender) {
            String nachbarn = land.getNachbarn().stream().map(n -> n.getName()).collect(Collectors.joining(", "));
            System.out.println(land.getName() + " | Angrenzend:" + nachbarn);
        }
        System.out.println();
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
    public Kontinent findeKontinentenzugehoerigkeit(Land land){
        for (Kontinent kontinent : alleKontinente){
            if (kontinent.beinhaltetLand(land)){
                return kontinent;
            }
        }
        return null; //sollte nie vorkommen
    }

}