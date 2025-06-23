package ui.gui;


import domain.AktiverSpielerListener;
import domain.Spiel;
import enums.Spielphase;
import ui.Risiko;
import valueobjects.Spieler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
