package server.net;

import server.domain.Spiel;
import server.persistence.SpielSpeichern;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SpielServer {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Spiel spiel;
        boolean neu = true;
        if(neu){//Neues Spiel
            spiel = Spiel.getInstance();
        } else {//Altes Spiel laden
            spiel = SpielSpeichern.laden("spielstand.risiko");
        }


        ServerSocket serverSocket = new ServerSocket(1399);
        System.out.println("Server läuft und wartet auf eingehende Verbindungen!");

        while (true) {
            Socket s = serverSocket.accept();

            ClientRequestHandler c = new ClientRequestHandler(s, spiel);

            // Parallele Abarbeitung des Clients starten
            Thread t = new Thread(c);
            t.start();

            System.err.println("Client verbunden!");
        }
    }

}
