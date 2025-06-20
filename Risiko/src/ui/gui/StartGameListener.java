package ui.gui;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartGameListener implements ActionListener {
    private final GuiMain gui;

    StartGameListener(GuiMain gui) {
        this.gui = gui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JOptionPane.showMessageDialog(gui, "Spiel startet: " + gui.getListModel().getSize(), "Start", JOptionPane.INFORMATION_MESSAGE);
    }
}
