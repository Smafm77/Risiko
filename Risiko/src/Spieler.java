import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class Spieler {
    //region Basics
    private final String name;
    private final int id;
    private boolean alive;
    private ArrayList<Land> besetzteLaender = new ArrayList<>();
    private HashSet<Karte> karten = new HashSet<>();

    public Spieler(String name, int id) {
        this.name = name.trim();
        this.id = id;
        alive = true;
    }

    //endregion
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public boolean isAlive() {
        return alive;
    }

    public ArrayList<Land> getBesetzteLaender() {
        return besetzteLaender;
    }

    public HashSet<Karte> getKarten() {
        return karten;
    }

    //region Land Methoden

    //Todo Test this
    public HashSet<Land> getFeinde() {
        HashSet<Land> feinde = new HashSet<>();
        for (Land kolonie : besetzteLaender) {
            feinde.addAll(kolonie.getFeindlicheNachbarn());
        }
        return feinde;
    }

    public void fuegeLandHinzu(Land land){
        besetzteLaender.add(land);
    }

    public void verliereLand(Land land) {
        besetzteLaender.remove(land);
    }
    //endregion

    //region Einheiten
    public void neueArmee(ArrayList<Kontinent> alleKontinente) { //Bei neuem Spielzug dazu
        int neueEinheiten = 0;
        //Zuschuss besetzte Länder
        if (besetzteLaender.size() <= 9) {
            neueEinheiten += 3;
        } else {
            neueEinheiten += (besetzteLaender.size() / 3);
        }
        //Zuschuss Kontinente
        Kontinent[] reiche = alleKontinente.stream().filter(kontinent -> kontinent.getEinzigerBesitzer() == this).toArray(Kontinent[]::new);
        if (reiche.length > 0) {
            neueEinheiten += Arrays.stream(reiche).mapToInt(Kontinent::getBuff).sum();
        }

        zuweisungEinheiten(neueEinheiten);
    }

    public void zuweisungEinheiten(int truppen) {
        for (int t = 1; t <= truppen; t++) {
            Scanner scanner = new Scanner(System.in);
            while(true){
                System.out.println("("+ t +"/"+ truppen +")Wohin soll diese Einheit gesetzt werden "+ name +"?");
                System.out.println();
                zeigeSpieler();

                Land basis = findeEigenesLand(scanner.nextLine());
                if (basis == null){
                    continue;
                }

                basis.einheitRekrutiert();
                break;
            }
        }
    }

    public void bewegeEinheiten(int truppen, Land herkunft, Land ziel) { //ToDo Wieso gibt es moveTroops sowohl in Spieler als auch in Spiel?
        //ToDO throw error
        /*if (herkunft.getBesitzer() != this) {
            //throw error (Diese Truppen gehören dir nicht!)
        }
        if (herkunft.getEinheiten() <= truppen) {
            //throw error (Es befinden sich zu wenig Einheiten auf diesem Feld!)
        }
        if (!herkunft.connectionPossible(ziel)) {
            //throw error (Die Länder sind nicht verbunden!)
        }*/
        int herkunftEinheiten = herkunft.getEinheiten();
        herkunft.setEinheiten(herkunftEinheiten - truppen);
        int zielEinheiten = ziel.getEinheiten();
        ziel.setEinheiten(zielEinheiten + truppen);
    }
    //endregion

    public void sterben(Spieler moerder) {
        moerder.getKarten().addAll(this.karten);
        karten.removeAll(karten);
        this.alive = false;
    }

    public int zaehleEinheiten() {
        int soldaten = 0;
        for (Land land : besetzteLaender) {
            soldaten += land.getEinheiten();
        }
        return soldaten;
    }

    @Override
    public boolean equals(Object spieler) {
        return (spieler instanceof Spieler) && ((Spieler) spieler).id == this.id;
    }

    public void zeigeSpieler(){
        System.out.println(id + " - " + name + " - " + besetzteLaender.size());
        for (Land land : besetzteLaender) {
            System.out.println(land.getBesitzer().getId() + ": " + land.getName() + " ("+ land.getEinheiten() +")");
        }
        System.out.println();
    }
    public Land findeEigenesLand(String name){
        String suche = name.trim().toLowerCase();
        for (Land land : besetzteLaender) {
            if (land.getName().toLowerCase().equals(suche)) {
                return land;
            }
        }
        System.out.println("Das Land "+ name +" existiert nicht");
        return null;
    }
    public String eigeneKartenToString(){
        String kartenTxt = "";
        for (Karte karte : karten){
            kartenTxt += "["+ karte.getStrength() + " - "+ karte.getLand().getName() +"]  ";
        }
        return kartenTxt;
    }
}
