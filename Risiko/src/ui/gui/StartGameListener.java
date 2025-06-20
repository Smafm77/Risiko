package ui.gui;


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

    StartGameListener(GuiMain gui) {
        this.gui = gui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<Spieler> spielerListe = new ArrayList<>();
        for (int i = 0; i < gui.getListModel().getSize(); i++){
            String entry = gui.getListModel().getElementAt(i);
            String name = entry.substring(0, entry.indexOf("("));
            String farbe = entry.substring(entry.indexOf("(") + 1, entry.indexOf(")"));
            spielerListe.add(new Spieler(name, farbe));
        }
        Spiel neuesSpiel;
        try {
            neuesSpiel = new Spiel();
            for (Spieler s : spielerListe){
                neuesSpiel.addSpieler(s);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        gui.setSpiel(neuesSpiel);
        neuesSpiel.init();
        // neuesSpiel.setPhase(Spielphase.VERTEILEN);
        for (Spieler s : spielerListe){
            new SpielerFenster(neuesSpiel, s);
        }
        // gui.setVisible(false);
    }
}
