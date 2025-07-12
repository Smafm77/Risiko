package client.ui.cui;

import common.enums.Befehl;
import common.exceptions.*;
import common.valueobjects.Land;
import common.valueobjects.Spieler;
import common.enums.Spielphase;
import common.enums.Infos;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class MenueLogik {
    Menue menue;
    MenuePrint mPrint;
    MenueEingabe mEingabe;

    public MenueLogik(Menue menue, MenuePrint mPrint, MenueEingabe mEingabe) {
        this.menue = menue;
        this.mPrint = mPrint;
        this.mEingabe = mEingabe;
    }

    public void infoLand() {
        System.out.println("Über welches Land möchtest du Informationen erhalten?");
        Land auswahlLand = mEingabe.eingabeLand();
        boolean weiter = true;
        while (weiter) {
            weiter = menue.infoMenue(auswahlLand);
        }
    }

    public boolean infoAuswahl(Land auswahlLand) {

        Infos auswahl = mEingabe.infoAbfrage();
        switch (auswahl) {
            case BESITZER:
                System.out.println(auswahlLand.getName() + " befindet sich in " + auswahlLand.getBesitzer().getName() + "'s Besitz.");
                break;
            case EINHEITEN:
                System.out.println("In " + auswahlLand.getName() + " befinden sich aktuell " + auswahlLand.getEinheiten() + " Einheiten.");
                break;
            case NACHBARN:
                System.out.println("Die Nachbarländer von " + auswahlLand.getName() + " sind: ");
                for (Land nachbar : auswahlLand.getNachbarn()) {
                    System.out.println(nachbar.getName());
                }
                break;
            case ZURUECK:
                return false;
        }
        return true;

    }

    public boolean hauptAuswahl(Spieler spieler, Spielphase phase) throws SpielPhaseException, FalscherBesitzerException, UngueltigeBewegungException, UngueltigeAuswahlException {
        Befehl auswahl = mEingabe.hauptAbfrage();

        if (auswahl == Befehl.ANGRIFF && phase != Spielphase.ANGRIFF) {
            throw new SpielPhaseException("Angreifen nur in der Angriffsphase erlaut!");
        }

        if (auswahl == Befehl.BEWEGEN && phase != Spielphase.VERSCHIEBEN) {
            throw new SpielPhaseException("Truppen verschiebenm nur in der Verschiebephase erlaubt!");
        }
        switch (auswahl) {
            case ANGRIFF:
                kampfInterface(spieler);
                break;
            case BEWEGEN:
                moveTroopsInterface(spieler);
                break;
            case INFO:
                infoLand();
                break;
            case UEBERSICHT:
                mPrint.printTheseLaender(spieler.getBesetzteLaender());
                break;
            case KARTE:
                menue.peruseCards(spieler);
                break;
            case ZUGBEENDEN:
                return false;
        }
        return true;

    }

    public void moveTroopsInterface(Spieler spieler) throws FalscherBesitzerException, UngueltigeAuswahlException, UngueltigeBewegungException {
        while (true) {
            mPrint.zeigeEigeneGebiete(spieler);
            System.out.println("Aus welchem Land sollen Einheiten entzogen werden?");
            Land herkunft = mEingabe.istEigenesLand(spieler);
            System.out.println("In welches Land sollen die Einheiten geschickt werden?");
            Land ziel = mEingabe.istEigenesLand(spieler);
            System.out.println("Wie viele der " + herkunft.getEinheiten() + " Einheiten aus " + herkunft.getName() + " sollen nach " + ziel.getName() + " entsendet werden?");
            int anzahl = mEingabe.eingabeTruppen();
            spieler.bewegeEinheiten(anzahl, herkunft, ziel);
            System.out.println("Neue Anzahl Einheiten: " + herkunft.getName() + ": " + herkunft.getEinheiten() + " , " + ziel.getName() + ": " + ziel.getEinheiten());
            break;
        }
    }

    public boolean kampfInterface(Spieler angreifer) throws FalscherBesitzerException, UngueltigeAuswahlException, UngueltigeBewegungException {
        HashSet<Land> volleKasernen = angreifer.getBesetzteLaender().stream().filter(land -> land.getEinheiten() > 1).collect(Collectors.toCollection(HashSet::new));
        boolean ergebnis;
        Land herkunft;
        Land ziel;
        int truppenA;
        int truppenV;
        Spieler verteidiger;
        ArrayList<Land> relevanteLaender = new ArrayList<>();
        while (true) {
            if (volleKasernen.isEmpty()) {
                return false;
            }
            mPrint.printTheseLaender(volleKasernen);
            System.out.println("Aus welchem Land willst du angreifen?");
            herkunft = mEingabe.istEigenesLand(angreifer);
            relevanteLaender.add(herkunft);
            mPrint.printTheseLaenderNamen(herkunft.getFeindlicheNachbarn());
            System.out.println("Welches Land möchtest du angreifen?");
            ziel = mEingabe.istFeind(angreifer);
            relevanteLaender.add(ziel);
            verteidiger = ziel.getBesitzer();
            System.out.println("Mit wie vielen Truppen möchtest du angreifen (1 - " + Math.min((herkunft.getEinheiten() - 1), 3) + ")?");
            truppenA = mEingabe.eingabeTruppen();
            System.out.println();
            System.out.println(verteidiger.getName() + " mit wie vielen Truppen möchtest du dich verteidigen (1 - " + Math.min((ziel.getEinheiten()), 2) + ")?");
            truppenV = mEingabe.eingabeTruppen();
            System.out.println();
            ergebnis = menue.getSpiel().kampf(herkunft.getId(), ziel.getId(), truppenA, truppenV);
            String sieger = ergebnis ? angreifer.getName() : verteidiger.getName();
            System.out.println(sieger + " hat gewonnen:");
            mPrint.printTheseLaender(relevanteLaender);
            break;
        }
        return ergebnis;
    }


    public boolean weiterSpielen() {
        int auswahl = mEingabe.inGameMenue();
        while (true) {
            switch (auswahl) {
                case 1:
                    return true;
                case 2:
                    return false;
            }
        }
    }
}
