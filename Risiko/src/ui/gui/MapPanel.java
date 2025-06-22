package ui.gui;

import exceptions.FalscherBesitzerException;
import exceptions.UngueltigeBewegungException;
import valueobjects.Land;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapPanel extends JPanel {
    private BufferedImage img;
    private BufferedImage bgImg;
    private Map<Integer, Land> farbwertZuLand = new HashMap<>();
    private LandKlickListener klickListener;

    public MapPanel(ArrayList<Land> laenderListe){
        try {
            img = ImageIO.read(new File("map-front.png"));
            bgImg = ImageIO.read(new File("map-back.png"));
        } catch (IOException e){
            img = null;
            bgImg = null;
        }
        for (Land land : laenderListe){
            farbwertZuLand.put(land.getFarbe(), land);
        }
        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                Land land = getLandAt(e.getX(), e.getY());
                if(land != null && klickListener != null){
                    try {
                        klickListener.landAngeklickt(land);
                    } catch (FalscherBesitzerException | UngueltigeBewegungException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
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

    public Land getLandAt(int x, int y){
        if (bgImg == null) return null;

        double scaleX = bgImg.getWidth() / (double) getWidth();
        double scaleY = bgImg.getHeight() / (double) getHeight();
        int realX = (int)(x * scaleX);
        int realY = (int)(y * scaleY);
        if(realX < 0 || realY < 0 || realX >= bgImg.getWidth() || realY >= bgImg.getHeight()){
            return null;
        }
        int rgb = bgImg.getRGB(realX, realY) & 0xFFFFFF;
        return farbwertZuLand.get(rgb);
    }

    public void setLandKlickListener(LandKlickListener listener){
        this.klickListener = listener;
    }

}
