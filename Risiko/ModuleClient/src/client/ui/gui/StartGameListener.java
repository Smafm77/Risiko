package client.ui.gui;


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
            spiel.init();
            AktiverSpielerListener.fire(spiel.getAktuellerSpieler());
            for (Spieler s : gui.getGuiSpieler()){
                new SpielerFenster(spiel, s);
            }
            gui.dispose();

        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }
}
