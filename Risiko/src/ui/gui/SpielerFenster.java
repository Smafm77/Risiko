package ui.gui;

import domain.AktiverSpielerListener;
import domain.Spiel;
import exceptions.FalscherBesitzerException;
import exceptions.UngueltigeBewegungException;
import valueobjects.Land;
import valueobjects.Spieler;
import enums.AuswahlModus;
import enums.Spielphase;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class SpielerFenster extends JFrame implements AktiverSpielerListener {
    private final Spiel spiel;
    private final Spieler spieler;
    private AuswahlModus auswahlModus = AuswahlModus.KEINER;
    private int verbleibendeTruppen;
    private Land ausgewaehlt1;
    private Land ausgewaehlt2;

    private static final java.util.List<SpielerFenster> ALLE = new java.util.ArrayList<>();

    private JLabel lblInfo;
    private JLabel lblPhase;
    private JPanel pnlActions;
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

        mapPanel = new MapPanel(spiel.getWelt().getAlleLaender());
        mapPanel.setLandKlickListener(land -> {
            if (!spiel.getAktuellerSpieler().equals(spieler)) return;

            switch (auswahlModus) {
                case VERTEILEN:
                    if (!land.getBesitzer().equals(spieler)) {
                        JOptionPane.showMessageDialog(SpielerFenster.this, "Dieses Land gehört dir nicht!");
                        return;
                    }
                    land.einheitenHinzufuegen(1);
                    verbleibendeTruppen--;
                    lblInfo.setText("Verteile " + verbleibendeTruppen + " Einheiten.");
                    mapPanel.repaint();
                    if (verbleibendeTruppen <= 0) {
                        auswahlModus = AuswahlModus.KEINER;
                        JOptionPane.showMessageDialog(SpielerFenster.this, "Alle Einheiten verteilt!");
                    }
                    break;

                case ANGRIFF_HERKUNFT:
                    if (!land.getBesitzer().equals(spieler) || land.getEinheiten() <= 1) {
                        JOptionPane.showMessageDialog(SpielerFenster.this, "Wähle ein eigenes Land mit mehr als 1 Einheit!");
                        return;
                    }
                    ausgewaehlt1 = land;
                    auswahlModus = AuswahlModus.ANGRIFF_ZIEL;
                    lblInfo.setText("Wähle ein feindliches Nachbarland zum Angreifen.");
                    break;

                case ANGRIFF_ZIEL:
                    if (!ausgewaehlt1.getFeindlicheNachbarn().contains(land)) {
                        JOptionPane.showMessageDialog(SpielerFenster.this, "Nur feindliche Nachbarländer angreifen!");
                        return;
                    }
                    ausgewaehlt2 = land;
                    int truppenA = frageAnzahl("Mit wie vielen Truppen angreifen (max " + Math.min(ausgewaehlt1.getEinheiten() - 1, 3) + ")?", 1, Math.min(ausgewaehlt1.getEinheiten() - 1, 3));
                    int truppenV = frageAnzahl("Wie viele Truppen verteidigen? (max " + Math.min(ausgewaehlt2.getEinheiten(), 2) + ")?", 1, Math.min(ausgewaehlt2.getEinheiten(), 2));
                    boolean ergebnis = spiel.kampf(ausgewaehlt1, ausgewaehlt2, truppenA, truppenV);
                    String sieger = ergebnis ? spieler.getName() : land.getBesitzer().getName();
                    JOptionPane.showMessageDialog(SpielerFenster.this, sieger + " hat den Kampf gewonnen.");
                    mapPanel.repaint();
                    ausgewaehlt1 = null;
                    ausgewaehlt2 = null;
                    auswahlModus = AuswahlModus.ANGRIFF_HERKUNFT;
                    lblInfo.setText("Für nächsten Angriff: Herkunft wählen.");
                    break;

                case VERSCHIEBEN_HERKUNFT:
                    if (!land.getBesitzer().equals(spieler) || land.getEinheiten() <= 1) {
                        JOptionPane.showMessageDialog(SpielerFenster.this, "Nur eigene Länder mit mehr als 1 Einheit wählen!");
                        return;
                    }
                    ausgewaehlt1 = land;
                    auswahlModus = AuswahlModus.VERSCHIEBEN_ZIEL;
                    lblInfo.setText("Wähle Ziel-Land (eigenes Nachbarland).");
                    break;

                case VERSCHIEBEN_ZIEL:
                    if (!ausgewaehlt1.getNachbarn().contains(land) || !land.getBesitzer().equals(spieler)) {
                        JOptionPane.showMessageDialog(SpielerFenster.this, "Nur eigene Nachbarländer als Ziel!");
                        return;
                    }
                    ausgewaehlt2 = land;
                    int max = ausgewaehlt1.getEinheiten() - 1;
                    int anzahl = frageAnzahl("Wie viele Einheiten verschieben? (max " + max + ")", 1, max);
                    spieler.bewegeEinheiten(anzahl, ausgewaehlt1, ausgewaehlt2);
                    JOptionPane.showMessageDialog(SpielerFenster.this, "Einheiten verschoben!");
                    mapPanel.repaint();
                    ausgewaehlt1 = null;
                    ausgewaehlt2 = null;
                    auswahlModus = AuswahlModus.KEINER;
                    break;

                default:
                    // Kein Modus → Nichts tun
            }
        });
        add(mapPanel, BorderLayout.CENTER);

        pnlActions = new JPanel();
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
        pnlActions.removeAll();

        if (spiel.getPhase() == Spielphase.VERTEILEN && spiel.getAktuellerSpieler().equals(spieler)) {
            verbleibendeTruppen = spieler.berechneNeueEinheiten(spiel.getWelt().alleKontinente);
            auswahlModus = AuswahlModus.VERTEILEN;
            lblInfo.setText("Verteile " + verbleibendeTruppen + " Einheiten. Klicke auf deine Länder.");
            JButton btnFertig = new JButton("Verteilen abschließen");
            btnFertig.addActionListener(e -> {
                spiel.naechstePhase();
                updateViewInAllFenster();
            });
            pnlActions.add(btnFertig);
        }

        if (spiel.getPhase() == Spielphase.ANGRIFF && spiel.getAktuellerSpieler().equals(spieler)) {
            auswahlModus = AuswahlModus.ANGRIFF_HERKUNFT;
            lblInfo.setText("Wähle ein eigenes Land mit >1 Einheiten für den Angriff.");
            JButton btnBeenden = new JButton("Angriffsphase beenden");
            btnBeenden.addActionListener(e -> {
                spiel.naechstePhase();
                updateViewInAllFenster();
            });
            pnlActions.add(btnBeenden);
        }

        if (spiel.getPhase() == Spielphase.VERSCHIEBEN && spiel.getAktuellerSpieler().equals(spieler)) {
            auswahlModus = AuswahlModus.VERSCHIEBEN_HERKUNFT;
            lblInfo.setText("Wähle ein eigenes Land, von dem du Einheiten verschieben willst.");
            JButton btnFertig = new JButton("Verschieben abschließen");
            btnFertig.addActionListener(e -> {
                spiel.naechstePhase();
                updateViewInAllFenster();
            });
            pnlActions.add(btnFertig);
        }

        if (!Objects.equals(spieler.getName(), spiel.getAktuellerSpieler().getName())) {
            auswahlModus = AuswahlModus.KEINER;
            lblInfo.setText("Spieler " + spiel.getAktuellerSpieler().getName() + " ist am Zug.");
            pnlActions.setVisible(false);
        } else {
            pnlActions.setVisible(true);
        }
        pnlActions.revalidate();
        mapPanel.repaint();
        mapPanel.repaint();
    }

    private void updateViewInAllFenster() {
        for (SpielerFenster fenster : SpielerFenster.ALLE) {
            fenster.updateView(spiel);
        }
    }

    private int frageAnzahl(String frage, int min, int max) {
        while (true) {
            String eingabe = JOptionPane.showInputDialog(this, frage);
            if (eingabe == null) return min;
            try {
                int wert = Integer.parseInt(eingabe);
                if (wert >= min && wert <= max) return wert;
            } catch (NumberFormatException ignored) {}
            JOptionPane.showMessageDialog(this, "Bitte Zahl zwischen " + min + " und " + max + " eingeben!");
        }
    }
}
