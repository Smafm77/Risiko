package client.ui.gui;

import client.net.RisikoClient;
import common.exceptions.EinheitenAnzahlException;
import common.exceptions.FalscherBesitzerException;
import common.exceptions.UngueltigeKarteException;
import common.valueobjects.*;
import common.enums.AuswahlModus;
import common.enums.Spielphase;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class SpielerFenster extends JFrame implements AktiverSpielerListener {
    private final ISpiel spiel;
    private final SpielerDTO spieler;
    private AuswahlModus auswahlModus = AuswahlModus.KEINER;
    private int verbleibendeTruppen;
    private LandDTO ausgewaehlt1;
    private LandDTO ausgewaehlt2;
    private boolean spielBeendet = false;
    javax.swing.Timer refresh;

    private static final java.util.List<SpielerFenster> ALLE = new java.util.ArrayList<>();
    private final JLabel lblInfo;
    private final JPanel pnlActions;
    private final MapPanel mapPanel;
    private final JProgressBar progress;


    public SpielerFenster(ISpiel spiel, SpielerDTO spieler) throws IOException {
        this.spieler = spieler;
        setTitle("Risiko - " + spieler.getName() + " (" + spieler.getFarbe() + ")");

        JMenuBar menuBar = getBar();
        setJMenuBar(menuBar);

        this.spiel = spiel;
        ALLE.add(this);

        setLayout(new BorderLayout());

        lblInfo = new JLabel();
        JLabel lblPhase = new JLabel();
        JPanel pnlNorth = new JPanel(new GridLayout(2, 1));
        pnlNorth.add(lblInfo);
        pnlNorth.add(lblPhase);
        add(pnlNorth, BorderLayout.NORTH);
        JPanel missionsPanel = new JPanel();
        missionsPanel.setLayout(new BoxLayout(missionsPanel, BoxLayout.Y_AXIS));
        missionsPanel.setBorder(BorderFactory.createTitledBorder("Mission"));
        missionsPanel.setPreferredSize(new Dimension(250, 0));
        missionsPanel.setMinimumSize(new Dimension(100, 0));

        JTextArea txtMission = new JTextArea(spiel.getMissionBeschreibung(spieler.getId()));
        txtMission.setWrapStyleWord(true);
        txtMission.setLineWrap(true);
        txtMission.setEditable(false);
        txtMission.setFocusable(false);
        txtMission.setOpaque(false);
        txtMission.setBorder(null);
        missionsPanel.add(txtMission);

        progress = new JProgressBar(0, 100);
        progress.setValue(spiel.getMissionProgress(spieler.getId()));
        progress.setStringPainted(true);
        progress.setAlignmentX(Component.CENTER_ALIGNMENT);
        missionsPanel.add(progress);

        missionsPanel.add(Box.createVerticalStrut(20));

        mapPanel = new MapPanel((ArrayList<LandDTO>) spiel.getWelt().getAlleLaender().stream().map(Land::toDTO).collect(Collectors.toList()), spiel);
        mapPanel.setLandKlickListener(land -> {
            if (!spiel.getAktuellerSpieler().equals(spieler)) return;

            switch (auswahlModus) {
                case VERTEILEN:
                    if (verbleibendeTruppen > 0) {
                        if (!spiel.getLandbesitzer(land.getId()).equals(spieler)) {
                            throw new FalscherBesitzerException("Dieses Land gehört dir nicht!");
                        }
                        spiel.einheitenStationieren(land.getId(), 1);
                        verbleibendeTruppen--;
                        lblInfo.setText("Verteile " + verbleibendeTruppen + " Einheiten.");
                        updateAllMaps();
                    } else {
                        throw new EinheitenAnzahlException("Du hast keine Einheiten mehr zum verteilen");
                    }
                    break;

                case ANGRIFF_HERKUNFT:
                    if (!spiel.getLandbesitzer(land.getId()).equals(spieler)) {
                        throw new FalscherBesitzerException("Dieses Land gehört dir nicht!");
                    }
                    if (spiel.getLandTruppen(land.getId()) <= 1) {
                        throw new EinheitenAnzahlException("Wähle ein eigenes Land mit mehr als 1 Einheit!");
                    }
                    ausgewaehlt1 = land;
                    mapPanel.zeigeOverlayHerkunft(land.getName());
                    auswahlModus = AuswahlModus.ANGRIFF_ZIEL;
                    lblInfo.setText("Wähle ein feindliches Nachbarland zum Angreifen.");
                    break;

                case ANGRIFF_ZIEL:
                    if (spiel.getLandbesitzer(land.getId()).equals(spieler) && spiel.getLandTruppen(land.getId()) > 1) {
                        mapPanel.versteckeOverlay();
                        ausgewaehlt1 = land;
                        mapPanel.zeigeOverlayHerkunft(land.getName());
                        break;
                    }
                    ausgewaehlt2 = land;
                    mapPanel.zeigeOverlayZiel(land.getName());
                    int truppenA = frageAnzahl("Mit wie vielen Truppen angreifen (max " + Math.min(spiel.getLandTruppen(ausgewaehlt1.getId()) - 1, 3) + ")?", 1, Math.min(spiel.getLandTruppen(ausgewaehlt1.getId()) - 1, 3));

                    int truppenV = Math.min(2, spiel.getLandTruppen(ausgewaehlt2.getId()));

                    mapPanel.zeigeKampfLand(land.getName());
                    boolean ergebnis = spiel.kampf(ausgewaehlt1.getId(), ausgewaehlt2.getId(), truppenA, truppenV);
                    String sieger = ergebnis ? spieler.getName() : spiel.getLandbesitzer(land.getId()).getName();
                    String schlachtbericht = ergebnis ? (sieger + " hat " + ausgewaehlt2.getName() + " erobert") : (sieger + " konnte " + ausgewaehlt2.getName() + " verteidigen");
                    JOptionPane.showMessageDialog(SpielerFenster.this, schlachtbericht);
                    updateMissionStatus();
                    updateAllMaps();
                    ausgewaehlt1 = null;
                    ausgewaehlt2 = null;
                    mapPanel.versteckeOverlay();
                    mapPanel.beendeKampfLand();
                    auswahlModus = AuswahlModus.ANGRIFF_HERKUNFT;
                    lblInfo.setText("Für nächsten Angriff: Herkunft wählen.");
                    break;

                case VERSCHIEBEN_HERKUNFT:
                    if (!spiel.getLandbesitzer(land.getId()).equals(spieler)) {
                        throw new FalscherBesitzerException("Dieses Land gehört dir nicht!");
                    }
                    if (spiel.getLandTruppen(land.getId()) <= 1) {
                        throw new EinheitenAnzahlException("Wähle ein eigenes Land mit mehr als 1 Einheit!");
                    }
                    ausgewaehlt1 = land;
                    mapPanel.zeigeOverlayHerkunft(land.getName());
                    auswahlModus = AuswahlModus.VERSCHIEBEN_ZIEL;
                    lblInfo.setText("Wähle Ziel-Land (eigenes Nachbarland von " + ausgewaehlt1.getName() + ").");
                    break;

                case VERSCHIEBEN_ZIEL:
                    ausgewaehlt2 = land;
                    mapPanel.zeigeOverlayZiel(land.getName());
                    int max = spiel.getLandTruppen(ausgewaehlt1.getId()) - 1;
                    int anzahl = frageAnzahl("Wie viele Einheiten verschieben? (max " + max + ")", 1, max);
                    spiel.bewegeEinheiten(spieler.getId(), anzahl, ausgewaehlt1.getId(), ausgewaehlt2.getId());
                    JOptionPane.showMessageDialog(SpielerFenster.this, "Einheiten verschoben!");
                    updateAllMaps();
                    ausgewaehlt1 = null;
                    ausgewaehlt2 = null;
                    mapPanel.versteckeOverlay();
                    auswahlModus = AuswahlModus.VERSCHIEBEN_HERKUNFT;
                    lblInfo.setText("Wähle ein eigenes Land, von dem du Einheiten verschieben willst.");
                    break;

                default:
                    // Kein Modus → Nichts tun
            }
        });

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, missionsPanel, mapPanel);
        split.setDividerLocation(250);
        split.setResizeWeight(0.0);
        split.setOneTouchExpandable(true);

        add(split, BorderLayout.CENTER);

        pnlActions = new JPanel();
        add(pnlActions, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        AktiverSpielerListener.add(this);
        updateView();
        setVisible(true);
    }

    private void updateInaktiveSpieler() {
        boolean inaktiv = (!spiel.getAktuellerSpieler().equals(spieler) && spiel instanceof RisikoClient);

        if (inaktiv) {
            if (refresh == null) {
                refresh = new javax.swing.Timer(1000, e -> SwingUtilities.invokeLater(this::updateView));
                refresh.start();
            }
        } else {
            if (refresh != null) {
                refresh.stop();
                refresh = null;
            }
        }

    }

    private void openCardsSelectionDialog() {
        try {
            Set<Karte> karteSet = spiel.getSpielerKarten(spieler.getId());
            if (karteSet.isEmpty()) {
                throw new UngueltigeKarteException("Du besitzt keine Karten");
            }
            ArrayList<Karte> karten = new ArrayList<>(karteSet);
            DefaultListModel<String> model = new DefaultListModel<>();
            for (Karte k : karten) {
                model.addElement("[" + k.getStrength() + "]" + k.getLand().getName());
            }
            JList<String> list = new JList<>(model);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            int res = JOptionPane.showConfirmDialog(this, new JScrollPane(list), "Karte spielen", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (res == JOptionPane.OK_OPTION && !list.isSelectionEmpty()) {
                Karte auswahl = karten.get(list.getSelectedIndex());
                int bonus = spiel.spieleKarte(spieler.getId(), auswahl);
                if (bonus > 0) {
                    JOptionPane.showMessageDialog(this, "Karte gespielt! Du erhälst " + bonus + " Einheiten.", "Karte gespielt", JOptionPane.INFORMATION_MESSAGE);
                    verbleibendeTruppen += bonus;
                    auswahlModus = AuswahlModus.VERTEILEN;
                    lblInfo.setText("Verteile " + verbleibendeTruppen + " Einheiten. Klicke auf deine Länder.");
                    mapPanel.repaint();
                }
            }
        } catch (UngueltigeKarteException e) {
            JOptionPane.showMessageDialog(SpielerFenster.this, "Fehler: " + e.getMessage(), "Ungültige Karte", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JMenuBar getBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Datei");
        JMenuItem miSaveExit = new JMenuItem("Speichern & Beenden");
        miSaveExit.addActionListener(e -> {
            if (spiel.getPhase() != Spielphase.VERTEILEN) {
                spiel.spielSpeichern();
                for (SpielerFenster fenster : SpielerFenster.ALLE) {
                    fenster.dispose();
                }
            } else {
                JOptionPane.showMessageDialog(SpielerFenster.this, "Schließe erst verteilen ab");
            }
        });
        fileMenu.add(miSaveExit);
        menuBar.add(fileMenu);
        return menuBar;
    }

    public void updateView() {
        if (spielBeendet) {
            pnlActions.removeAll();
            pnlActions.revalidate();
            pnlActions.repaint();
            return;
        }
        lblInfo.setText(spieler.getName());
        pnlActions.removeAll();
        updateMissionStatus();

        if (spiel.getPhase() == Spielphase.VERTEILEN && spiel.getAktuellerSpieler().equals(spieler)) {
            JButton btnKarteSpielen = new JButton(("Karte spielen"));
            btnKarteSpielen.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnKarteSpielen.addActionListener(e -> openCardsSelectionDialog());
            pnlActions.add(btnKarteSpielen);

            updateMissionStatus();
            verbleibendeTruppen = spiel.berechneSpielerEinheiten(spieler.getId());
            auswahlModus = AuswahlModus.VERTEILEN;
            lblInfo.setText("Verteile " + verbleibendeTruppen + " Einheiten. Klicke auf deine Länder.");
            JButton btnFertig = new JButton("Verteilen abschließen");
            btnFertig.addActionListener(e -> {
                if (verbleibendeTruppen <= 0) {
                    pnlActions.remove(btnKarteSpielen);
                    spiel.naechstePhase();
                    updateMissionStatus();
                    pnlActions.setVisible(false);
                    pnlActions.setVisible(true);
                    updateViewInAllFenster();
                } else {
                    JOptionPane.showMessageDialog(SpielerFenster.this, "Du hast noch nicht alle Truppen verteilt");
                }
            });
            pnlActions.add(btnFertig);
        }

        if (spiel.getPhase() == Spielphase.ANGRIFF && spiel.getAktuellerSpieler().equals(spieler)) {
            auswahlModus = AuswahlModus.ANGRIFF_HERKUNFT;
            lblInfo.setText("Wähle ein eigenes Land mit >1 Einheiten für den Angriff.");
            JButton btnBeenden = new JButton("Angriffsphase beenden");
            btnBeenden.addActionListener(e -> {
                updateMissionStatus();
                spiel.naechstePhase();
                mapPanel.versteckeOverlay();
                updateViewInAllFenster();
            });
            pnlActions.add(btnBeenden);
        }

        if (spiel.getPhase() == Spielphase.VERSCHIEBEN && spiel.getAktuellerSpieler().equals(spieler)) {
            auswahlModus = AuswahlModus.VERSCHIEBEN_HERKUNFT;
            lblInfo.setText("Wähle ein eigenes Land, von dem du Einheiten verschieben willst.");
            JButton btnFertig = new JButton("Verschieben abschließen");
            btnFertig.addActionListener(e -> {
                updateMissionStatus();
                spiel.naechstePhase();
                mapPanel.versteckeOverlay();
                updateViewInAllFenster();
            });
            pnlActions.add(btnFertig);
        }

        if (!spiel.getAktuellerSpieler().equals(spieler)) {
            auswahlModus = AuswahlModus.KEINER;
            lblInfo.setText("Spieler " + spiel.getAktuellerSpieler().getName() + " ist am Zug.");
            updateMissionProgressbar();
            pnlActions.setVisible(false);
        } else {
            pnlActions.setVisible(true);
        }
        pnlActions.revalidate();
        mapPanel.repaint();
        updateInaktiveSpieler();
    }

    private void updateViewInAllFenster() {
        for (SpielerFenster fenster : SpielerFenster.ALLE) {
            fenster.updateView();
            updateMissionProgressbar();
        }
    }

    private void updateAllMaps() {
        for (SpielerFenster fenster : SpielerFenster.ALLE) {
            fenster.mapPanel.repaint();
        }
    }

    private void updateMissionStatus() {
        checkMissionFulfilled();
        updateMissionProgressbar();
    }

    private void updateMissionProgressbar() {//Updated Progressbar auf Bildschirm
        if (progress != null) {
            progress.setValue(spiel.getMissionProgress(spieler.getId()));
        }
    }

    private void checkMissionFulfilled() {
        if (spiel.hatMissionErfuellt(spieler.getId())) {
            updateMissionProgressbar();
            spielBeendet = true;
            benachrichtigeAlle(spieler.getName() + "'s Mission ist erfüllt. Damit hat " + spieler.getName() + " gewonnen!");
            for (SpielerFenster fenster : ALLE) {
                if (fenster.spieler.equals(this.spieler)) {
                    fenster.mapPanel.gewinnerAnimation();
                }
                for (Component c : fenster.pnlActions.getComponents()) {
                    if (c instanceof JButton) {
                        c.setEnabled(false);
                    }
                }
                JMenuBar mb = fenster.getJMenuBar();
                if (mb != null) {
                    for (int i = 0; i < mb.getMenuCount(); i++) {
                        JMenu m = mb.getMenu(i);
                        if (m != null) {
                            for (int j = 0; j < m.getItemCount(); j++) {
                                JMenuItem mi = m.getItem(j);
                                if (mi != null && !"Speichern & Beenden".equals(mi.getText())) {
                                    mi.setEnabled(false);
                                }
                            }
                        }
                    }
                }
                fenster.pnlActions.revalidate();
                fenster.pnlActions.repaint();
            }

        }

    }

    private void benachrichtigeAlle(String nachricht) {
        for (SpielerFenster fenster : SpielerFenster.ALLE) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(fenster, nachricht, "To whom it may concern", JOptionPane.INFORMATION_MESSAGE));
        }
    }

    private int frageAnzahl(String frage, int min, int max) {
        while (true) {
            String eingabe = JOptionPane.showInputDialog(this, frage);
            if (eingabe == null) return min;
            try {
                int wert = Integer.parseInt(eingabe);
                if (wert >= min && wert <= max) return wert;
            } catch (NumberFormatException ignored) {
            }
            JOptionPane.showMessageDialog(this, "Bitte Zahl zwischen " + min + " und " + max + " eingeben!");
        }
    }

    @Override
    public void onAktiverSpielerGeaendert(Spieler neu) {

        List<AktiverSpielerListener> LIST = new CopyOnWriteArrayList<>();
        SwingUtilities.invokeLater(this::updateView);
        updateInaktiveSpieler();

    }
}
