package client.ui.gui;

import server.domain.Spiel;
import common.enums.Spielphase;
import server.persistence.SpielSpeichern;
import common.valueobjects.Spieler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class LoadGameListener implements ActionListener {
    private final GuiMain gui;
    private ArrayList<Spieler> spielerListe;
    private Spieler aktuellerSpieler;
    private Spielphase phase;

    LoadGameListener(GuiMain gui) {
        this.gui = gui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Spielstand laden");
        int result = chooser.showOpenDialog(gui);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                Spiel geladen = SpielSpeichern.laden(file.getAbsolutePath());
                gui.setSpiel(geladen);
                this.spielerListe = geladen.getSpielerListe();
                this.aktuellerSpieler = geladen.getAktuellerSpieler();
                this.phase = geladen.getPhase();

                for (Spieler s : geladen.getSpielerListe()) {
                    new SpielerFenster(geladen, s);
                }
                gui.setVisible(false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(gui, "Fehler beim Laden!! Cause:" + ex.getCause());
                System.out.println("Source: " + Arrays.toString(ex.getStackTrace()));
            }
        }
    }

}
