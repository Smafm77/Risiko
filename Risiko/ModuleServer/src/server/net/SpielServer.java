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

    public static void startServer(boolean spielLaden, int anzahlGewaehlt, JLabel lblStatus) {

        int spielerAnzahl = anzahlGewaehlt;
        try (ServerSocket serverSocket = new ServerSocket(1399)) {
            Spiel spiel;
            if (!spielLaden) {//Neues Spiel
                spiel = Spiel.getInstance();

            } else {//Altes Spiel laden
                spiel = SpielSpeichern.laden("spielstand.risiko");
            }

            ArrayList<Socket> clientSockets = new ArrayList<>();
            ArrayList<String> spielerNamen = new ArrayList<>();
            ArrayList<String> spielerColor = new ArrayList<>();
            ArrayList<Spieler> spielerListe = new ArrayList<>();
            updateLabel(lblStatus, "Warte auf " + anzahlGewaehlt + " Clients...");

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
                String color = Farben.get(spielerNamen.size());
                spielerNamen.add(name);
                spielerColor.add(color);
                Spieler spieler = new Spieler(name, color);
                spielerListe.add(spieler);
                out.println("OK:" + color);

                clientSockets.add(s);
                verbunden++;
                updateLabel(lblStatus, verbunden + "/" + spielerAnzahl+ " Clients verbunden...");
            }

            updateLabel(lblStatus, "Alle Clients verbunden. Spiel wird gestartet...");
            spiel.getWelt().setSpielerListe(spielerListe);

            for(Socket s : clientSockets){
                try{
                    PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                    out.println("CMD_SPIEL_INIT_RESP" + "%FromSpielServer");
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            updateLabel(lblStatus, "Startsignal gesendet - Spiel läuft");


            for (int i = 0; i < spielerAnzahl; i++){
                new Thread(new ClientRequestHandler(clientSockets.get(i), spiel, spielerListe.get(i))).start();
            }
            /*for(Socket s : clientSockets){
                new Thread(new ClientRequestHandler(s, spiel)).start();
            }*/

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
