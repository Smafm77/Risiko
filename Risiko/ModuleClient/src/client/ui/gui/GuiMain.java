package client.ui.gui;

import client.net.RisikoClient;

import javax.swing.*;
import java.awt.*;

public class GuiMain extends JFrame {
    private final RisikoClient client;
    protected final JButton btnStartGame = new JButton("Spiel starten");


    public  GuiMain(RisikoClient client) {
        super("Risiko - Client");
        this.client = client;
        setLayout(new BorderLayout());
        add(btnStartGame, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void showWithListener(){
        btnStartGame.addActionListener(new StartGameListener(this, client));
        setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientLoginDialog::new);
    }
}
