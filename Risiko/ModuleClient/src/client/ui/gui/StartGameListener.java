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
    private final RisikoClient client;

    public StartGameListener(GuiMain gui, RisikoClient client) {
        this.gui = gui;
        this.client = client;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            ISpiel spiel = this.client;
            spiel.init();
            Spieler dieserSpieler = spiel.getSpielerListe().stream().filter(s -> s.getName().equals(client.getSpielerName())).findFirst().orElseGet(spiel::getAktuellerSpieler);
            SwingUtilities.invokeLater(()-> {
                try {
                    new SpielerFenster(spiel, dieserSpieler.toDTO());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                gui.dispose();
            });
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        }

    }
}
