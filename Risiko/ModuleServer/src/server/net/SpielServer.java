package server.net;

import server.domain.Spiel;
import server.persistence.SpielSpeichern;
import common.valueobjects.Spieler;
import server.net.ClientRequestHandler;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SpielServer {

    public static void startServer(ArrayList<Spieler> spielerListe, JLabel lblStatus) {
        int anzahlSpieler = spielerListe.size();

        try {
            Spiel spiel;
            boolean neu = true;
            if (neu) {//Neues Spiel
                spiel = Spiel.getInstance();
            } else {//Altes Spiel laden
                spiel = SpielSpeichern.laden("spielstand.risiko");
            }
            ServerSocket serverSocket = new ServerSocket(1399);
            if (lblStatus != null) {
                SwingUtilities.invokeLater(() -> lblStatus.setText("Warte auf " + anzahlSpieler + " Clients..."));
            }
            ArrayList<Socket> clients = new ArrayList<>();
            for (int i = 0; i < anzahlSpieler; i++) {
                Socket s = serverSocket.accept();
                clients.add(s);
                int clientNum = i +1;
                if (lblStatus != null) {
                    SwingUtilities.invokeLater(() -> lblStatus.setText(clientNum + "/" + anzahlSpieler + " Clients verbunden..."));
                }
            }
            if (lblStatus != null) {
                SwingUtilities.invokeLater(() -> lblStatus.setText("Alle Clients verbunden! Spiel startet..."));
            }

            for (Socket s : clients) {
                ClientRequestHandler c = new ClientRequestHandler(s, spiel);
                Thread t = new Thread(c);
                t.start();
            }
        } catch (IOException | ClassNotFoundException e) {
            if (lblStatus != null) {
                SwingUtilities.invokeLater(() -> lblStatus.setText("Serverfehler: " + e.getMessage()));
                e.printStackTrace();
            }
        }
    }

}
