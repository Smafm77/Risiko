package server.net;

import javax.swing.*;
import java.awt.*;


public class ServerGUI extends JFrame{
    private static final String TITLE = "Risiko - Server Setup";
    private static final Dimension WINDOW_SIZE = new Dimension(460, 300);

    private final JLabel lblPrompt = new JLabel("Anzahl Spieler:");
    private final JSpinner spinnerSpieler = new JSpinner(new SpinnerNumberModel(3,3,6,1));
    private final JButton btnStartServer = new JButton("Server starten");
    private final JLabel lblStatus = new JLabel("Wähle Spieleranzahl");
    private final JCheckBox cbSpielLaden = new JCheckBox("Bestehendes Spiel laden");

    public ServerGUI() {
        super(TITLE);
        setPreferredSize(WINDOW_SIZE);
        setLayout(new BorderLayout());

        JPanel pnNorth = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnNorth.add(cbSpielLaden);
        add(pnNorth, BorderLayout.NORTH);

        JPanel pnCenter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnCenter.add(lblPrompt);
        pnCenter.add(spinnerSpieler);
        add(pnCenter, BorderLayout.CENTER);

        JPanel pnSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnSouth.add(lblStatus);
        pnSouth.add(btnStartServer);
        add(pnSouth, BorderLayout.SOUTH);

        initListeners();
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void initListeners() {

        btnStartServer.addActionListener(e -> {
           boolean spielLaden = cbSpielLaden.isSelected();
            int spielerAnzahl = (Integer) spinnerSpieler.getValue();
           btnStartServer.setEnabled(false);
            lblStatus.setText((spielLaden ? "Lade Spiel für " : "Server startet für ") + spielerAnzahl + " Spieler...");

            new Thread(()-> {
                SpielServer.startServer(spielLaden, spielerAnzahl, lblStatus);
            }).start();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerGUI::new);
    }
}
