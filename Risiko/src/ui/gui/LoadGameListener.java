package ui.gui;

import domain.Spiel;
import persistence.SpielSpeichern;
import valueobjects.Spieler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class LoadGameListener implements ActionListener {
    private final GuiMain gui;

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

                for (Spieler s : geladen.getSpielerListe()) {
                    new SpielerFenster(geladen, s);
                }
                gui.setVisible(false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(gui, "Fehler beim Laden!!");
            }
        }
    }

}
