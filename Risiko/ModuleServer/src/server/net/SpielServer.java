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


        try (ServerSocket serverSocket = new ServerSocket(1399)) {
            Spiel spiel;
            int spielerAnzahl;
            Map<String, Spieler> gespeicherteSpieler = null;
            if (!spielLaden) {
                spiel = Spiel.getInstance();
                spielerAnzahl = anzahlGewaehlt;

            } else {
                spiel = SpielSpeichern.laden("spielstand.risiko");
                gespeicherteSpieler = new HashMap<>();
                spielerAnzahl = spiel.getWelt().getSpielerListe().size();
                for (Spieler sp : spiel.getWelt().getSpielerListe()) {
                    gespeicherteSpieler.put(sp.getName(), sp);
                }
            }

            ArrayList<Socket> clientSockets = new ArrayList<>();
            ArrayList<String> spielerNamen = new ArrayList<>();
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
                if (spielLaden) {
                    if (!gespeicherteSpieler.containsKey(name)) {
                        out.println("Error: Spieler unbekannt");
                        s.close();
                        continue;
                    }
                    Spieler spieler = gespeicherteSpieler.get(name);
                    spielerNamen.add(name);
                    spielerListe.add(spieler);
                    out.println("OK:" + spieler.getFarbe());
                } else {
                    String color = Farben.get(spielerNamen.size());
                    Spieler spieler = new Spieler(name, color);
                    spielerNamen.add(name);
                    spielerListe.add(spieler);
                    out.println("OK:" + color);
                }
                clientSockets.add(s);
                verbunden++;
                updateLabel(lblStatus, verbunden + "/" + spielerAnzahl + " Clients verbunden...");
            }

            updateLabel(lblStatus, "Alle Clients verbunden. Spiel wird gestartet...");
            if (!spielLaden) {
                spiel.getWelt().setSpielerListe(spielerListe);
            }
            updateLabel(lblStatus, "Startsignal gesendet - Spiel läuft");

            ArrayList<ClientRequestHandler> clientRequestHandlers = new ArrayList<>();
            for (int i = 0; i < spielerAnzahl; i++) {
                new Thread(new ClientRequestHandler(clientSockets.get(i), spiel, spielerListe.get(i), clientRequestHandlers)).start();
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
