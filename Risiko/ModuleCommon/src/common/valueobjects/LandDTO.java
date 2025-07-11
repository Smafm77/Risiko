package common.valueobjects;

public class LandDTO {
    private final int id;
    private final String name;
    private final int farbe;


    public LandDTO(Land land) {
        this.id = land.getId();
        this.name = land.getName();
        this.farbe = land.getFarbe();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getFarbe() {
        return farbe;
    }


    @Override
    public boolean equals(Object land) {
        return ((land instanceof Land) && (((Land) land).getId() == (this.id))) || ((land instanceof LandDTO) && (((LandDTO) land).getId() == (this.id)));
    }


}
