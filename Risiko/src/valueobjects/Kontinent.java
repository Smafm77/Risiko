package valueobjects;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

public class Kontinent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String name;
    private final Land[] gebiete;
    private final int buff;

    public Kontinent(String name, Land[] gebiete, int buff) {
        this.name = name;
        this.gebiete = gebiete;
        this.buff = buff;
    }

    //region getter

    public int getBuff() {
        return buff;
    }

    public String getName() {
        return name;
    }

    public Spieler getEinzigerBesitzer() throws NullPointerException { //Nicht sicher ob es das ist da es nicht fälschlich versucht auf null zuzugreifen, aber es gibt null zurück und damit sind alle Aufrufe hiervon von Nullpointerexceptions bedroht
        boolean isSame = true;
        for (int i = 1; i < gebiete.length; i++) {
            if (!gebiete[i].getBesitzer().equals(gebiete[0].getBesitzer())) {
                isSame = false;
                break;
            }
        }
        if (isSame) {
            return gebiete[0].getBesitzer();
        } else {
            return null;
        }
    }

    public int getBesetzungsFortschritt(Spieler spieler, int bruchteil){
        return Math.toIntExact(Arrays.stream(gebiete).filter(land -> land.getBesitzer().equals(spieler)).count()) * bruchteil / gebiete.length;
    }
    //endregion

    //region setter
    public boolean beinhaltetLand(Land land) {
        for (Land landK : gebiete) {
            if (landK.equals(land)) {
                return true;
            }
        }
        return false;
    }
    //endregion
}
