package ui.gui;

import domain.Spiel;
import ui.Risiko;

import javax.swing.*;
import java.awt.*;

public class GuiMain extends JFrame {
    private static final String TITLE = "Risiko - Start";
    private static final Dimension WINDOW_SIZE = new Dimension(460, 300);

    protected final JButton btnLoadGame = new JButton("Spiel laden");
    protected final JTextField tfPlayerName = new JTextField();
    protected final JComboBox<String> cboColor = new JComboBox<>(new String[]{
            "Rot", "Blau", "Grün", "Gelb", "Schwarz", "Weiß"});
    protected final JButton btnAddPlayer = new JButton("Spieler hinzufügen");
    protected final DefaultListModel<String> listModel = new DefaultListModel<>();
    protected final JList<String> listPlayers = new JList<>(listModel);
    protected final JButton btnStartGame = new JButton("Spiel starten");
    private Risiko risiko;
    private Spiel spiel;

    public GuiMain() {
        super(TITLE);
        initLayout();
        initListeners();
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void setRisiko(Risiko risiko) {
        this.risiko = risiko;
    }
    public Risiko getRisiko(){
        return risiko;
    }
    public void setSpiel(Spiel spiel) {
        this.spiel = spiel;
    }
    public Spiel getSpiel(){
        return spiel;
    }
    JTextField getTfPlayerName() {
        return tfPlayerName;
    }

    JComboBox<String> getCboColor() {
        return cboColor;
    }

    DefaultListModel<String> getListModel() {
        return listModel;
    }

    JButton getBtnStartGame() {
        return btnStartGame;
    }

    private void initLayout() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(WINDOW_SIZE);
        setLayout(new BorderLayout());

        JPanel pnNorth = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnNorth.add(btnLoadGame);
        add(pnNorth, BorderLayout.NORTH);

        listPlayers.setVisibleRowCount(6);
        JScrollPane spPlayers = new JScrollPane(listPlayers);
        add(spPlayers, BorderLayout.CENTER);

        JPanel pnWest = new JPanel();
        pnWest.setLayout(new BoxLayout(pnWest, BoxLayout.Y_AXIS));
        pnWest.setBorder(BorderFactory.createTitledBorder("Neuer Spieler"));
        pnWest.add(new JLabel("Name:"));
        pnWest.add(tfPlayerName);
        pnWest.add(Box.createVerticalStrut(6));
        pnWest.add(new JLabel("Farbe:"));
        pnWest.add(cboColor);
        pnWest.add(Box.createVerticalStrut(6));
        pnWest.add(btnAddPlayer);
        add(pnWest, BorderLayout.WEST);

        JPanel pnSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnStartGame.setEnabled(false);
        pnSouth.add(btnStartGame);
        add(pnSouth, BorderLayout.SOUTH);

    }

    private void initListeners() {
        btnAddPlayer.addActionListener(new AddPlayerListener(this));
        btnLoadGame.addActionListener(new LoadGameListener(this));
        btnStartGame.addActionListener(new StartGameListener(this));
    }

    void updateStartButtonState() {
        btnStartGame.setEnabled(listModel.getSize() >= 2);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GuiMain::new);
    }
}

