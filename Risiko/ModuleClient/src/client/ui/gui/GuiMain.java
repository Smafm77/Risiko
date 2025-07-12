package client.ui.gui;

import javax.swing.*;
import java.awt.*;

public class GuiMain extends JFrame {

    protected final JButton btnStartGame = new JButton("Spiel starten");


    public  GuiMain() {
        super("Risiko - Client");
        setLayout(new BorderLayout());
        add(btnStartGame, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void showWithListener(){
        btnStartGame.addActionListener(new StartGameListener(this));
        setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientLoginDialog::new);
    }
}
