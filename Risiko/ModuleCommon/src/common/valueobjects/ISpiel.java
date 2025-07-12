package common.valueobjects;

import common.enums.Spielphase;
import common.exceptions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

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
    int berechneSpielerEinheiten(int spielerId);
    HashSet<Karte> getSpielerKarten(int spielerId);
    int spieleKarte(int spielerId, Karte karte);
    String getMissionBeschreibung(int spielerId);
    boolean hatMissionErfuellt(int spielerId);
    int getMissionProgress(int spielerId);
    Spieler getLandbesitzer (int landId);
    int getLandTruppen (int landId);
    //endregion

    //region setter
    void setSpielerliste (ArrayList<Spieler> spielerListe);
    void setPhase(Spielphase spielphase);
    void weiseMissionenZu();
    void einheitenStationieren(int landId, int einheiten);
    //endregion

    //region other
    void init();
    void naechstePhase();
    boolean kampf(int herkunftId, int zielId, int truppenA, int truppenV) throws FalscherBesitzerException, UngueltigeBewegungException;
    void bewegeEinheiten(int spielerId, int truppen, int herkunftId, int zielId) throws FalscherBesitzerException, UngueltigeBewegungException;
    void spielSpeichern ();
    //endregion
}
