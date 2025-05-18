package enums;


public enum Befehl {
    ANGRIFF(1),
    BEWEGEN(2),
    INFO(3),
    UEBERSICHT(4),
    ZUGBEENDEN(5);

    private final int auswahl;

    Befehl(int auswahl) {
        this.auswahl = auswahl;
    }

    public int getAuswahl() {
        return auswahl;
    }

    public static Befehl fromInt(int auswahl) {
        for (Befehl b : values()) {
            if (b.getAuswahl() == auswahl) {
                return b;
            }
        }
        return null;
    }
}

