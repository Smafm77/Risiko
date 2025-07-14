package client.ui.gui;

import common.exceptions.FalscherBesitzerException;
import common.exceptions.UngueltigeBewegungException;
import common.valueobjects.ISpiel;
import common.valueobjects.Land;
import common.valueobjects.LandDTO;
import common.valueobjects.Spieler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapPanel extends JPanel {
    private final ISpiel spiel;
    private BufferedImage img;
    private BufferedImage bgImg;
    private final Map<Integer, LandDTO> farbwertZuLand = new HashMap<>();
    private LandKlickListener klickListener;
    private final Map<String, Point> landKoordinaten = new HashMap<>();
    private final Map<String, Image> iconByColor = new HashMap<>();
    private final Map<String, Color> spielerFarbe = new HashMap<>();
    private String overlayHerkunft = null;
    private String overlayZiel = null;
    private String kampfLand = null;
    public BufferedImage currentWinOverlay;
    private Timer gewinnerTimer;


    private void ladeFarben() {
        spielerFarbe.put("Blau", Color.decode("#001dff"));
        spielerFarbe.put("Gelb", Color.decode("#fff700"));
        spielerFarbe.put("Gruen", Color.decode("#1bff00"));
        spielerFarbe.put("Orange", Color.decode("#ff8300"));
        spielerFarbe.put("Rot", Color.decode("#cc0000"));
        spielerFarbe.put("Violett", Color.decode("#740ece"));
    }

    private void ladeIcons() {
        iconByColor.putAll(ImageCache.playerIcons);
    }

    public MapPanel(ArrayList<LandDTO> laenderListe, ISpiel spiel) {
        this.spiel = spiel;
        img = ImageCache.mapFront;
        bgImg = ImageCache.mapBack;

        ladeFarben();
        ladeKoordinaten();
        ladeIcons();
        //ImageCache.ladeWinOverlay();
        ImageCache.ladeOverlays(laenderListe);
        ImageCache.ladeKampfOverlays(laenderListe);
        for (LandDTO land : laenderListe) {
            farbwertZuLand.put(land.getFarbe(), land);
        }
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                LandDTO land = getLandAt(e.getX(), e.getY());
                if (land != null && klickListener != null) {
                    try {
                        klickListener.landAngeklickt(land);
                    }    catch (RuntimeException | FalscherBesitzerException | UngueltigeBewegungException  ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
                }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Font font = new Font(Font.DIALOG, Font.BOLD, 15);
        g.setFont(font);
        int w = getWidth();
        int h = getHeight();
        g.drawImage(bgImg, 0, 0, w, h, null);
        g.drawImage(img, 0, 0, w, h, null);

        for (Land land : spiel.getWelt().getAlleLaender()) {
            Spieler spieler = land.getBesitzer();
            g.setColor(spielerFarbe.get(spieler.getFarbe()));
            Point p = landKoordinaten.get(land.getName());

            double sx = getWidth() / (double) bgImg.getWidth();
            double sy = getHeight() / (double) bgImg.getHeight();
            int drawX = (int) Math.round(p.x * sx);
            int drawY = (int) Math.round(p.y * sy);

            Image icon = iconByColor.get(spieler.getFarbe());
            if (icon != null) {
                g.drawImage(icon, drawX - icon.getWidth(null) / 2, drawY - icon.getHeight(null) / 2, null);
                String landesEinheiten = String.valueOf(land.getEinheiten());
                g.drawString(landesEinheiten, drawX + 15, drawY + icon.getHeight(null) / 2);
            }
        }
        if (overlayHerkunft != null) {
            BufferedImage overlay = ImageCache.landOverlayImages.get(overlayHerkunft);
            if (overlay != null) {
                g.drawImage(overlay, 0, 0, getWidth(), getHeight(), null);
            }
        }
        if (overlayZiel != null) {
            BufferedImage overlay = ImageCache.landOverlayImages.get(overlayZiel);
            if (overlay != null) {
                g.drawImage(overlay, 0, 0, getWidth(), getHeight(), null);
            }
        }
        if (kampfLand != null) {
            BufferedImage overlay = ImageCache.kampfOverlayImages.get(kampfLand);
            if (overlay != null) {
                g.drawImage(overlay, 0, 0, getWidth(), getHeight(), null);
            }
        }
        if(currentWinOverlay !=null){
            g.drawImage(currentWinOverlay, 0, 0, getWidth(),getHeight(),null);
        }
    }

    public LandDTO getLandAt(int x, int y) {
        if (bgImg == null) return null;

        double scaleX = bgImg.getWidth() / (double) getWidth();
        double scaleY = bgImg.getHeight() / (double) getHeight();
        int realX = (int) (x * scaleX);
        int realY = (int) (y * scaleY);
        if (realX < 0 || realY < 0 || realX >= bgImg.getWidth() || realY >= bgImg.getHeight()) {
            return null;
        }
        int rgb = bgImg.getRGB(realX, realY) & 0xFFFFFF;
        return farbwertZuLand.get(rgb);
    }

    public void setLandKlickListener(LandKlickListener listener) {
        this.klickListener = listener;
    }

    private void ladeKoordinaten() {
        try (BufferedReader br = new BufferedReader(new FileReader("Risiko/Txt-dateien/LaenderKoordinaten.txt"))) {
            String zeile;
            while ((zeile = br.readLine()) != null) {
                String[] parts = zeile.split(":");
                String name = parts[0].trim();
                String[] xy = parts[1].split(",");
                int x = Integer.parseInt(xy[0].trim());
                int y = Integer.parseInt(xy[1].trim());
                landKoordinaten.put(name, new Point(x, y));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void zeigeKampfLand(String landName){
        kampfLand = landName;
        repaint();
    }

    public void beendeKampfLand(){
        kampfLand = null;
        repaint();
    }

    public void zeigeOverlayHerkunft(String landName) {
        overlayHerkunft = landName;
        repaint();
    }

    public void zeigeOverlayZiel(String landName) {
        overlayZiel = landName;
        repaint();
    }

    public void versteckeOverlay() {
        overlayHerkunft = null;
        overlayZiel = null;
        repaint();
    }

    public void gewinnerAnimation(){
        if(gewinnerTimer != null  && gewinnerTimer.isRunning()){
            return;
        }
        gewinnerTimer = new Timer(500, new ActionListener() {
            int i = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                    currentWinOverlay = ImageCache.winOverlays[i% ImageCache.winOverlays.length];
                    i++;
                    repaint();
            }
        });
        gewinnerTimer.setRepeats(true);
        gewinnerTimer.start();
    }
}
