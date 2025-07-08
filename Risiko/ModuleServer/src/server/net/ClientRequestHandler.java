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

    private void decipherRequest(String message) throws FalscherBesitzerException, UngueltigeBewegungException, IOException {
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
    private void handleGetWelt() throws IOException {
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

        socketOut.println((sb));
        System.out.println("Gesendete Welt: " + sb); //Will das nur mal anzeigen lassen zum testen
    }

    private void handleGetPhase(){
        String resp = Commands.CMD_GET_SPIELPHASE_RESP.name() + separator + separator + spiel.getPhase().name();
        socketOut.println(resp);
    }
    private void handleSpieleKarte(String[] info) throws IOException {
        int id = Integer.parseInt(info[1]);
        Spieler spieler = spiel.getWelt().findeSpieler(id);
        Karte karte = spieler.getKarten().stream().filter(karte1 -> karte1.getLand().getName().equals(info[2])).findAny().orElseThrow();
        int strength = spiel.spieleKarte(spieler, karte);

        String resp = Commands.CMD_SPIELE_KARTE_RESP.name() + separator + strength;
        socketOut.println(resp);
    }
    private void handleBeschreibung(int spielerId) throws IOException {
        Spieler spieler = spiel.getWelt().findeSpieler(spielerId);
        String beschreibung = spiel.getMissionBeschreibung(spieler);

        String resp = Commands.CMD_GET_MISS_BESCHREIBUNG_RESP.name() + separator + beschreibung;
        socketOut.println(resp);
    }
    private void handleErfuellt(int spielerId) throws IOException {
        Spieler spieler = spiel.getWelt().findeSpieler(spielerId);
        boolean erfuellt = spiel.hatMissionErfuellt(spieler);

        String resp = Commands.CMD_GET_MISS_BESCHREIBUNG_RESP.name() + separator + erfuellt;
        socketOut.println(resp);
    }
    private void handleProgress(int spielerId) throws IOException {
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
    private void handleKampf(String[] infos) throws FalscherBesitzerException, UngueltigeBewegungException, IOException {
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
