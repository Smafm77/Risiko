public class Kontinent {
    public Land[] gebiete;
    public int buff;
    public Spieler einzigerBesitzer; //Kann null sein, falls Kontinent mehrere Besatzer hat

    public Kontinent(Land[] gebiete, int buff) {
        this.gebiete = gebiete;
        this.buff = buff;
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
