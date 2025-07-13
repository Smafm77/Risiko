package server.net;

import server.domain.Spiel;
import server.persistence.SpielSpeichern;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpielServer {
    private static final List<String> Farben = Arrays.asList("Rot", "Blau", "Gruen", "Gelb", "Orange", "Violett");

    public static void startServer(int spielerListe, JLabel lblStatus) {
        int anzahlSpieler = spielerListe;

        try (ServerSocket serverSocket = new ServerSocket(1399)){
            Spiel spiel;
            boolean neu = true;
            if (neu) {//Neues Spiel
                spiel = Spiel.getInstance();
            } else {//Altes Spiel laden
                spiel = SpielSpeichern.laden("spielstand.risiko");
            }
            ArrayList<Socket> clientSockets = new ArrayList<>();
            ArrayList<String> spielerNamen = new ArrayList<>();
            ArrayList<String> spielerColor = new ArrayList<>();

            updateLabel(lblStatus, "Warte auf " + anzahlSpieler + " Clients...");

            int verbunden = 0;

            while(verbunden < anzahlSpieler) {
                Socket s = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                String name = in.readLine();
                if (name == null || name.isBlank()) {
                   out.println("Error: Ungülter Name");
                   s.close();
                   continue;
                }
                if (spielerNamen.contains(name)){
                    out.println("Error: Name bereits vergeben");
                    s.close();
                    continue;
                }
                String color = Farben.get(spielerNamen.size());
                spielerNamen.add(name);
                spielerColor.add(color);

                out.println("OK:"+color);

                clientSockets.add(s);
                verbunden++;
                updateLabel(lblStatus, verbunden  + "/" + anzahlSpieler + " Clients verbunden...");
            }

        } catch (IOException | ClassNotFoundException e) {
            if (lblStatus != null) {
                SwingUtilities.invokeLater(() -> lblStatus.setText("Serverfehler: " + e.getMessage()));
                e.printStackTrace();
            }
        }
    }

    private static void updateLabel(JLabel lbl, String text){
        if(lbl != null){
            SwingUtilities.invokeLater(()-> lbl.setText(text));
        }
    }

    private int farbeZuId(String color){
        return switch (color) {
            case "Rot" -> 1;
            case "Blau" -> 2;
            case "Gruen" -> 3;
            case "Gelb" -> 4;
            case "Orange" -> 5;
            case "Violett" -> 6;
            default -> 0;
        };
    }

}
