package client.ui.gui;

import common.exceptions.FalscherBesitzerException;
import common.exceptions.UngueltigeBewegungException;
import common.valueobjects.Land;

public interface LandKlickListener {
    void landAngeklickt(Land land) throws FalscherBesitzerException, UngueltigeBewegungException;
}
