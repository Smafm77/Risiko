package common.valueobjects;

import common.enums.Spielphase;
import common.exceptions.*;

import java.util.ArrayList;

public interface ISpiel {
    //not working
    /*
    Spiel getInstance();
     */
    //region getter
    Spieler getAktuellerSpieler();
    Welt getWelt();
    Spielphase getPhase();
    ArrayList<Spieler> getSpielerListe();
    int spieleKarte(Spieler spieler, Karte karte);
    String getMissionBeschreibung(Spieler spieler);
    boolean hatMissionErfuellt(Spieler spieler);
    int getMissionProgress(Spieler spieler);
    //endregion

    //region setter
    void addSpieler(Spieler spieler);
    void setPhase(Spielphase spielphase);
    void weiseMissionenZu();
    //endregion

    //region other
    void init();
    void naechstePhase();
    boolean kampf(Land herkunft, Land ziel, int truppenA, int truppenV) throws FalscherBesitzerException, UngueltigeBewegungException;
    void spielSpeichern ();
    //endregion
}
