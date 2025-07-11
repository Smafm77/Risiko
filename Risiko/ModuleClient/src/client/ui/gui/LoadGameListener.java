package client.ui.gui;

import common.exceptions.FalscherBesitzerException;
import common.exceptions.UngueltigeBewegungException;
import server.domain.Spiel;
import server.persistence.SpielSpeichern;
import common.valueobjects.Spieler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;

public class LoadGameListener implements ActionListener {
    private final GuiMain gui;

    LoadGameListener(GuiMain gui) {
        this.gui = gui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            Spiel geladen = SpielSpeichern.laden("spielstand.risiko");
            for (Spieler s : geladen.getSpielerListe()) {
                try {
                    new SpielerFenster(geladen, s);
                } catch (RuntimeException | FalscherBesitzerException | UngueltigeBewegungException | IOException
                        ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
                }
            }
            gui.setVisible(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(gui, "Fehler beim Laden!! Cause:" + ex.getCause() + ex.getMessage());
            System.out.println("Source: " + Arrays.toString(ex.getStackTrace()));
        }
    }
}


