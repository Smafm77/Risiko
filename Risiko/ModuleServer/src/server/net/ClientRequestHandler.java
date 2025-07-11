package server.net;

import common.enums.Commands;
import common.enums.Spielphase;
import common.exceptions.FalscherBesitzerException;
import common.exceptions.UngueltigeBewegungException;
import common.valueobjects.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class ClientRequestHandler implements Runnable {
    private final InputStream socketIn;
    private final OutputStream socketOut;

    private final String separator = "%";
    ISpiel spiel;

    private void writeString(String cmd){
        PrintStream socketOutPrint = new PrintStream(socketOut);
        System.out.println("Gesendete Daten: " + cmd);
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

    public ClientRequestHandler(Socket s, ISpiel spiel) throws IOException {
        this.spiel = spiel;
        socketIn = s.getInputStream();
        socketOut = s.getOutputStream();
    }

    @Override
    public void run() {
        while(true) {
            try {
                BufferedReader socketInString = new BufferedReader(new InputStreamReader(socketIn));
                String receivedData = socketInString.readLine();
                decipherRequest(receivedData);
            } catch (SocketException e) {
                System.err.println("Client hat Verbindung geschlossen");
                break;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } catch (FalscherBesitzerException | UngueltigeBewegungException  e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void decipherRequest(String message) throws FalscherBesitzerException, UngueltigeBewegungException, IOException, ClassNotFoundException {
        System.out.println("Empfangene Daten: "+ message);
        String[] data = message.split(separator);

        switch (Commands.valueOf(data[0])){
            case CMD_GET_AKTUELLER_SPIELER -> handleAktuellerSpieler();
            case CMD_GET_WELT -> handleGetWeltObject();
            case CMD_GET_SPIELPHASE -> handleGetPhase();
            case CMD_SPIELE_KARTE -> handleSpieleKarte(data);
            case CMD_GET_MISS_BESCHREIBUNG -> handleBeschreibung(Integer.parseInt(data[1]));
            case CMD_GET_MISS_ERFUELLT -> handleErfuellt(Integer.parseInt(data[1]));
            case CMD_GET_MISS_PROGRESS -> handleProgress(Integer.parseInt(data[1]));

            case CMD_SET_SPIELERLISTE -> handleSetSpielerliste();
            case CMD_SET_PHASE -> handleSetPhase(data[1]);
            case CMD_WEISE_MISS_ZU -> handleMissionsZuweisung();

            case CMD_SPIEL_INIT -> handleInit();
            case CMD_NAECHSTE_PHASE -> handleNextPhase();
            case CMD_KAMPF -> handleKampf(data);
            case CMD_SPIEL_SPEICHERN -> handleSpeichern();

            default -> System.err.println("Ungueltige Anfrage empfangen: " + data[0]);
        }
    }

    private void handleAktuellerSpieler(){
        String resp = Commands.CMD_GET_AKTUELLER_SPIELER_RESP.name()+ separator + spiel.getAktuellerSpieler().getId();
        writeString(resp);
    }
    private void handleGetWeltObject() throws IOException {
        Welt welt = spiel.getWelt();
        writeObject(welt);
    }

    private void handleGetWeltPrint()  {
        Welt welt = spiel.getWelt();
        StringBuilder sb = new StringBuilder();
        sb.append(Commands.CMD_GET_WELT_RESP.name());

        //Spieler serialisieren
        sb.append(separator).append(welt.getSpielerListe().size());
        for(Spieler s : welt.getSpielerListe()){
            sb.append(separator).append(s.getId()).append(":").append(s.getName()).append(":").append(s.getFarbe() != null ? s.getFarbe() : "").append(":").append(s.isAlive());
        }

        //Kontinente serialisieren
        sb.append(separator).append(welt.alleKontinente.size());
        for(Kontinent k : welt.alleKontinente){
            sb.append(separator).append(k.getName()).append(":").append(String.valueOf(k.getBuff())).append(":");
            Land[] gebiete = k.gebiete;
            for (int i = 0; i<gebiete.length; i++){
                sb.append(gebiete[i].getId());
                if(i< gebiete.length -1) sb.append(",");
            }
        }

        //Länder serialisieren
        sb.append(separator).append(welt.getAlleLaender().size());
        for(Land l : welt.getAlleLaender()){
            sb.append(separator).append(l.getId()).append(":").append(l.getName()).append(":").append(l.getBesitzer() != null ? l.getBesitzer().getId() : -1).append(":").append(l.getEinheiten()).append(":").append(l.getFarbe()).append(":");
            int i = 0;
            for (Land n : l.getNachbarn()){
                sb.append(n.getId());
                if(++i < l.getNachbarn().size()) sb.append(",");
            }
        }

        writeString(String.valueOf(sb));
        System.out.println("Gesendete Welt: " + sb); //Will das nur mal anzeigen lassen zum testen
    }

    private void handleGetPhase(){
        String resp = Commands.CMD_GET_SPIELPHASE_RESP.name() + separator + spiel.getPhase().name();
        writeString(resp);
    }
    private void handleSpieleKarte(String[] info) throws IOException {
        int id = Integer.parseInt(info[1]);
        Spieler spieler = spiel.getWelt().findeSpieler(id);
        Karte karte = spieler.getKarten().stream().filter(karte1 -> karte1.getLand().getName().equals(info[2])).findAny().orElseThrow();
        int strength = spiel.spieleKarte(spieler, karte);

        String resp = Commands.CMD_SPIELE_KARTE_RESP.name() + separator + strength;
        writeString(resp);
    }
    private void handleBeschreibung(int spielerId) throws IOException {
        Spieler spieler = spiel.getWelt().findeSpieler(spielerId);
        String beschreibung = spiel.getMissionBeschreibung(spieler);

        String resp = Commands.CMD_GET_MISS_BESCHREIBUNG_RESP.name() + separator + beschreibung;
        writeString(resp);
    }
    private void handleErfuellt(int spielerId) throws IOException {
        Spieler spieler = spiel.getWelt().findeSpieler(spielerId);
        boolean erfuellt = spiel.hatMissionErfuellt(spieler);

        String resp = Commands.CMD_GET_MISS_ERFUELLT_RESP.name() + separator + erfuellt;
        writeString(resp);
    }
    private void handleProgress(int spielerId) throws IOException {
        Spieler spieler = spiel.getWelt().findeSpieler(spielerId);
        int fortschritt = spiel.getMissionProgress(spieler);

        String resp = Commands.CMD_GET_MISS_PROGRESS_RESP.name() + separator + fortschritt;
        writeString(resp);
    }

    private void handleSetSpielerliste() {
        writeString(Commands.CMD_SET_SPIELERLISTE_RESP.name());
        ArrayList<Spieler> spielerListe = (ArrayList<Spieler>) readObjectResponse();
        spiel.setSpielerliste(spielerListe);
    }

    private void handleSetPhase(String phase){
        spiel.setPhase(Spielphase.valueOf(phase));
        writeString(Commands.CMD_SET_PHASE_RESP.name());
    }
    private void handleMissionsZuweisung(){
        spiel.weiseMissionenZu();
        writeString(Commands.CMD_WEISE_MISS_ZU_RESP.name());
    }

    private void handleInit(){
        spiel.init();
        writeString(Commands.CMD_SPIEL_INIT_RESP.name());
    }
    private void handleNextPhase(){
        spiel.naechstePhase();
        writeString(Commands.CMD_NAECHSTE_PHASE_RESP.name());
    }
    private void handleKampf(String[] infos) throws FalscherBesitzerException, UngueltigeBewegungException, IOException {
        Land herkunft = spiel.getWelt().findeLand(Integer.parseInt(infos[1]));
        Land ziel = spiel.getWelt().findeLand(Integer.parseInt(infos[2]));
        int angreifendeTruppe = Integer.parseInt(infos[3]);
        int verteidigendeTruppe = Integer.parseInt(infos[4]);
        boolean erfolg = spiel.kampf(herkunft, ziel, angreifendeTruppe, verteidigendeTruppe);
        String resp = Commands.CMD_KAMPF_RESP.name() + separator + erfolg;
        writeString(resp);
    }
    private void handleSpeichern(){
        spiel.spielSpeichern();
    }
}
