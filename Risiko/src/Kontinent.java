public class Kontinent {
    private String name;
    private Land[] gebiete;
    private int buff;
    private Spieler einzigerBesitzer; //Kann null sein, falls Kontinent mehrere Besatzer hat

    public Kontinent(String name, Land[] gebiete, int buff) {
        this.name = name;
        this.gebiete = gebiete;
        this.buff = buff;
        einzigerBesitzer = null;
    }
    public Land[] getGebiete() {
        return gebiete;
    }
    public int getBuff() {
        return buff;
    }
    public Spieler getEinzigerBesitzer() {
        return einzigerBesitzer;
    }
    public String getName() {
        return name;
    }

    public boolean beinhaltetLand(Land land){
        for (Land landK : gebiete){
            if (landK.equals(land)){
                return true;
            }
        }
        return false;
    }

    public void checkBesitzer(){
        boolean isSame = true;
        for (int i = 1; i < gebiete.length; i++){
            if (!gebiete[i].getBesitzer().equals(gebiete[0].getBesitzer())) {
                isSame = false;
                break;
            }
        }
        if (isSame){
            einzigerBesitzer = gebiete[0].getBesitzer();
        } else {
            einzigerBesitzer = null;
        }
    }
    //ToDO Update Kontinentbesitzer bei jedem Besitzwechsel von zugehörigem Land
    // -->boolenrückgabe bei Änderung möglich einzufügen
}
