package domain;

import valueobjects.Spieler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface AktiverSpielerListener {
    void onAktiverSpielerGeaendert(Spieler neu);

    List<AktiverSpielerListener> LIST = new CopyOnWriteArrayList<>();

    static void add(AktiverSpielerListener l) {
        LIST.add(l);
    }

    static void fire(Spieler neu) {
        for (var l : LIST) l.onAktiverSpielerGeaendert(neu);
    }
}
