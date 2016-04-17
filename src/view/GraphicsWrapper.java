package view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * Created by Sun on 4/6/2016.
 *
 * Graphics wrapper.
 */
public class GraphicsWrapper {
    static final int PRECISION_FACTOR = 1000;
    static final String FONT_NAME = "Arial";
    private Graphics2D g2;
    private Graphics2D originalGraphics;
    private JComponent canvas;

    public GraphicsWrapper(Graphics g, JComponent canvas) {
        this.originalGraphics = (Graphics2D)g;
        this.canvas = canvas;
        this.g2 = this.originalGraphics;

        //enable antialiasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        //configure transforms for 16x10 coordinate space
        float scale = PRECISION_FACTOR;
        float xScale = ((float)canvas.getWidth()) / (16.0f * scale);
        float yScale = ((float)canvas.getHeight()) / (10.0f * scale);
        AffineTransform t = g2.getTransform();
        g2.setTransform(new AffineTransform(xScale, 0.0, 0.0, yScale, t.getTranslateX(), t.getTranslateY()));
        fillRect(0, 0, 16, 16, Color.WHITE);
    }
    public void drawString(String s, float fontSize, Color c, float x, float y, boolean centered) {
        //Support multiline strings
        String[] lines = s.split("\n");
        if (lines.length > 1) {
            for (String line : lines) {
                drawString(line, fontSize, c, x, y, centered);
                y += fontSize * 1.2f;
            }
            return;
        }
        float scale = PRECISION_FACTOR;
        Font f = new Font(FONT_NAME, 0, (int)Math.round(fontSize * scale));
        g2.setFont(f);
        g2.setColor(c);
        float w = centered ? g2.getFontMetrics(f).stringWidth(s) : 0;
        g2.drawString(s, x*scale - w/2.0f, y*scale);
    }
    //These are here to avoid having to change code using them elsewhere
    public void drawStringCentered(String s, float fontSize, Color c, float x, float y) {
        drawString(s, fontSize, c, x, y, true);
    }
    public void drawString(String s, float fontSize, Color c, float x, float y) {
        drawString(s, fontSize, c, x, y, false);
    }
    public void drawImage(String imgName, float x, float y) {
        drawImage(imgName, x, y, true);
    }
    public void fillRect(float x, float y, float w, float h, Color c) {
        float scale = PRECISION_FACTOR;
        g2.setColor(c);
        g2.fillRect(
                (int)Math.round(x*scale),
                (int)Math.round(y*scale),
                (int)Math.round(w*scale),
                (int)Math.round(h*scale)
        );
    }

    //if useInstanceCache is false, do the rotate transform here, not at the instance cache level
    //this is desirable if the image will be rotated often (i.e. on a body) to avoid running out of memory by filling up the instance cache
    public void drawImage(String imgName, float x, float y, boolean useInstanceCache) {

        float scale = PRECISION_FACTOR;
        float scaleX = ((float)canvas.getWidth()) / (16.0f * scale);
        float scaleY = ((float)canvas.getHeight()) / (10.0f * scale);

        float cumTransX = (float)(g2.getTransform().getTranslateX() - originalGraphics.getTransform().getTranslateX());
        float cumTransY = (float)(g2.getTransform().getTranslateY() - originalGraphics.getTransform().getTranslateY());
        float realX = (x / 16f) * canvas.getWidth() + cumTransX;
        float realY = (y / 10f) * canvas.getHeight() + cumTransY;

        if (!useInstanceCache) {

            BufferedImage image = ImageManager.getImage(imgName);
            float defaultScale = ImageManager.getDefaultScale(imgName);

            AffineTransform tform = new AffineTransform();
            tform.concatenate(AffineTransform.getTranslateInstance(realX, realY));
            tform.concatenate(AffineTransform.getTranslateInstance(-image.getWidth()*scaleX*0.5f, -image.getHeight()*scaleY*0.5f));
            tform.concatenate(AffineTransform.getScaleInstance(scaleX*defaultScale, scaleY*defaultScale));

            originalGraphics.drawImage(image, tform, null);

        } else {

            Image inst = ImageManager.getImageInstance(imgName, scaleX, scaleY, 0);
            AffineTransform tf = new AffineTransform();
            tf.concatenate(AffineTransform.getTranslateInstance(realX - inst.getWidth(null)/2f/ImageManager.REAL_DENSITY, realY - inst.getHeight(null)/2f/ImageManager.REAL_DENSITY));
            if (ImageManager.REAL_DENSITY != 1.0f)
                tf.concatenate(AffineTransform.getScaleInstance(1/ImageManager.REAL_DENSITY, 1/ImageManager.REAL_DENSITY));
            originalGraphics.drawImage(inst, tf, null);
        }
    }

    public void fillCircle(float x, float y, float radius, Color c) {
        float scale = PRECISION_FACTOR;
        x -= radius;
        y -= radius;
        g2.setColor(c);
        g2.fillOval(
                (int)Math.round(x * scale),
                (int)Math.round(y * scale),
                (int)Math.round(2 * radius * scale),
                (int)Math.round(2 * radius * scale)
        );
    }
}
