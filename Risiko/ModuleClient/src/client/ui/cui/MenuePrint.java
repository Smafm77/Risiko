package client.ui.cui;

import common.valueobjects.Land;
import common.valueobjects.Spieler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class MenuePrint{
    Menue menue;
    public MenuePrint(Menue menue){
        this.menue = menue;
    }

    public void zeigeEigeneGebiete(Spieler spieler) {
        System.out.println("All deine Gebiete:");
        for (Land land : spieler.getBesetzteLaender()) {
            System.out.print(spieler.getId() + " - " + land.getName() + ": " + land.getEinheiten() + " | ");
            for (Land nachbar : land.getNachbarn()) {
                for (Land besetzt : spieler.getBesetzteLaender()) {
                    if (besetzt == nachbar) {
                        System.out.print(nachbar.getName() + " ");
                    }
                }
            }
            System.out.println();
        }
    }

    public void zeigeAlleSpieler(ArrayList<Spieler> spielerListe) {
        for (Spieler spieler : spielerListe) {
            spieler.zeigeSpieler();
        }
        System.out.println();
        System.out.println();
    }

    public void printWorldMap() {
        System.out.println("Weltkarte:");
        System.out.println();
        for (Land land : menue.getWelt().getAlleLaender()) {
            String nachbarn = land.getNachbarn().stream().map(Land::getName).collect(Collectors.joining(", "));
            System.out.println(land.getName() + " | Angrenzend:" + nachbarn);
        }
        System.out.println();
    }

    public void printTheseLaender(Collection<Land> laender) {
        for (Land land : laender) {
            StringBuilder fNachbarn = new StringBuilder();
            for (Land fLand : land.getFeindlicheNachbarn()) {
                fNachbarn.append(" [").append(fLand.getName()).append(" - ").append(fLand.getBesitzer().getName()).append("(").append(fLand.getEinheiten()).append(")]");
            }
            System.out.println(land.getName() + " ist im Besitz von " + land.getBesitzer().getName() + "(" + land.getEinheiten() + ") und bedroht von " + fNachbarn);
        }
    }

    public void printTheseLaenderNamen(Collection<Land> laender) {
        for (Land land : laender) {
            System.out.println(land.getName() + " ist im Besitz von " + land.getBesitzer().getName() + "(" + land.getEinheiten() + ")");
        }
    }

}

