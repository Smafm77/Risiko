package ui.gui;

import domain.AktiverSpielerListener;
import domain.Spiel;
import valueobjects.Spieler;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class SpielerFenster extends JFrame implements AktiverSpielerListener {
    private final Spiel spiel;
    private final Spieler spieler;

    private static final java.util.List<SpielerFenster> ALLE = new java.util.ArrayList<>();

    private JLabel lblInfo;
    private JLabel lblPhase;
    private JLabel pnlActions;
    private MapPanel mapPanel;


    public SpielerFenster(Spiel spiel, Spieler spieler) throws IOException {
        this.spieler = spieler;
        setTitle("Risiko - " + spieler.getName());
        this.spiel = Spiel.getInstance();

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
        AktiverSpielerListener.add(this);
        updateView(Spiel.getInstance());
        setVisible(true);


    }
    @Override
    public void onAktiverSpielerGeaendert(Spieler neu){
        SwingUtilities.invokeLater(() -> {
            updateView(Spiel.getInstance());
        });
    }

    public void updateView(Spiel spiel) {
        lblInfo.setText(spieler.getName());

        if (Objects.equals(spieler.getName(), spiel.getAktuellerSpieler().getName())) {
            lblInfo.setText("Du bist am Zug!");
            pnlActions.setVisible(true);
            //ToDO je nach Spielphase bedingt optionen einfügen wie in cui
            JButton btnEndTurn = new JButton("Zug beenden");
            btnEndTurn.addActionListener(e -> {
                spiel.naechsterSpieler();
                updateViewInAllFenster();
            });
            pnlActions.add(btnEndTurn);
        } else {
            lblInfo.setText("Spieler " + spieler.getName() + " ist am Zug.");
            pnlActions.setVisible(false);
        }
        mapPanel.repaint();
    }

    private void updateViewInAllFenster() {
        for (SpielerFenster fenster : SpielerFenster.ALLE) {
            fenster.updateView(spiel);
        }
    }
}
