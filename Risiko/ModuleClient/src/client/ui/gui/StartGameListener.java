package client.ui.gui;


import server.domain.AktiverSpielerListener;
import server.domain.Spiel;
import common.enums.Spielphase;
import common.valueobjects.Spieler;
import common.valueobjects.SpielerDTO;

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
            ISpiel spiel = new RisikoClient();
            spiel.setSpielerliste(gui.getGuiSpieler());
            spiel.weiseMissionenZu();
            spiel.init();
            //AktiverSpielerListener.fire(spiel.getAktuellerSpieler());
            for (Spieler s : spiel.getSpielerListe()){
                new SpielerFenster(spiel, s.toDTO());
            }
            gui.dispose();

        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.out.println(ex.getStackTrace());
            throw new RuntimeException(ex);
        }

    }
}
