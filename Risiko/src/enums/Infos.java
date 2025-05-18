package enums;

public enum Infos { //Eigenes Enum
    BESITZER(1),
    EINHEITEN(2),
    NACHBARN(3),
    ZURUECK(4);

    private final int auswahl;

    Infos(int auswahl) {
        this.auswahl = auswahl;
    }

    public int getAuswahl() {
        return auswahl;
    }

    public static Infos fromInt(int auswahl) {
        for (Infos i : values()) {
            if (i.getAuswahl() == auswahl) {
                return i;
            }
        }
        return null;
    }
}