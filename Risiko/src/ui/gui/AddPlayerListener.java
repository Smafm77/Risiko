package ui.gui;

import valueobjects.Spieler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddPlayerListener implements ActionListener {
    private final GuiMain gui;

    AddPlayerListener(GuiMain gui) {
        this.gui = gui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String name = gui.getTfPlayerName().getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(gui,
                    "Bitte einen Spielernamen angeben!",
                    "Eingabefehler",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String color = (String) gui.getCboColor().getSelectedItem();
        gui.getListModel().addElement(name + "(" + color + ")");
        gui.getTfPlayerName().setText("");
        gui.getGuiSpieler().add(new Spieler(name, color));
        gui.updateStartButtonState();
    }
}
