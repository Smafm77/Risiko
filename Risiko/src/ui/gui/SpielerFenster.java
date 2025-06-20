package ui.gui;

import domain.Spiel;
import valueobjects.Spieler;

import javax.swing.*;
import java.awt.*;

public class SpielerFenster extends JFrame {
    public SpielerFenster(Spiel spiel, Spieler spieler){
        setTitle("Risiko - " + spieler.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JLabel info = new JLabel("Spielphase: " + spiel.getPhase());
        add(info, BorderLayout.NORTH);
        setVisible(true);
    }
}
