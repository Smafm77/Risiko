package server.net;

import common.enums.Commands;
import common.enums.Spielphase;
import common.exceptions.FalscherBesitzerException;
import common.exceptions.UngueltigeBewegungException;
import common.valueobjects.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ClientRequestHandler implements Runnable {
    private PrintStream socketOut;
    private BufferedReader socketIn;
    private final String separator = "%";
    ISpiel spiel;

    public ClientRequestHandler(Socket s, ISpiel spiel) throws IOException {
        this.spiel = spiel;
        socketIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
        socketOut = new PrintStream(s.getOutputStream());
    }

    @Override
    public void run() {
        while(true) {
            try {
                String receivedData = socketIn.readLine();
                decipherRequest(receivedData);
            } catch (SocketException e) {
                System.err.println("Client hat Verbindung geschlossen");
                break;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FalscherBesitzerException | UngueltigeBewegungException  e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void decipherRequest(String message) throws FalscherBesitzerException, UngueltigeBewegungException {
        System.out.println("Empfangene Daten: "+ message);
        String[] data = message.split(separator);

        switch (Commands.valueOf(data[0])){
            case CMD_GET_AKTUELLER_SPIELER -> handleAktuellerSpieler();
            case CMD_GET_WELT -> handleGetWelt();
            case CMD_GET_SPIELPHASE -> handleGetPhase();
            case CMD_SPIELE_KARTE -> handleSpieleKarte(data);
            case CMD_GET_MISS_BESCHREIBUNG -> handleBeschreibung(Integer.parseInt(data[1]));
            case CMD_GET_MISS_ERFUELLT -> handleErfuellt(Integer.parseInt(data[1]));
            case CMD_GET_MISS_PROGRESS -> handleProgress(Integer.parseInt(data[1]));

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
        socketOut.println(resp);
    }
    private void handleGetWelt(){
        Welt welt = spiel.getWelt(); //ToDo figure this out, notfalls mit ObjectIn/OutputStreams
    }
    private void handleGetPhase(){
        String resp = Commands.CMD_GET_SPIELPHASE_RESP.name() + separator + separator + spiel.getPhase().name();
        socketOut.println(resp);
    }
    private void handleSpieleKarte(String[] info){
        int id = Integer.parseInt(info[1]);
        Spieler spieler = spiel.getWelt().findeSpieler(id);
        Karte karte = spieler.getKarten().stream().filter(karte1 -> karte1.getLand().getName().equals(info[2])).findAny().orElseThrow();
        int strength = spiel.spieleKarte(spieler, karte);

        String resp = Commands.CMD_SPIELE_KARTE_RESP.name() + separator + strength;
        socketOut.println(resp);
    }
    private void handleBeschreibung(int spielerId){
        Spieler spieler = spiel.getWelt().findeSpieler(spielerId);
        String beschreibung = spiel.getMissionBeschreibung(spieler);

        String resp = Commands.CMD_GET_MISS_BESCHREIBUNG_RESP.name() + separator + beschreibung;
        socketOut.println(resp);
    }
    private void handleErfuellt(int spielerId){
        Spieler spieler = spiel.getWelt().findeSpieler(spielerId);
        boolean erfuellt = spiel.hatMissionErfuellt(spieler);

        String resp = Commands.CMD_GET_MISS_BESCHREIBUNG_RESP.name() + separator + erfuellt;
        socketOut.println(resp);
    }
    private void handleProgress(int spielerId){
        Spieler spieler = spiel.getWelt().findeSpieler(spielerId);
        int fortschritt = spiel.getMissionProgress(spieler);

        String resp = Commands.CMD_GET_MISS_BESCHREIBUNG_RESP.name() + separator + fortschritt;
        socketOut.println(resp);
    }

    private void handleSetPhase(String phase){
        spiel.setPhase(Spielphase.valueOf(phase));
    }
    private void handleMissionsZuweisung(){
        spiel.weiseMissionenZu();
    }

    private void handleInit(){
        spiel.init();
    }
    private void handleNextPhase(){
        spiel.naechstePhase();
    }
    private void handleKampf(String[] infos) throws FalscherBesitzerException, UngueltigeBewegungException {
        Land herkunft = spiel.getWelt().findeLand(Integer.parseInt(infos[1]));
        Land ziel = spiel.getWelt().findeLand(Integer.parseInt(infos[2]));
        int angreifendeTruppe = Integer.parseInt(infos[3]);
        int verteidigendeTruppe = Integer.parseInt(infos[4]);
        boolean erfolg = spiel.kampf(herkunft, ziel, angreifendeTruppe, verteidigendeTruppe);
        String resp = Commands.CMD_KAMPF_RESP.name() + separator + erfolg;
        socketOut.println(resp);
    }
    private void handleSpeichern(){
        spiel.spielSpeichern();
    }
}
