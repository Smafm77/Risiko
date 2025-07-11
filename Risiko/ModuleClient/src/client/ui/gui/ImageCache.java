package client.ui.gui;

import common.valueobjects.Land;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ImageCache {

    public static final BufferedImage mapFront;
    public static final BufferedImage mapBack;
    public static final Map<String, BufferedImage> playerIcons;
    public static final Map<String, BufferedImage> landOverlayImages = new HashMap<>();

    static {
        try {
            mapFront = ImageIO.read(new File("Risiko/Grafiken/map-front.png"));
            mapBack = ImageIO.read(new File("Risiko/Grafiken/map-back.png"));
            playerIcons = Map.of("Blau", ImageIO.read(new File("Risiko/Grafiken/playericon_blau.png")),
                    "Gelb", ImageIO.read(new File("Risiko/Grafiken/playericon_gelb.png")),
                    "Gruen", ImageIO.read(new File("Risiko/Grafiken/playericon_gruen.png")),
                    "Orange", ImageIO.read(new File("Risiko/Grafiken/playericon_orange.png")),
                    "Rot", ImageIO.read(new File("Risiko/Grafiken/playericon_rot.png")),
                    "Violett", ImageIO.read(new File("Risiko/Grafiken/playericon_violett.png")));

        } catch (IOException e) {
            throw new RuntimeException("Grafiken konnten nicht geladen werden.", e);
        }
    }

    public static void ladeOverlays(ArrayList<Land> laenderliste) {
        for (Land land : laenderliste) {
            try {
                String path = "Risiko/Grafiken/overlays/overlay-" + land.getName() + ".png";
                BufferedImage overlayImg = ImageIO.read(new File(path));
                landOverlayImages.put(land.getName(), overlayImg);
            } catch (IOException e) {
                landOverlayImages.put(land.getName(), null);
            }
        }
    }
}
