package server.persistence;

import common.valueobjects.Karte;
import common.valueobjects.Kontinent;
import common.valueobjects.Land;
import server.domain.missionen.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class NeuesSpielEinlesen implements Serializable {

    public Map<String, Integer> laenderFarbcodesEinlesen() throws IOException {
        Map<String, Integer> map = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader("Risiko/Txt-dateien/Laenderfarben.txt"));
        String input;
        while ((input = br.readLine()) != null) {
            input = input.trim();
            if (!input.contains(":")) continue;
            String[] parts = input.split(":");
            if (parts.length < 2) continue;
            String landName = parts[0].trim();
            String farbeStr = parts[1].trim();
            try {
                int farbcode = Integer.parseInt(farbeStr, 16);
                map.put(landName, farbcode);
            } catch (NumberFormatException e) {
                System.out.println("FEHLER: Farbcode für " + landName + " nicht lesbar: " + farbeStr);
            }
        }
        br.close();
        return map;
    }

    public ArrayList<Land> alleLaenderEinlesen() throws IOException,
            NumberFormatException { //FileReader, readLine, parseInt
        ArrayList<Land> alleLaender = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader("Risiko/Txt-dateien/Staatenliste.txt"));
        String input;

        Map<String, Integer> farbMap = laenderFarbcodesEinlesen();

        while ((input = br.readLine()) != null) {
            String[] values = input.trim().split(" "); //Array der Werte einer Zeile - müssen durch exakt ein Leerzeichen getrennt sein
            String landName = values[0].trim();
            int staerke = Integer.parseInt(values[1]); //NumberFormatException
            int id = Integer.parseInt(values[2]);
            if (!input.isEmpty()) {
                Land land = new Land(staerke, landName, id);
                Integer farbe = farbMap.get(landName);
                if (farbe != null) {
                    land.setFarbe(farbe);
                } else {
                    System.out.println("WARNUNG: Kein Farbcode für Land \"" + landName + "\" gefunden!");
                    land.setFarbe(0xCCCCCC);
                }
                alleLaender.add(land);
            }
        }
        alleNachbarnEinlesen(alleLaender);
        return alleLaender;
    }

    private void alleNachbarnEinlesen(ArrayList<Land> laender) throws IOException,
            NumberFormatException {//FileReader, readLine, parseInt
        BufferedReader br = new BufferedReader(new FileReader("Risiko/Txt-dateien/Nachbarliste.txt"));
        String input;
        int index = 0;
        while ((input = br.readLine()) != null) {
            String[] values = input.trim().split(" ");
            Land[] nachbarn = new Land[values.length - 1];
            for (int i = 0; i < nachbarn.length; i++) {
                nachbarn[i] = laender.get(Integer.parseInt(values[i + 1])); //NumberFormatException
            }
            laender.get(index++).addNachbarn(nachbarn);
        }
    }

    public ArrayList<Kontinent> alleKontinenteEinlesen(ArrayList<Land> laender) throws IOException,
            NumberFormatException { //FileReader, readLine, parseInt{
        ArrayList<Kontinent> kontinente = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader("Risiko/Txt-dateien/Kontinentliste.txt"));
        String input;
        while ((input = br.readLine()) != null) {
            String[] values = input.trim().split(" ");
            int buff = Integer.parseInt(values[0]);
            String kontinentName = values[1];
            Land[] gebiete = new Land[values.length - 2];
            for (int i = 0; i < gebiete.length; i++) {
                gebiete[i] = laender.get(Integer.parseInt(values[i + 2]));
            }
            kontinente.add(new Kontinent(kontinentName, gebiete, buff));
        }
        return kontinente;
    }

    public HashSet<Karte> kartenstapelEinlesen(ArrayList<Land> laender) throws IOException, NumberFormatException {//FileReader, readLine, parseInt
        HashSet<Karte> alleKarten = new HashSet<>();
        BufferedReader br = new BufferedReader(new FileReader("Risiko/Txt-dateien/Staatenliste.txt"));
        String input;
        int index = 0;
        while ((input = br.readLine()) != null) {
            String[] values = input.trim().split(" ");
            int staerke = Integer.parseInt(values[1]);
            alleKarten.add(new Karte(laender.get(index), staerke));
            index++;
        }
        return alleKarten;
    }

    public HashSet<Mission> missionenErstellen(ArrayList<Kontinent> kontinente) {
        HashSet<Mission> missionen = new HashSet<>();

        missionen.add(new Kontinenteroberung(kontinente.get(1), kontinente.get(5)));
        missionen.add(new Kontinenteroberung(kontinente.get(1), kontinente.get(3)));
        missionen.add(new Kontinenteroberung(kontinente.get(4), kontinente.get(2)));
        missionen.add(new Kontinenteroberung(kontinente.get(3), kontinente.get(4)));

        missionen.add(new KleinerKontinentEroberung(kontinente.get(0), kontinente.get(5)));
        missionen.add(new KleinerKontinentEroberung(kontinente.get(0), kontinente.get(2)));

        missionen.add(new Laendereroberungplus(18));
        missionen.add(new Laendereroberung(24));
        for (int i = 1; i <= 6; i++) {
            missionen.add(new Spielervernichtung(i, 24));
        }

        return missionen;
    }
}
