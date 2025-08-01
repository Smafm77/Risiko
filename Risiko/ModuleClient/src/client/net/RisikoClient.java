package client.net;

import common.enums.Commands;
import common.enums.Spielphase;
import common.exceptions.FalscherBesitzerException;
import common.exceptions.UngueltigeBewegungException;
import common.valueobjects.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class RisikoClient implements ISpiel {
    private Socket socket;
    private final InputStream socketIn;
    private final OutputStream socketOut;
    private final String spielerName;
    private final String spielerColor;
    private final String separator = "%";

    //region convenience
    private String[] readStringResponse(){
        BufferedReader socketInPrint = new BufferedReader(new InputStreamReader(socketIn));
        String[] data = null;
        try{
            String received = socketInPrint.readLine();
            System.out.println("Empfangene Daten: "+ received);
            data = received.split(separator);
        } catch(SocketTimeoutException e) {
            System.out.println("Server hat nicht geantwortet.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
    private void writeString(String cmd){
        System.out.println("Gesendete Daten: " + cmd);
        PrintStream socketOutPrint = new PrintStream(socketOut);
        socketOutPrint.println(cmd);
    }
    private Object readObjectResponse(){
        Object resp;
        try{
            ObjectInputStream socketInObject = new ObjectInputStream(socketIn);
            resp = socketInObject.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return resp;
    }
    private void writeObject(Object obj){
        try {
            ObjectOutputStream socketOutObject = new ObjectOutputStream(socketOut);
            socketOutObject.reset();
            socketOutObject.writeObject(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void checkResponse(String[] data, Commands command){
        if(Commands.valueOf(data[0]) != command){
            throw new RuntimeException("Ungueltige Antwort auf Anfrage erhalten!");
        }
    }
    private void checkExceptions(String[] data) throws FalscherBesitzerException, UngueltigeBewegungException {
        if(Commands.valueOf(data[0]) == Commands.EX_FALSCHER_BESITZER){
            throw new FalscherBesitzerException(data[1]);
        }
        if(Commands.valueOf(data[0]) == Commands.EX_FALSCHE_BEWEGUNG){
            throw new UngueltigeBewegungException(data[1]);
        }
    }
    //endregion

    @Override
    public Spieler getAktuellerSpieler() {
        String cmd = Commands.CMD_GET_AKTUELLER_SPIELER.name();
        writeString(cmd);

        String[] data = readStringResponse();

        checkResponse(data, Commands.CMD_GET_AKTUELLER_SPIELER_RESP);

        int spielerId = Integer.parseInt(data[1]);
        return getWelt().findeSpieler(spielerId);
    }

    @Override
    public Welt getWelt() {
        String cmd = Commands.CMD_GET_WELT.name();
        writeString(cmd);
        return (Welt) readObjectResponse();
    }


    @Override
    public Spielphase getPhase() {
        String cmd = Commands.CMD_GET_SPIELPHASE.name();
        writeString(cmd);
        String[] phase = readStringResponse();
        checkResponse(phase, Commands.CMD_GET_SPIELPHASE_RESP);
        return Spielphase.valueOf(phase[1]);
    }

    @Override
    public ArrayList<Spieler> getSpielerListe() {
        return getWelt().spielerListe;
    }

    @Override
    public int berechneSpielerEinheiten(int spielerId){
        String cmd = Commands.CMD_GET_SPIELER_EINHEITEN.name() + separator + spielerId;
        writeString(cmd);
        String[] einheiten = readStringResponse();
        checkResponse(einheiten, Commands.CMD_GET_SPIELER_EINHEITEN_RESP);
        return Integer.parseInt(einheiten[1]);
    }
    @Override
    public HashSet<Karte> getSpielerKarten(int spielerId){
        String cmd = Commands.CMD_GET_KARTEN.name() + separator + spielerId;
        writeString(cmd);
        return (HashSet<Karte>) readObjectResponse();
    }

    @Override
    public int spieleKarte(int spielerId, Karte karte) {
        String cmd = Commands.CMD_SPIELE_KARTE.name() + separator + spielerId + separator + karte.getLand().getName();
        writeString(cmd);
        String[] data = readStringResponse();
        checkResponse(data, Commands.CMD_SPIELE_KARTE_RESP);
        return Integer.parseInt(data[1]);
    }

    @Override
    public String getMissionBeschreibung(int spielerId) {
        String cmd = Commands.CMD_GET_MISS_BESCHREIBUNG.name() + separator + spielerId;
        writeString(cmd);
        String[] beschreibung = readStringResponse();
        checkResponse(beschreibung, Commands.CMD_GET_MISS_BESCHREIBUNG_RESP);
        return beschreibung[1];
    }

    @Override
    public boolean hatMissionErfuellt(int spielerId) {
        String cmd = Commands.CMD_GET_MISS_ERFUELLT.name() + separator + spielerId;
        writeString(cmd);
        String[] erfolg = readStringResponse();
        checkResponse(erfolg, Commands.CMD_GET_MISS_ERFUELLT_RESP);
        return Boolean.parseBoolean(erfolg[1]);
    }

    @Override
    public int getMissionProgress(int spielerId) {
        String cmd = Commands.CMD_GET_MISS_PROGRESS.name() + separator + spielerId;
        writeString(cmd);
        String[] prog = readStringResponse();
        checkResponse(prog, Commands.CMD_GET_MISS_PROGRESS_RESP);
        return Integer.parseInt(prog[1]);
    }
    @Override
    public Spieler getLandbesitzer (int landId){
        String cmd = Commands.CMD_GET_LANDBESITZER.name() + separator + landId;
        writeString(cmd);
        return (Spieler) readObjectResponse();
    }
    @Override
    public int getLandTruppen (int landId){
        String cmd = Commands.CMD_GET_LANDTRUPPEN.name() + separator + landId;
        writeString(cmd);
        String[] truppen = readStringResponse();
        checkResponse(truppen, Commands.CMD_GET_LANDTRUPPEN_RESP);
        return Integer.parseInt(truppen[1]);
    }


    @Override
    public void setSpielerliste (ArrayList<Spieler> spielerListe){
        String cmd = Commands.CMD_SET_SPIELERLISTE.name();
        writeString(cmd);
        String[] resp = readStringResponse();
        checkResponse(resp, Commands.CMD_SET_SPIELERLISTE_RESP);

        writeObject(spielerListe);
    }


    @Override
    public void weiseMissionenZu() {
        writeString(Commands.CMD_WEISE_MISS_ZU.name());
        String[] resp = readStringResponse();
        checkResponse(resp, Commands.CMD_WEISE_MISS_ZU_RESP);
    }

    @Override
    public void einheitenStationieren(int landId, int einheiten){
        String cmd = Commands.CMD_STATIONIEREN.name() + separator + landId + separator + einheiten;
        writeString(cmd);
        String[] report = readStringResponse();
        checkResponse(report, Commands.CMD_STATIONIEREN_RESP);
    }



    @Override
    public void init() {
        writeString(Commands.CMD_SPIEL_INIT.name());
        String[] resp = readStringResponse();
        checkResponse(resp, Commands.CMD_SPIEL_INIT_RESP);
    }

    @Override
    public void naechstePhase() {
        writeString(Commands.CMD_NAECHSTE_PHASE.name());
        String[] resp = readStringResponse();
        checkResponse(resp, Commands.CMD_NAECHSTE_PHASE_RESP);
    }

    @Override
    public boolean kampf(int herkunftId, int zielId, int truppenA, int truppenV) throws FalscherBesitzerException, UngueltigeBewegungException {
        String cmd = Commands.CMD_KAMPF.name() + separator + herkunftId + separator + zielId + separator + truppenA + separator + truppenV;
        writeString(cmd);
        String[] erfolg = readStringResponse();
        checkExceptions(erfolg);
        checkResponse(erfolg, Commands.CMD_KAMPF_RESP);
        return Boolean.parseBoolean(erfolg[1]);
    }

    @Override
    public void bewegeEinheiten(int spielerId, int truppen, int herkunftId, int zielId) throws FalscherBesitzerException, UngueltigeBewegungException {
        String cmd = Commands.CMD_BEWEGE_EINHEITEN.name() + separator + spielerId + separator + truppen + separator + herkunftId + separator + zielId;
        writeString(cmd);
        String[] resp = readStringResponse();
        checkExceptions(resp);
        checkResponse(resp, Commands.CMD_BEWEGE_EINHEITEN_RESP);
    }

    @Override
    public void spielSpeichern(){
        writeString(Commands.CMD_SPIEL_SPEICHERN.name());
    }

    public RisikoClient(Socket socket, String spielerName, String spielerColor) throws IOException{
        this.socket = socket;
        this.socket.setSoTimeout(1000);
        this.socketIn = socket.getInputStream();
        this.socketOut = socket.getOutputStream();
        this.spielerName = spielerName;
        this.spielerColor = spielerColor;
    }
    public String getSpielerName(){
        return spielerName;
    }

    public String getSpielerColor(){
        return spielerColor;
    }

}
