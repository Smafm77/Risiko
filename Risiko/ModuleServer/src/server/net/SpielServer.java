package server.net;

import common.valueobjects.Spieler;
import server.domain.Spiel;
import server.persistence.SpielSpeichern;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class SpielServer {
    private static final List<String> Farben = Arrays.asList("Rot", "Blau", "Gruen", "Gelb", "Orange", "Violett");

    public static void startServer(boolean spielLaden, int spielerListe, JLabel lblStatus) {

        int spielerAnzahl = spielerListe;
        try (ServerSocket serverSocket = new ServerSocket(1399)) {
            Spiel spiel;
            if (spielLaden) {//Neues Spiel
                spiel = Spiel.getInstance();

            } else {//Altes Spiel laden
                spiel = SpielSpeichern.laden("spielstand.risiko");
            }
            ArrayList<Socket> clientSockets = new ArrayList<>();
            ArrayList<String> spielerNamen = new ArrayList<>();
            ArrayList<String> spielerColor = new ArrayList<>();
            updateLabel(lblStatus, "Warte auf " + spielerListe + " Clients...");

            if (spielLaden) {
                ArrayList <Spieler> domainSpieler = spiel.getSpielerListe();
                for (Spieler sp : domainSpieler) {
                    spielerNamen.add(sp.getName());
                    spielerColor.add(sp.getFarbe());
                }
                spielerAnzahl = spielerNamen.size();
            }

            int verbunden = 0;

            while (verbunden < spielerAnzahl) {
                Socket s = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                String name = in.readLine();
                if (name == null || name.isBlank()) {
                    out.println("Error: Ungülter Name");
                    s.close();
                    continue;
                }
                if (spielerNamen.contains(name)) {
                    out.println("Error: Name bereits vergeben");
                    s.close();
                    continue;
                }
                String color = spielLaden ? spielerColor.get(spielerNamen.indexOf(name)) : Farben.get(spielerNamen.size());
                spielerNamen.add(name);
                spielerColor.add(color);

                out.println("OK:" + color);

                clientSockets.add(s);
                verbunden++;
                updateLabel(lblStatus, verbunden + "/" + spielerAnzahl+ " Clients verbunden...");
            }

            updateLabel(lblStatus, "Alle Clients verbunden. Spiel wird gestartet...");

            if(!spielLaden){
                ArrayList <Spieler> domainSpieler = spiel.getSpielerListe();
                for (int i = 0; i< spielerNamen.size(); i++) {
                    String name = spielerNamen.get(i);
                    String color = spielerColor.get(i);
                    domainSpieler.add(new Spieler(name, color));
                }
                spiel.setSpielerliste(domainSpieler);
            }
            spiel.weiseMissionenZu();
            spiel.init();

            for(Socket s : clientSockets){
                try{
                    PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                    out.println("CMD_START");
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            updateLabel(lblStatus, "Startsignal gesendet - Spiel läuft");

            for(Socket s : clientSockets){
                new Thread(new ClientRequestHandler(s, spiel)).start();
            }

        } catch (IOException | ClassNotFoundException e) {
            if (lblStatus != null) {
                SwingUtilities.invokeLater(() -> lblStatus.setText("Serverfehler: " + e.getMessage()));
                e.printStackTrace();
            }
        }
    }

    private static void updateLabel(JLabel lbl, String text) {
        if (lbl != null) {
            SwingUtilities.invokeLater(() -> lbl.setText(text));
        }
    }



}
