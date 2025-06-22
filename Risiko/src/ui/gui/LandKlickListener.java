package ui.gui;

import exceptions.FalscherBesitzerException;
import exceptions.UngueltigeBewegungException;
import valueobjects.Land;

public interface LandKlickListener {
    void landAngeklickt(Land land) throws FalscherBesitzerException, UngueltigeBewegungException;
}
