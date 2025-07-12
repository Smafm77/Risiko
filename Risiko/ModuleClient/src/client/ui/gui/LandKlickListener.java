package client.ui.gui;

import common.exceptions.FalscherBesitzerException;
import common.exceptions.UngueltigeBewegungException;
import common.valueobjects.LandDTO;

public interface LandKlickListener {
    void landAngeklickt(LandDTO land) throws FalscherBesitzerException, UngueltigeBewegungException;
}
