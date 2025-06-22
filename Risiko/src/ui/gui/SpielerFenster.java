package ui.gui;

import domain.Spiel;
import valueobjects.Spieler;

import javax.swing.*;
import java.awt.*;

public class SpielerFenster extends JFrame {
    private final Spiel spiel;
    private final Spieler spieler;

    private static final java.util.List<SpielerFenster> ALLE = new java.util.ArrayList<>();

    private JLabel lblInfo;
    private JLabel lblPhase;
    private JLabel pnlActions;
    private MapPanel mapPanel;


    public SpielerFenster(Spiel spiel, Spieler spieler) {
        this.spieler = spieler;
        setTitle("Risiko - " + spieler.getName());
        this.spiel = spiel;

        ALLE.add(this);

        setLayout(new BorderLayout());

        lblInfo = new JLabel();
        lblPhase = new JLabel();
        JPanel pnlNorth = new JPanel(new GridLayout(2, 1));
        pnlNorth.add(lblInfo);
        pnlNorth.add(lblPhase);
        add(pnlNorth, BorderLayout.NORTH);

        mapPanel = new MapPanel();
        add(mapPanel, BorderLayout.CENTER);

        pnlActions = new JLabel();
        add(pnlActions, BorderLayout.SOUTH);

        updateView(spiel);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void updateView(Spiel spiel) {
        boolean amZug = spieler.equals(spiel.getAktuellerSpieler());
        lblInfo.setText(spieler.getName());

        if (spiel.getPhase() != null) {
            lblPhase.setText("Phase: " + spiel.getPhase().toString());
        } else {
            lblPhase.setText("Spielphase: nicht gesetzt"); //Todo: Können wir später hoffentlich raus nehmen aber gerade habe ich den Fehler satt
        }
        repaint();

        pnlActions.removeAll();

        if (amZug) {
            lblInfo.setText("Du bist am Zug!");
            //ToDO je nach Spielphase bedingt optionen einfügen wie in cui
            JButton btnEndTurn = new JButton("Zug beenden");
            btnEndTurn.addActionListener(e -> {
                spiel.naechsterSpieler();
                updateViewInAllFenster();
            });
            pnlActions.add(btnEndTurn);
        } else {
            lblInfo.setText("Spieler " + spieler.getName() + " ist am Zug.");
        }
        pnlActions.revalidate();
        pnlActions.repaint();

        mapPanel.repaint();
    }

    private void updateViewInAllFenster() {
        for (SpielerFenster fenster : SpielerFenster.ALLE) {
            fenster.updateView(spiel);
        }
    }
}
