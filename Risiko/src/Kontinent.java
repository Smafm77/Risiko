public class Kontinent {
    private Land[] gebiete;
    private int buff;
    private Spieler einzigerBesitzer; //Kann null sein, falls Kontinent mehrere Besatzer hat

    public Kontinent(Land[] gebiete, int buff) {
        this.gebiete = gebiete;
        this.buff = buff;
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

    public void checkBesitzer(){
        boolean isSame = true;
        for (int i = 1; i < gebiete.length; i++){
            if (!gebiete[i].besitzer.equals(gebiete[0].besitzer)) {
                isSame = false;
                break;
            }
        }
        if (isSame){
            einzigerBesitzer = gebiete[0].besitzer;
        } else {
            einzigerBesitzer = null;
        }
    }
    //ToDO Update Kontinentbesitzer bei jedem Besitzwechsel von zugehörigem Land
    // -->boolenrückgabe bei Änderung möglich einzufügen
}
