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
import java.util.Objects;

public class RisikoClient implements ISpiel {
    private Socket socket;
    private final PrintStream socketOut;
    private final BufferedReader socketIn;
    private Welt welt;

    private final String separator = "%";

    public RisikoClient() throws IOException{
        socket = new Socket("127.0.0.1", 1399);
        socket.setSoTimeout(1000);
        InputStream inputStream = socket.getInputStream();
        socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        socketOut = new PrintStream(socket.getOutputStream());
        //ToDo konstruktor Bücherei 6 nachempfunden schreiben
        // initiiere Welt nach spiel init über setWelt()

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
    public Welt getWelt() throws IOException {
        if (welt == null){
            setWelt();
        }
        return welt;
    }

    private Welt setWelt() throws IOException {
        String cmd = Commands.CMD_GET_WELT.name();
        writeString(cmd);
        String[] data = readStringResponse();
        if (data == null || data.length <2) {
            return null;
        }
        if(!data[0].equals((Commands.CMD_GET_WELT_RESP.name()))){
            throw new RuntimeException("Falsches Kommando: " + data[0]);
        }

        int idx = 1;

        int spielerCount = Integer.parseInt(data[idx++]);
        ArrayList<Spieler> spielerListe = new ArrayList<>();
        for(int i = 0; i < spielerCount; i++){
            String[] s = data[idx++].split(":");
            int id = Integer.parseInt(s[0]);
            String name = s[1];
            String farbe = s[2];
            boolean alive = Boolean.parseBoolean(s[3]);
            Spieler spieler = (farbe == null || farbe.isEmpty()) ? new Spieler(name, id) : new Spieler(name, farbe, id); //CUI ohne farbe, GUI mit
            spielerListe.add(spieler);
        }
        int kontinentCount = Integer.parseInt(data[idx++]);
        ArrayList<Kontinent> kontinentListe = new ArrayList<>();
        ArrayList<int[]> kontinentLandIds = new ArrayList<>();
        for(int i = 0; i < kontinentCount; i++){
            String[] k = data[idx++].split(":");
            String kontinentName = k[0];
            int buff = Integer.parseInt(k[1]);
            String[] landIds = k.length > 2 && !k[2].isEmpty() ? k[2].split(",") : new String[0];
            int[] ids = Arrays.stream(landIds).filter(str -> !str.isEmpty()).mapToInt(Integer::parseInt).toArray();
            kontinentLandIds.add(ids);
            kontinentListe.add(null);
        }
        int landCount = Integer.parseInt(data[idx++]);
        ArrayList<Land> laenderListe = new ArrayList<>();
        ArrayList<int[]> nachbarnIds = new ArrayList<>();
        int startIdx = idx; //Merken für Nachbar-Verlinkung
        for(int i = 0; i < landCount; i++){
            String[] l = data[idx++].split(":");
            int id = Integer.parseInt(l[0]);
            String name = l[1];
            int besitzerId = Integer.parseInt(l[2]);
            int einheiten = Integer.parseInt(l[3]);
            int farbe = Integer.parseInt(l[4]);
            String[] nIds = l.length > 5 && !l[5].isEmpty() ? l[5].split(",") : new String[0];
            Land land = new Land(einheiten, name, id);
            land.setFarbe(farbe);
            laenderListe.add(land);
            nachbarnIds.add(Arrays.stream(nIds).filter(str -> !str.isEmpty()).mapToInt(Integer::parseInt).toArray());
        }

        for (int i = 0; i < landCount; i++) {
            Land land = laenderListe.get(i);
            String[] l = data[startIdx + i].split(":");
            int besitzerId = Integer.parseInt(l[2]);
            Spieler besitzer = spielerListe.stream().filter(s -> s.getId() == besitzerId).findFirst().orElse(null); land.setBesitzer(besitzer);
            if (besitzer != null) besitzer.fuegeLandHinzu(land);
            int[] nIds = nachbarnIds.get(i);
            for (int nId : nIds) {
                laenderListe.stream().filter(la -> la.getId() == nId).findFirst().ifPresent(land::addNachbar);
            }
        }
        for (int i = 0; i < kontinentCount; i++) {
            int[] ids = kontinentLandIds.get(i);
            Land[] gebiete = Arrays.stream(ids).mapToObj(id -> laenderListe.stream().filter(l -> l.getId() == id).findFirst().orElse(null)).filter(Objects::nonNull).toArray(Land[]::new);
            Kontinent k = new Kontinent(data[1 + spielerCount + 1 + i].split(":")[0], gebiete, Integer.parseInt(data[1 + spielerCount + 1 + i].split(":")[1]));
            kontinentListe.set(i,k);
        }

        welt = new Welt(laenderListe, kontinentListe);
        welt.setSpielerListe(spielerListe);
        return welt;
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
