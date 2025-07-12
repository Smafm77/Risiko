package client.ui.gui;

import common.valueobjects.Spieler;

import javax.swing.*;
import java.awt.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientLoginDialog extends JFrame {
    public ClientLoginDialog() {
        setTitle("Mit Server verbinden...");
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        ArrayList<String> farbenList = new ArrayList<>(Arrays.asList("Rot", "Blau", "Gruen", "Gelb", "Orange", "Violett"));
        JComboBox<String> cboColor = new JComboBox<>(farbenList.toArray(new String[0]));
        JTextField tfName = new JTextField();
        JButton btnConnect = new JButton("Verbinden");

        JPanel p = new JPanel(new GridLayout(2, 2));
        p.add(new JLabel("Name:"));
        p.add(tfName);
        p.add(new JLabel("Farbe:"));
        p.add(cboColor);
        add(p, BorderLayout.CENTER);
        add(btnConnect, BorderLayout.SOUTH);

        btnConnect.addActionListener(e -> {
            String name = tfName.getText().trim();
            String color = (String) cboColor.getSelectedItem();
            if (name.isEmpty() || color == null) {
                JOptionPane.showMessageDialog(this, "Bitte Name und Farbe angeben!");
                return;
            }
            try {
                Socket s = new Socket("localhost", 1399);
                PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                out.println(name + "," + color);
                String response = in.readLine();
                if (!"OK".equals(response)) {
                    JOptionPane.showMessageDialog(this, "Server: " + response);
                    s.close();
                    return;
                }
                this.dispose();
                SwingUtilities.invokeLater(() -> {
                    GuiMain main = new GuiMain();
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

