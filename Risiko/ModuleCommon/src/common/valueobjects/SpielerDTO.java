package common.valueobjects;

public class SpielerDTO {
    private final String name;
    private final String farbe;
    private final int id;

    public SpielerDTO(Spieler spieler) {
        this.name = spieler.getName();
        this.farbe = spieler.getFarbe();
        this.id = spieler.getId();
    }

    public String getName() {
        return name;
    }

    public String getFarbe() {
        return farbe;
    }

    public int getId() {
        return id;
    }
    @Override
    public boolean equals(Object spieler) {
        return ((spieler instanceof Spieler) && ((Spieler) spieler).getId() == this.id) || ((spieler instanceof SpielerDTO) && ((SpielerDTO) spieler).id == this.id);
    }
}
