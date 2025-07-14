package server.net;

import common.enums.Commands;
import common.enums.Spielphase;
import common.exceptions.FalscherBesitzerException;
import common.exceptions.UngueltigeBewegungException;
import common.valueobjects.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;

public class ClientRequestHandler implements Runnable {
    private final Spieler clientSpieler;
    private final InputStream socketIn;
    private final OutputStream socketOut;

    private final String separator = "%";
    ISpiel spiel;

    private void writeString(String cmd) {
        PrintStream socketOutPrint = new PrintStream(socketOut);
        System.out.println("Gesendete Daten: " + cmd);
        socketOutPrint.println(cmd);
    }

    private Object readObjectResponse() {
        Object resp;
        try {
            ObjectInputStream socketInObject = new ObjectInputStream(socketIn);
            resp = socketInObject.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return resp;
    }

    private void writeObject(Object obj) {
        try {
            ObjectOutputStream socketOutObject = new ObjectOutputStream(socketOut);
            socketOutObject.reset();
            socketOutObject.writeObject(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ClientRequestHandler(Socket s, ISpiel spiel, Spieler spieler) throws IOException {
        this.spiel = spiel;
        this.clientSpieler = spieler;
        socketIn = s.getInputStream();
        socketOut = s.getOutputStream();
    }

    @Override
    public void run() {
        try {
            BufferedReader socketInString = new BufferedReader(new InputStreamReader(socketIn));
            /*
            String spielerDaten = socketInString.readLine();
            if (spielerDaten == null || !spielerDaten.contains(",")) {
                System.err.println("Ungültige Login-Daten empfangen: " + spielerDaten);
                return;
            }
            String[] spielerParts = spielerDaten.split(",");
            String name = spielerParts[0].trim();
            String color = spielerParts[1].trim();

            for (Spieler s : spiel.getSpielerListe()) {
                if (s.getName().equalsIgnoreCase(name) && s.getFarbe().equalsIgnoreCase(color)) {
                    clientSpieler = s;
                    break;
                }
            }
            if (clientSpieler == null) {
                System.err.println("Spieler nicht gefunden für: " + name + "/" + color);
                PrintStream socketOutPrint = new PrintStream(socketOut);
                socketOutPrint.println("Fehler: Spieler nicht gefunden!");
                return;
            }
            PrintStream socketOutPrint = new PrintStream(socketOut);
            socketOutPrint.println("OK");*/

            while (true) {
                String receivedData = socketInString.readLine();
                if (receivedData == null) {
                    break;
                }
                decipherRequest(receivedData);
            }
        } catch (SocketException e) {
            System.err.println("Client hat Verbindung geschlossen");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void decipherRequest(String message) throws IOException, ClassNotFoundException {
        System.out.println("Empfangene Daten: " + message);
        String[] data = message.split(separator);

        switch (Commands.valueOf(data[0])) {
            case CMD_GET_AKTUELLER_SPIELER -> handleAktuellerSpieler();
            case CMD_GET_WELT -> handleGetWeltObject();
            case CMD_GET_SPIELPHASE -> handleGetPhase();
            case CMD_GET_SPIELER_EINHEITEN -> handleSpielerEinheiten(Integer.parseInt(data[1]));
            case CMD_GET_KARTEN -> handleGetKarten(Integer.parseInt(data[1]));
            case CMD_SPIELE_KARTE -> handleSpieleKarte(data);
            case CMD_GET_MISS_BESCHREIBUNG -> handleBeschreibung(Integer.parseInt(data[1]));
            case CMD_GET_MISS_ERFUELLT -> handleErfuellt(Integer.parseInt(data[1]));
            case CMD_GET_MISS_PROGRESS -> handleProgress(Integer.parseInt(data[1]));
            case CMD_GET_LANDBESITZER -> handleLandbesitzer(Integer.parseInt(data[1]));
            case CMD_GET_LANDTRUPPEN -> handleLandTruppen(Integer.parseInt(data[1]));

            case CMD_SET_SPIELERLISTE -> handleSetSpielerliste();
            case CMD_SET_PHASE -> handleSetPhase(data[1]);
            case CMD_WEISE_MISS_ZU -> handleMissionsZuweisung();
            case CMD_STATIONIEREN -> handleStationieren(data);

            case CMD_SPIEL_INIT -> handleInit();
            case CMD_NAECHSTE_PHASE -> handleNextPhase();
            case CMD_KAMPF -> handleKampf(data);
            case CMD_BEWEGE_EINHEITEN -> handleBewegeEinheiten(data);
            case CMD_SPIEL_SPEICHERN -> handleSpeichern();

            default -> System.err.println("Ungueltige Anfrage empfangen: " + data[0]);
        }
    }

    private void handleAktuellerSpieler() {
        String resp = Commands.CMD_GET_AKTUELLER_SPIELER_RESP.name() + separator + spiel.getAktuellerSpieler().getId();
        writeString(resp);
    }

    private void handleGetWeltObject() throws IOException {
        Welt welt = spiel.getWelt();
        writeObject(welt);
    }

    private void handleGetWeltPrint() {
        Welt welt = spiel.getWelt();
        StringBuilder sb = new StringBuilder();
        sb.append(Commands.CMD_GET_WELT_RESP.name());

        //Spieler serialisieren
        sb.append(separator).append(welt.getSpielerListe().size());
        for (Spieler s : welt.getSpielerListe()) {
            sb.append(separator).append(s.getId()).append(":").append(s.getName()).append(":").append(s.getFarbe() != null ? s.getFarbe() : "").append(":").append(s.isAlive());
        }

        //Kontinente serialisieren
        sb.append(separator).append(welt.alleKontinente.size());
        for (Kontinent k : welt.alleKontinente) {
            sb.append(separator).append(k.getName()).append(":").append(String.valueOf(k.getBuff())).append(":");
            Land[] gebiete = k.gebiete;
            for (int i = 0; i < gebiete.length; i++) {
                sb.append(gebiete[i].getId());
                if (i < gebiete.length - 1) sb.append(",");
            }
        }

        //Länder serialisieren
        sb.append(separator).append(welt.getAlleLaender().size());
        for (Land l : welt.getAlleLaender()) {
            sb.append(separator).append(l.getId()).append(":").append(l.getName()).append(":").append(l.getBesitzer() != null ? l.getBesitzer().getId() : -1).append(":").append(l.getEinheiten()).append(":").append(l.getFarbe()).append(":");
            int i = 0;
            for (Land n : l.getNachbarn()) {
                sb.append(n.getId());
                if (++i < l.getNachbarn().size()) sb.append(",");
            }
        }

        writeString(String.valueOf(sb));
        System.out.println("Gesendete Welt: " + sb); //Will das nur mal anzeigen lassen zum testen
    }

    private void handleGetPhase() {
        String resp = Commands.CMD_GET_SPIELPHASE_RESP.name() + separator + spiel.getPhase().name();
        writeString(resp);
    }

    private void handleSpielerEinheiten(int spielerId) {
        String resp = Commands.CMD_GET_SPIELER_EINHEITEN_RESP.name() + separator + spiel.berechneSpielerEinheiten(spielerId);
        writeString(resp);
    }

    private void handleGetKarten(int spielerId) {
        HashSet<Karte> karten = spiel.getSpielerKarten(spielerId);
        writeObject(karten);
    }

    private void handleSpieleKarte(String[] info) {
        int spielerId = Integer.parseInt(info[1]);
        Spieler spieler = spiel.getWelt().findeSpieler(spielerId);
        Karte karte = spieler.getKarten().stream().filter(karte1 -> karte1.getLand().getName().equals(info[2])).findAny().orElseThrow();
        int strength = spiel.spieleKarte(spielerId, karte);

        String resp = Commands.CMD_SPIELE_KARTE_RESP.name() + separator + strength;
        writeString(resp);
    }

    private void handleBeschreibung(int spielerId) {
        String beschreibung = spiel.getMissionBeschreibung(spielerId);

        String resp = Commands.CMD_GET_MISS_BESCHREIBUNG_RESP.name() + separator + beschreibung;
        writeString(resp);
    }

    private void handleErfuellt(int spielerId) {
        boolean erfuellt = spiel.hatMissionErfuellt(spielerId);

        String resp = Commands.CMD_GET_MISS_ERFUELLT_RESP.name() + separator + erfuellt;
        writeString(resp);
    }

    private void handleProgress(int spielerId) {
        int fortschritt = spiel.getMissionProgress(spielerId);

        String resp = Commands.CMD_GET_MISS_PROGRESS_RESP.name() + separator + fortschritt;
        writeString(resp);
    }

    public void handleLandbesitzer(int landId) {
        Spieler spieler = spiel.getLandbesitzer(landId);
        writeObject(spieler);
    }

    public void handleLandTruppen(int landId) {
        String resp = Commands.CMD_GET_LANDTRUPPEN_RESP.name() + separator + spiel.getLandTruppen(landId);
        writeString(resp);
    }

    private void handleSetSpielerliste() {
        writeString(Commands.CMD_SET_SPIELERLISTE_RESP.name());
        ArrayList<Spieler> spielerListe = (ArrayList<Spieler>) readObjectResponse();
        spiel.setSpielerliste(spielerListe);
    }

    private void handleSetPhase(String phase) {
        spiel.setPhase(Spielphase.valueOf(phase));
        writeString(Commands.CMD_SET_PHASE_RESP.name());
    }

    private void handleMissionsZuweisung() {
        spiel.weiseMissionenZu();
        writeString(Commands.CMD_WEISE_MISS_ZU_RESP.name());
    }

    private void handleStationieren(String[] info) {
        int stationId = Integer.parseInt(info[1]);
        int truppen = Integer.parseInt(info[2]);
        spiel.einheitenStationieren(stationId, truppen);
        writeString(Commands.CMD_STATIONIEREN_RESP.name() + separator + spiel.getWelt().findeLand(stationId).getEinheiten());
    }

    private void handleInit() {
        spiel.init();
        writeString(Commands.CMD_SPIEL_INIT_RESP.name()+"%ClientRequestHandler");
    }

    private void handleNextPhase() {
        spiel.naechstePhase();
        writeString(Commands.CMD_NAECHSTE_PHASE_RESP.name());
    }

    private void handleKampf(String[] infos) {
        int herkunftId = Integer.parseInt(infos[1]);
        int zielId = Integer.parseInt(infos[2]);
        int angreifendeTruppe = Integer.parseInt(infos[3]);
        int verteidigendeTruppe = Integer.parseInt(infos[4]);
        boolean erfolg = false;
        try {
            erfolg = spiel.kampf(herkunftId, zielId, angreifendeTruppe, verteidigendeTruppe);
        } catch (UngueltigeBewegungException e) {
            writeString(Commands.EX_FALSCHE_BEWEGUNG.name() + separator + e.getMessage());
            return;
        } catch (FalscherBesitzerException e) {
            writeString(Commands.EX_FALSCHER_BESITZER.name() + separator + e.getMessage());
            return;
        }
        String resp = Commands.CMD_KAMPF_RESP.name() + separator + erfolg;
        writeString(resp);
    }

    private void handleBewegeEinheiten(String[] infos) {
        int spielerId = Integer.parseInt(infos[1]);
        int truppen = Integer.parseInt(infos[2]);
        int herkunftId = Integer.parseInt(infos[3]);
        int zielId = Integer.parseInt(infos[4]);

        try {
            spiel.bewegeEinheiten(spielerId, truppen, herkunftId, zielId);
        } catch (UngueltigeBewegungException e) {
            writeString(Commands.EX_FALSCHE_BEWEGUNG.name() + separator + e.getMessage());
            return;
        } catch (FalscherBesitzerException e) {
            writeString(Commands.EX_FALSCHER_BESITZER.name() + separator + e.getMessage());
            return;
        }
        writeString(Commands.CMD_BEWEGE_EINHEITEN_RESP.name());
    }

    private void handleSpeichern() {
        spiel.spielSpeichern();
    }
}
