package client.ui.gui;

import client.net.RisikoClient;
import common.valueobjects.ISpiel;
import common.valueobjects.Spieler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class LoadGameListener implements ActionListener {
    private final GuiMain gui;


    LoadGameListener(GuiMain gui) {
        this.gui = gui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

            try {
                ISpiel geladen = new RisikoClient();

                for (Spieler s : geladen.getSpielerListe()) {
                    new SpielerFenster(geladen, s);
                }
                gui.setVisible(false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(gui, "Fehler beim Laden!! Cause:" + ex.getCause() + ex.getMessage());
                System.out.println("Source: " + Arrays.toString(ex.getStackTrace()));
            }
        }
    }


