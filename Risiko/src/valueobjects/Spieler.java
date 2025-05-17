package valueobjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class Spieler {
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

    //region getters
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    } //Wird das irgendwo außerhalb von Spieler genutzt?

    public boolean isAlive() {
        return alive;
    }

    public ArrayList<Land> getBesetzteLaender() {
        return besetzteLaender;
    }

    public HashSet<Karte> getKarten() {
        return karten;
    }

    //endregion

    //region Land Methoden

    public HashSet<Land> getFeinde() {  //todo wird nie genutzt! Nochmal in gruen //Kann gelöscht werden. Momentan brauchen wir es nicht und notfalls können wir es neu schreiben
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
    public void neueArmee(ArrayList<Kontinent> alleKontinente) { //Int zurückgeben? Wenn zuweisungEinheiten in die Domain wandern muss kann es von hier nicht aufgerufen werden. Bei neuem Spielzug dazu
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

    public void zuweisungEinheiten(int truppen) {//Domain & UI
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

                basis.einheitenHinzufuegen(1);
                break;
            }
        }
    }

    public void bewegeEinheiten(int truppen, Land herkunft, Land ziel) { //Domain - Interface in UI
        //ToDO throw errors
        /*if (herkunft.getBesitzer() != this) {
            //throw error (Diese Truppen gehören dir nicht!)
        }
        if (herkunft.getEinheiten() <= truppen) {
            //throw error (Es befinden sich zu wenig Einheiten auf diesem Feld!)
        }
        if (!herkunft.connectionPossible(ziel)) {
            //throw error (Die Länder sind nicht verbunden!)
        }*/
        herkunft.einheitenEntfernen(truppen);
        ziel.einheitenHinzufuegen(truppen);
    }
    //endregion
    public void sterben(Spieler moerder) {  //In Domain, da Aufruf eines anderen Spielers. Replace/Reduce hier mit alive=false
        moerder.getKarten().addAll(this.karten);
        karten.removeAll(karten);
        this.alive = false;
    }


    public int zaehleEinheiten() { //Kann weg. Notfalls neu coden
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

    public void zeigeSpieler(){ //UI
        System.out.println(id + " - " + name + " - " + besetzteLaender.size());
        for (Land land : besetzteLaender) {
            System.out.println(land.getBesitzer().getId() + ": " + land.getName() + " ("+ land.getEinheiten() +")");
        }
        System.out.println();
    }
    public Land findeEigenesLand(String name){ //UI
        String suche = name.trim().toLowerCase();
        for (Land land : besetzteLaender) {
            if (land.getName().toLowerCase().equals(suche)) {
                return land;
            }
        }
        System.out.println("Das valueobjects.Land "+ name +" existiert nicht");
        return null;
    }
    public String eigeneKartenToString(){ //Print? UI? - sollte UI sein, muss nochmal gucken wofür das überhaupt aufgerufen wurde. Glaube Peruse Cards
        String kartenTxt = "";
        for (Karte karte : karten){
            kartenTxt += "["+ karte.getStrength() + " - "+ karte.getLand().getName() +"]  ";
        }
        return kartenTxt;
    }
}