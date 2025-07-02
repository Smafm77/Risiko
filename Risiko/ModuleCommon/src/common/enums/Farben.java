package common.enums;

import java.awt.*;

public enum Farben {
    ROT(Color.decode("#cc0000")),
    BLAU(Color.decode("#001dff")),
    GELB(Color.decode("#fff700")),
    GRUEN(Color.decode("#1bff00")),
    ORANGE(Color.decode("#ff8300")),
    VIOLETT(Color.decode("#740ece"));

    private final Color farbe;

    Farben(Color farbe) { this.farbe = farbe; }

}
