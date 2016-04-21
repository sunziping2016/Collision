package view;

/**
 * Created by Sun on 3/21/2016.
 *
 * Load image file.
 */

import java.awt.Image;
import java.awt.image.*;
import javax.imageio.*;
import java.util.*;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.geom.*;
import java.io.InputStream;
import java.util.logging.Logger;

class ImageManager {
    private final static Logger logger = Logger.getLogger(ImageManager.class.getName());

    //set to 1.0 for non-Retina image instances
    static final float REAL_DENSITY = 2.0f;

    private static HashMap<String, BufferedImage> cache = new HashMap<>();
    private static HashMap<String, Image> instanceCache = new HashMap<>();
    private static Properties defaultScales = null;

    static {
        try {
            InputStream is = ImageManager.class.getResource("../properties/DEFAULT_SCALES.properties").openStream();
            Properties p = new Properties();
            p.load(is);
            is.close();
            defaultScales = p;
        } catch (Exception e) {
            defaultScales = new Properties();
            //logger.warning("Failed to DEFAULT_SCALES.properties.");
        }
    }

    private static void load(String name) throws Exception {
        BufferedImage b = ImageIO.read(ImageManager.class.getResource("../images/" + name + ".png"));
        cache.put(name, b);
        //logger.info(String.format("Loaded image resource %s.", name));
    }

    public static BufferedImage getImage(String name) {
        if (!cache.containsKey(name)) {
            try {
                load(name);
            } catch (Exception e) {
                //logger.warning(String.format("Failed to load image resource %s.", name));
                return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            }
        }
        return cache.get(name);
    }

    public static ImageIcon getIcon(String name) {
        return new ImageIcon(ImageManager.getImage(name).getScaledInstance(64, 64, Image.SCALE_SMOOTH));
    }

    public static Image getImageInstance(String name, float xScale, float yScale, float rotation) {
        // Check if an instance with given scale and rotation is already in the instanceCache
        String key = String.format("%s@xs=%.5f;ys=%.5f;rot=%.5f", name, xScale, yScale, rotation);
        if (instanceCache.containsKey(key))
            return instanceCache.get(key);
        BufferedImage raw = getImage(name);
        float defaultScale = getDefaultScale(name);
        Image tbr = null;
        // If the image is too small, don't try to do transforms on it
        if (raw.getWidth() <= 10 || raw.getHeight() <= 10 || xScale*raw.getWidth() < 5 || yScale*raw.getHeight() < 5)
            return raw;
        try {
            tbr = getImageInstance(raw, xScale * defaultScale, yScale * defaultScale, rotation);
        } catch (Exception e) {
            logger.warning(String.format("Failed to load image resource %s.", name));
            return raw;
        }
        if (tbr == null)
            return raw;
        instanceCache.put(key, tbr);
        return tbr;

    }

    private static Image getImageInstance(BufferedImage raw, float xScale, float yScale, float rotation) {
        //onPaint the rotated image at full (raw file pixels) size
        BufferedImage step1 = new BufferedImage(raw.getWidth() * 2, raw.getHeight() * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D s1g = (Graphics2D)step1.getGraphics();
        AffineTransform s1tf = new AffineTransform();
        s1tf.concatenate(AffineTransform.getTranslateInstance(raw.getWidth() * 0.5f, raw.getHeight() * 0.5f));
        s1tf.concatenate(AffineTransform.getRotateInstance(rotation / 180f * Math.PI, raw.getWidth() * 0.5f, raw.getHeight() * 0.5f));
        s1g.drawImage(raw, s1tf, null);
        s1g.dispose();
        //scale the rotated image to the correct resolution for the screen
        return step1.getScaledInstance((int)Math.round(step1.getWidth() * xScale * REAL_DENSITY), (int)Math.round(step1.getHeight() * yScale * REAL_DENSITY), Image.SCALE_SMOOTH);
    }

    public static float getDefaultScale(String name) {
        String val = defaultScales.getProperty(name);
        if (val == null)
            return 1.0f;
        try {
            return Float.parseFloat(val);
        } catch (Exception e) {
            return 1.0f;
        }

    }
}