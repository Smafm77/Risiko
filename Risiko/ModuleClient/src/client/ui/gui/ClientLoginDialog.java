package client.ui.gui;

import client.net.RisikoClient;

import javax.swing.*;
import java.awt.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientLoginDialog extends JFrame {
    public ClientLoginDialog() {
        setTitle("Mit Server verbinden...");
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        JTextField tfName = new JTextField();
        JButton btnConnect = new JButton("Verbinden");

        JPanel p = new JPanel(new GridLayout(1, 2));
        p.add(new JLabel("Name:"));
        p.add(tfName);

        add(p, BorderLayout.CENTER);
        add(btnConnect, BorderLayout.SOUTH);

        btnConnect.addActionListener(e -> {
            String name = tfName.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bitte Namen angeben!");
                return;
            }
            try {
                Socket s = new Socket("localhost", 1399);
                PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                out.println(name);
                String response = in.readLine();
                if (!response.startsWith("OK")) {
                    JOptionPane.showMessageDialog(this, "Server: " + response);
                    s.close();
                    return;
                }
                String spielerColor = response.split(":", 2)[1];
                this.dispose();
                RisikoClient client = new RisikoClient(s, name, spielerColor);
                SwingUtilities.invokeLater(() -> {
                    GuiMain main = new GuiMain(client);
                    main.showWithListener();
                });
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Verbindung fehlgeschlagen!");
                ex.printStackTrace();
            }
        });
        setLocationRelativeTo(null);
        setVisible(true);
    }


}

