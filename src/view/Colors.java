package view;

import java.awt.*;

/**
 * Created by Sun on 4/15/2016.
 *
 * Colors.
 */
public class Colors {
    public static final Color LEAP_WARNING_OVERLAY = new Color(150, 0, 0, 150);
    public static final Color LEAP_WARNING_TEXT = Color.white;

    public static Color mixtue(Color a, Color b, float r) {
        return new Color((int) (a.getRed() * r + b.getRed() * (1.0f - r) + 0.5),
                (int) (a.getGreen() * r + b.getGreen() * (1.0f - r) + 0.5),
                (int) (a.getBlue() * r + b.getBlue() * (1.0f - r) + 0.5));
   }
}
