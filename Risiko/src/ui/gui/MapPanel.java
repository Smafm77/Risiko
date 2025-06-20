package ui.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MapPanel extends JPanel {
    private BufferedImage img;
    public MapPanel(){
        try {
            img = ImageIO.read(new File("map-front.png"));
        } catch (IOException e){
            img = null;
        }
    }
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if(img != null) {
            int w = getWidth();
            int h = getHeight();
            g.drawImage(img, 0, 0, w, h, null);
        }
        //ToDo: Truppen, Besitzer, etc
    }
}
