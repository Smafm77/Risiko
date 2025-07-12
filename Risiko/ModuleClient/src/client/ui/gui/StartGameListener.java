package client.ui.gui;


import client.net.RisikoClient;
import common.valueobjects.ISpiel;
import common.valueobjects.Spieler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;

public class StartGameListener implements ActionListener {
    private final GuiMain gui;

    public StartGameListener(GuiMain gui) {
        this.gui = gui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            ISpiel spiel = new RisikoClient();

            spiel.weiseMissionenZu();
            spiel.init();
            AktiverSpielerListener.fire(spiel.getAktuellerSpieler());
            for (Spieler s : spiel.getSpielerListe()){
                new SpielerFenster(spiel, s.toDTO());
            }
            gui.dispose();

        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.out.println(Arrays.toString(ex.getStackTrace()));
            throw new RuntimeException(ex);
        }

    }
}
