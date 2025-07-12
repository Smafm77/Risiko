package server.net;

import common.valueobjects.Spieler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;


public class ServerGUI extends JFrame{
    private static final String TITLE = "Risiko - Server Setup";
    private static final Dimension WINDOW_SIZE = new Dimension(460, 300);

    protected final JButton btnLoadGame = new JButton("Spiel laden");
    protected final JTextField tfPlayerName = new JTextField();
    protected ArrayList<String> farbenList = new ArrayList<>(Arrays.asList("Rot", "Blau", "Gruen", "Gelb", "Orange", "Violett"));
    protected Vector<String> farben = new Vector<>(farbenList);
    protected final JComboBox<String> cboColor = new JComboBox<>(farben);
    protected final JButton btnAddPlayer = new JButton("Spieler hinzufügen");
    protected final DefaultListModel<String> listModel = new DefaultListModel<>();
    protected final JList<String> listPlayers = new JList<>(listModel);
    protected final JButton btnStartGame = new JButton("Spiel starten");
    private final ArrayList<Spieler> guiSpieler = new ArrayList<>();
    protected final JLabel lblStatus = new JLabel("Mindestens 3 Spieler hinzufügen");

    public ServerGUI() {
        super(TITLE);
        initLayout();
        initListeners();
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
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
        pnSouth.add(lblStatus);
        pnSouth.add(btnStartGame);
        add(pnSouth, BorderLayout.SOUTH);

    }
    private void initListeners() {
        btnAddPlayer.addActionListener(e ->{
            String name = tfPlayerName.getText().trim();
            String color = (String) cboColor.getSelectedItem();
            if(name.isEmpty() || color == null){
                JOptionPane.showMessageDialog(this, "Bitte Name und Farbe angeben!");
            }
            for(Spieler sp : guiSpieler){
                if (sp.getName().equalsIgnoreCase(name)){
                    JOptionPane.showMessageDialog(this, "Name bereits vergeben!");
                }
                if(sp.getFarbe().equalsIgnoreCase(color)){
                    JOptionPane.showMessageDialog(this, "Farbe bereits vergeben!");
                }
            }
            guiSpieler.add(new Spieler(name, color, farbeZuId(color)));
            listModel.addElement(name + " (" + color + ")");
            farben.remove(color);
            cboColor.setModel(new DefaultComboBoxModel<>(farben));
            tfPlayerName.setText("");
            cboColor.setSelectedIndex(0);
            updateStartButtonState();
        });
        btnStartGame.addActionListener(e -> {
            if (guiSpieler.size() <3){
                JOptionPane.showMessageDialog(this, "Mindestens 3 Spieler notwendig!");
            }
            btnAddPlayer.setEnabled(false);
            btnStartGame.setEnabled(false);
            lblStatus.setText("Server startet...");

            new Thread(()-> {
                SpielServer.startServer(guiSpieler, lblStatus);
            }).start();
        });
    }

    void updateStartButtonState() {
        btnStartGame.setEnabled(listModel.getSize() >= 3);
    }
    private int farbeZuId(String color){
        return switch (color) {
            case "Rot" -> 1;
            case "Blau" -> 2;
            case "Gruen" -> 3;
            case "Gelb" -> 4;
            case "Orange" -> 5;
            case "Violett" -> 6;
            default -> 0;
        };
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerGUI::new);
    }
}

