package ui.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoadGameListener implements ActionListener {
    private final GuiMain gui;

    LoadGameListener(GuiMain gui) {
        this.gui = gui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Spielstand laden");
        int result = chooser.showOpenDialog(gui);
        if (result == JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(gui, "Lade " + chooser.getSelectedFile().getName(), "Laden", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}
