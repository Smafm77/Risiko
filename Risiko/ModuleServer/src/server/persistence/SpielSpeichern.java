package server.persistence;

import server.domain.Spiel;

import java.io.*;

public class SpielSpeichern implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static void speichern(Spiel spiel, String dateiname) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dateiname))) {
            oos.writeObject(spiel);
        }
    }

    public static Spiel laden(String dateiname) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dateiname))) {
            return (Spiel) ois.readObject();
        }
    }
}
