package server.net;


import common.valueobjects.ISpiel;
import server.domain.Spiel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SpielServer {
    public static void main(String[] args) throws IOException {
        Spiel spiel = Spiel.getInstance();

        ServerSocket serverSocket = new ServerSocket(1399);
        System.out.println("Server laeuft und wartet auf eingehende Verbindungen!");

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
