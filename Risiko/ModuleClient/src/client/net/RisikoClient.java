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
import java.util.NoSuchElementException;

public class RisikoClient implements ISpiel {
    private Socket socket;
    private final PrintStream socketOut;
    private final BufferedReader socketIn;
    private Welt welt;

    private final String separator = "%";

    public RisikoClient() throws IOException{
        socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        socketOut = new PrintStream(socket.getOutputStream());
        //ToDo konstruktor Bücherei 6 nachempfunden schreiben
        // initiiere Welt nach spiel init über initWelt()
        /*
                public BibliothekClient() throws IOException {
                    // Verbindung zum Server aufbauen
                    socket = new Socket("127.0.0.1", 1399);
                    // Siehe Doku:
                    // With this option set to a positive timeout value, a read() call on the InputStream associated with
                    // this Socket will block for only this amount of time.
                    socket.setSoTimeout(1000); // Jegliche Antworten vom Server werden innerhalb einer Sekunde erwartet

                    // Streams vom Socket holen
                    InputStream inputStream = socket.getInputStream();
                    socketIn = new BufferedReader(new InputStreamReader(inputStream));
                    socketOut = new PrintStream(socket.getOutputStream());
                }
         */
    }

    //region convenience
    private String[] readStringResponse(){
        String[] data = null;
        try{
            String received = socketIn.readLine();
            data = received.split(separator);
        } catch(SocketTimeoutException e) {
            System.out.println("Server hat nicht geantwortet.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
    private void writeString(String cmd){
        socketOut.println(cmd);
    }
    private void checkResponse(String[] data, Commands command){
        if(Commands.valueOf(data[0]) != command){
            throw new RuntimeException("Ungueltige Antwort auf Anfrage erhalten!");
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
        return welt.findeSpieler(spielerId);
    }

    @Override
    public Welt getWelt() {
        return welt;
    }

    private Welt initWelt(){
        //TODO: use CMD_GET_WELT to initialize Welt
        return null;
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
        return welt.spielerListe;
    }

    @Override
    public int spieleKarte(Spieler spieler, Karte karte) {
        String cmd = Commands.CMD_SPIELE_KARTE.name() + separator + spieler.getId() + separator + karte.getLand().getName();
        writeString(cmd);
        String[] data = readStringResponse();
        checkResponse(data, Commands.CMD_SPIELE_KARTE_RESP);
        return Integer.parseInt(data[1]);
    }

    @Override
    public String getMissionBeschreibung(Spieler spieler) {
        String cmd = Commands.CMD_GET_MISS_BESCHREIBUNG.name() + separator + spieler.getId();
        writeString(cmd);
        String[] beschreibung = readStringResponse();
        checkResponse(beschreibung, Commands.CMD_GET_MISS_BESCHREIBUNG_RESP);
        return beschreibung[1];
    }

    @Override
    public boolean hatMissionErfuellt(Spieler spieler) {
        String cmd = Commands.CMD_GET_MISS_ERFUELLT.name() + separator + spieler.getId();
        writeString(cmd);
        String[] erfolg = readStringResponse();
        checkResponse(erfolg, Commands.CMD_GET_MISS_ERFUELLT_RESP);
        return Boolean.parseBoolean(erfolg[1]);
    }

    @Override
    public int getMissionProgress(Spieler spieler) {
        String cmd = Commands.CMD_GET_MISS_PROGRESS.name() + separator + spieler.getId();
        writeString(cmd);
        String[] prog = readStringResponse();
        checkResponse(prog, Commands.CMD_GET_MISS_PROGRESS_RESP);
        return Integer.parseInt(prog[1]);
    }

    @Override
    public void setPhase(Spielphase spielphase) {
        String cmd = Commands.CMD_SET_PHASE.name() + separator + spielphase.name();
        writeString(cmd);
    }

    @Override
    public void weiseMissionenZu() {
        writeString(Commands.CMD_WEISE_MISS_ZU.name());
    }

    @Override
    public void init() {
        writeString(Commands.CMD_SPIEL_INIT.name());
    }

    @Override
    public void naechstePhase() {
        writeString(Commands.CMD_NAECHSTE_PHASE.name());
    }

    @Override
    public boolean kampf(Land herkunft, Land ziel, int truppenA, int truppenV) throws FalscherBesitzerException, UngueltigeBewegungException {
        String cmd = Commands.CMD_KAMPF.name() + separator + herkunft.getId() + separator + ziel.getId() + separator + truppenA + separator + truppenV;
        writeString(cmd);
        String[] erfolg = readStringResponse();
        checkResponse(erfolg, Commands.CMD_KAMPF_RESP);
        return Boolean.parseBoolean(erfolg[1]);
    }

    @Override
    public void spielSpeichern(){
        writeString(Commands.CMD_SPIEL_SPEICHERN.name());
    }
}
