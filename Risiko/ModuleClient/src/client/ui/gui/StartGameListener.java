package client.ui.gui;


import common.exceptions.FalscherBesitzerException;
import common.exceptions.UngueltigeBewegungException;
import server.domain.AktiverSpielerListener;
import server.domain.Spiel;
import common.enums.Spielphase;
import common.valueobjects.Spieler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class StartGameListener implements ActionListener {
    private final GuiMain gui;

    public StartGameListener(GuiMain gui) {
        this.gui = gui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Spiel spiel = Spiel.getInstance();
            spiel.getWelt().setSpielerListe(gui.getGuiSpieler());
            spiel.weiseMissionenZu();
            spiel.init();
            AktiverSpielerListener.fire(spiel.getAktuellerSpieler());
            for (Spieler s : gui.getGuiSpieler()){
                new SpielerFenster(spiel, s);
            }
            gui.dispose();

        } catch (RuntimeException | FalscherBesitzerException | UngueltigeBewegungException | IOException ex) {
        JOptionPane.showMessageDialog(null, ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
    }

    }
}
