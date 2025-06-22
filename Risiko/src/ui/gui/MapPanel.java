package ui.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MapPanel extends JPanel {
    private BufferedImage img;
    private BufferedImage bgImg;
    public MapPanel(){
        try {
            img = ImageIO.read(new File("map-front.png"));
            bgImg = ImageIO.read(new File("map-back.png"));
        } catch (IOException e){
            img = null;
            bgImg = null;
        }
    }
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if(img != null && bgImg != null) {
            int w = getWidth();
            int h = getHeight();
            g.drawImage(bgImg, 0, 0, w, h, null);
            g.drawImage(img, 0, 0, w, h, null);

        }
        //ToDo: Truppen, Besitzer, etc
    }
}
