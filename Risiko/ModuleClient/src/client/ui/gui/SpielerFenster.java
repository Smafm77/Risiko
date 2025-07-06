package client.ui.gui;

import common.valueobjects.Karte;
import server.domain.AktiverSpielerListener;
import server.domain.Spiel;
import server.persistence.NeuesSpielEinlesen;
import server.persistence.SpielSpeichern;
import common.valueobjects.Land;
import common.valueobjects.Spieler;
import common.enums.AuswahlModus;
import common.enums.Spielphase;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SpielerFenster extends JFrame implements AktiverSpielerListener {
    private final Spiel spiel;
    private final Spieler spieler;
    private AuswahlModus auswahlModus = AuswahlModus.KEINER;
    private int verbleibendeTruppen;
    private Land ausgewaehlt1;
    private Land ausgewaehlt2;

    private static final java.util.List<SpielerFenster> ALLE = new java.util.ArrayList<>();
    NeuesSpielEinlesen einlesen = new NeuesSpielEinlesen();
    private JLabel lblInfo;
    private JLabel lblPhase;
    private JPanel pnlActions;
    private MapPanel mapPanel;


    public SpielerFenster(Spiel spiel, Spieler spieler) throws IOException {
        this.spieler = spieler;
        setTitle("Risiko - " + spieler.getName() + " (" + spieler.getFarbe() + ")");

        JMenuBar menuBar = getBar();
        setJMenuBar(menuBar);

        this.spiel = spiel;
        ALLE.add(this);

        setLayout(new BorderLayout());

        lblInfo = new JLabel();
        lblPhase = new JLabel();
        JPanel pnlNorth = new JPanel(new GridLayout(2, 1));
        pnlNorth.add(lblInfo);
        pnlNorth.add(lblPhase);
        add(pnlNorth, BorderLayout.NORTH);
        JPanel missionsPanel = new JPanel();
        missionsPanel.setLayout(new BoxLayout(missionsPanel, BoxLayout.Y_AXIS));
        missionsPanel.setBorder(BorderFactory.createTitledBorder("Mission"));

        JLabel lblMission = new JLabel(spiel.getMissionBeschreibung(spieler));
        lblMission.setAlignmentX(Component.CENTER_ALIGNMENT);
        missionsPanel.add(lblMission);

        JProgressBar progress = new JProgressBar(0, 100);
        progress.setValue(spiel.getMissionProgress(spieler));
        progress.setStringPainted(true);
        progress.setAlignmentX(Component.CENTER_ALIGNMENT);
        missionsPanel.add(progress);

        missionsPanel.add(Box.createVerticalStrut(20));

        JButton btnKarteSpielen = new JButton(("Karte spielen"));
        btnKarteSpielen.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnKarteSpielen.addActionListener(e -> openCardsSelectionDialog());
        missionsPanel.add(btnKarteSpielen);

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
                    updateAllMaps();
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
                    SpielerFenster verteidiger = ALLE.stream().filter(spielerFenster -> spielerFenster.spieler.equals(ausgewaehlt2.getBesitzer())).findFirst().orElseThrow();
                    int truppenV = verteidiger.frageAnzahl("Wie viele Einheiten sollen " + ausgewaehlt2.getName() + " vor " + spieler.getName() + "'s " + truppenA + " angreifenden Truppen verteidigen? (max " + Math.min(ausgewaehlt2.getEinheiten(), 2) + ")?", 1, Math.min(ausgewaehlt2.getEinheiten(), 2));
                    boolean ergebnis = spiel.kampf(ausgewaehlt1, ausgewaehlt2, truppenA, truppenV);
                    String sieger = ergebnis ? spieler.getName() : land.getBesitzer().getName();
                    String schlachtbericht = ergebnis ? (sieger + " hat " + ausgewaehlt2.getName() + " erobert") : (sieger + " konnte " + ausgewaehlt2.getName() + " verteidigen");
                    JOptionPane.showMessageDialog(SpielerFenster.this, schlachtbericht);
                    JOptionPane.showMessageDialog(verteidiger, schlachtbericht);
                    updateMissionStaus();
                    updateAllMaps();
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
                    if (!ausgewaehlt1.connectionPossible(land)) {
                        JOptionPane.showMessageDialog(SpielerFenster.this, "Nur eigene Nachbarländer als Ziel!");
                        return;
                    }
                    ausgewaehlt2 = land;
                    int max = ausgewaehlt1.getEinheiten() - 1;
                    int anzahl = frageAnzahl("Wie viele Einheiten verschieben? (max " + max + ")", 1, max);
                    spieler.bewegeEinheiten(anzahl, ausgewaehlt1, ausgewaehlt2);
                    JOptionPane.showMessageDialog(SpielerFenster.this, "Einheiten verschoben!");
                    updateAllMaps();
                    ausgewaehlt1 = null;
                    ausgewaehlt2 = null;
                    auswahlModus = AuswahlModus.VERSCHIEBEN_HERKUNFT;
                    break;

                default:
                    // Kein Modus → Nichts tun
            }
        });
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, missionsPanel, mapPanel);
        split.setResizeWeight(0.3);
        split.setOneTouchExpandable(true);

        add(split, BorderLayout.CENTER);

        pnlActions = new JPanel();
        add(pnlActions, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        AktiverSpielerListener.add(this);
        updateView(this.spiel);
        setVisible(true);


    }

    private void openCardsSelectionDialog() {
        Set<Karte> karteSet = spieler.getKarten();
        if (karteSet.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Du hast keine Karten zum spielen!");
            return;
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
            int bonus = spiel.spieleKarte(spieler, auswahl);
            if (bonus > 0) {
                JOptionPane.showMessageDialog(this, "Karte gespielt! Du erhälst " + bonus + " Einheiten.", "Karte gespielt", JOptionPane.INFORMATION_MESSAGE);
                spiel.setPhase(Spielphase.VERTEILEN);
                verbleibendeTruppen = bonus;
                auswahlModus = AuswahlModus.VERTEILEN;
                lblInfo.setText("Verteile " + bonus + " Bonus-Einheiten.");

                pnlActions.removeAll();
                JButton btnFertig = new JButton("Verteilen abschließen");
                btnFertig.addActionListener(e -> {
                    spiel.naechstePhase();
                    updateViewInAllFenster();
                });
                pnlActions.add(btnFertig);
                pnlActions.setVisible(true);
                pnlActions.revalidate();
                mapPanel.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Karte konnte nicht gespielt werden.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JMenuBar getBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Datei");
        JMenuItem miSaveExit = new JMenuItem("Speichern & Beenden");
        miSaveExit.addActionListener(e -> {
            try {
                SpielSpeichern.speichern(Spiel.getInstance(), "spielstand.risiko");
                for (SpielerFenster fenster : SpielerFenster.ALLE) {
                    fenster.dispose();
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Fehler beim Speichern:\n" + ex.getMessage(),
                        "Speicherfehler",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
        fileMenu.add(miSaveExit);
        menuBar.add(fileMenu);
        return menuBar;
    }

    @Override
    public void onAktiverSpielerGeaendert(Spieler neu) {
        SwingUtilities.invokeLater(() -> {
            updateView(Spiel.getInstance());
        });
    }

    public void updateView(Spiel spiel) {
        lblInfo.setText(spieler.getName());
        pnlActions.removeAll();

        if (spiel.getPhase() == Spielphase.VERTEILEN && spiel.getAktuellerSpieler().equals(spieler)) {
            updateMissionStaus();
            verbleibendeTruppen = spieler.berechneNeueEinheiten(spiel.getWelt().alleKontinente);
            auswahlModus = AuswahlModus.VERTEILEN;
            lblInfo.setText("Verteile " + verbleibendeTruppen + " Einheiten. Klicke auf deine Länder.");
            JButton btnFertig = new JButton("Verteilen abschließen");
            btnFertig.addActionListener(e -> {
                updateMissionStaus();
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
                updateMissionStaus();
                spiel.naechstePhase();
                updateViewInAllFenster();
            });
            pnlActions.add(btnFertig);
        }

        if (!Objects.equals(spieler.getName(), spiel.getAktuellerSpieler().getName())) { //Warum nicht einfach spieler.equals(spiel.getAktuellerSpieler()) ?
            auswahlModus = AuswahlModus.KEINER;
            lblInfo.setText("Spieler " + spiel.getAktuellerSpieler().getName() + " ist am Zug.");
            updateMissionProgressbar();
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
        System.out.println("__________");
    }

    private void updateAllMaps() {
        for (SpielerFenster fenster : SpielerFenster.ALLE) {
            fenster.mapPanel.repaint();
        }
    }

    private void updateMissionStaus() {
        checkMissionFulfilled();
        updateMissionProgressbar();
    }

    private void updateMissionProgressbar() {//Updated Progressbar auf Bildschirm
        System.out.println(spieler.getName() + ": [ " + spiel.getMissionProgress(spieler) + "% ]");
    }

    private void checkMissionFulfilled() {
        if (spiel.hatMissionErfuellt(spieler)) {
            updateMissionProgressbar();
            benachrichtigeAlle(spieler.getName() + "'s Mission ist erfüllt. Damit hat " + spieler.getName() + " gewonnen!");
            //ToDo spielende einläuten
        } else {
            System.out.println(spieler.getName() + " hat Mission [" + spiel.getMissionBeschreibung(spieler) + "] noch nicht erfüllt");
        }
    }

    private void benachrichtigeAlle(String nachricht) {
        //make Asynchron? alle sollen die Nachricht zeitgleich, nicht nacheinander kriegen wenn möglich
        for (SpielerFenster fenster : SpielerFenster.ALLE) {
            JOptionPane.showMessageDialog(fenster, nachricht, "To whom it may concern", JOptionPane.INFORMATION_MESSAGE);
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
}
