package client.ui.gui;

import common.exceptions.FalscherBesitzerException;
import common.exceptions.UngueltigeBewegungException;
import common.valueobjects.Land;
import common.valueobjects.Spieler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapPanel extends JPanel {
    private BufferedImage img;
    private BufferedImage bgImg;
    private final Map<Integer, Land> farbwertZuLand = new HashMap<>();
    private LandKlickListener klickListener;
    private final Map<String, Point> landKoordinaten = new HashMap<>();
    private final Map<String, Image> iconByColor = new HashMap<>();
    private final Map<String, Color> spielerFarbe = new HashMap<>();


    private void ladeIcons(){
        try{
            iconByColor.put("Blau", ImageIO.read(new File("playericon_blau.png")));
            iconByColor.put("Gelb", ImageIO.read(new File("playericon_gelb.png")));
            iconByColor.put("Gruen", ImageIO.read(new File("playericon_gruen.png")));
            iconByColor.put("Orange", ImageIO.read(new File("playericon_orange.png")));
            iconByColor.put("Rot", ImageIO.read(new File("playericon_rot.png")));
            iconByColor.put("Violett", ImageIO.read(new File("playericon_violett.png")));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void ladeFarben(){
        spielerFarbe.put("Blau", Color.decode("#001dff"));
        spielerFarbe.put("Gelb", Color.decode("#fff700"));
        spielerFarbe.put("Gruen", Color.decode("#1bff00"));
        spielerFarbe.put("Orange", Color.decode("#ff8300"));
        spielerFarbe.put("Rot", Color.decode("#cc0000"));
        spielerFarbe.put("Violett", Color.decode("#740ece"));
    }

    public MapPanel(ArrayList<Land> laenderListe){
        try {
            img = ImageIO.read(new File("map-front.png"));
            bgImg = ImageIO.read(new File("map-back.png"));
        } catch (IOException e){
            img = null;
            bgImg = null;
        }
        ladeFarben();
        ladeKoordinaten();
        ladeIcons();
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
        Font font = new Font(Font.DIALOG, Font.BOLD, 15);
        g.setFont(font);
        int w = getWidth();
        int h = getHeight();
        g.drawImage(bgImg, 0, 0, w, h, null);
        g.drawImage(img, 0, 0, w, h, null);
        for (Land land : farbwertZuLand.values()) {
            Spieler spieler = land.getBesitzer();
            g.setColor(spielerFarbe.get(spieler.getFarbe()));
            Point p = landKoordinaten.get(land.getName());

            double sx = getWidth() / (double) bgImg.getWidth();
            double sy = getHeight() / (double) bgImg.getHeight();
            int drawX = (int) Math.round(p.x * sx);
            int drawY = (int) Math.round(p.y * sy);

            Image icon = iconByColor.get(spieler.getFarbe());
            if (icon != null){
                g.drawImage(icon, drawX - icon.getWidth(null)/2, drawY - icon.getHeight(null)/2, null);
                String landesEinheiten = String.valueOf(land.getEinheiten());
                g.drawString(landesEinheiten, drawX + 15, drawY + icon.getHeight(null)/2);
            }
        }
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

    private void ladeKoordinaten(){
        try (BufferedReader br = new BufferedReader(new FileReader("LaenderKoordinaten.txt"))){
            String zeile;
            while ((zeile = br.readLine()) != null){
                String[] parts = zeile.split(":");
                String name = parts[0].trim();
                String[] xy = parts[1].split(",");
                int x = Integer.parseInt(xy[0].trim());
                int y = Integer.parseInt(xy[1].trim());
                landKoordinaten.put(name, new Point(x,y));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
