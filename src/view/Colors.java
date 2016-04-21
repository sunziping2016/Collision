package view;

import java.awt.*;

/**
 * Created by Sun on 4/15/2016.
 *
 * Colors.
 */
public class Colors {
    public static final Color LETTERBOX = new Color(39,40,34);
    public static final Color BACKGROUND = new Color(39,40,34);
    public static final Color PAUSED = new Color(128,128,128);
    public static final Color PAUSED_TEXT = new Color(150,150,150);
    public static final Color LEADERBOARD = Color.WHITE;

    public static final Color LEAP_WARNING_OVERLAY = new Color(150, 0, 0, 150);
    public static final Color LEAP_WARNING_TEXT = Color.white;

    public static final Color MENU_GAP = new Color(20,20,20,150);
    public static final Color MENU_ITEM = new Color(180,180,180);
    public static final Color MENU_ITEM_SELECTED = new Color(0x20D972);
    public static final Color MENU_TOOLTIP = new Color(255,255,255);
    public static final Color MENU_CURSOR = new Color(255,0,0,150);

    public static final Color[] USER_BALL = new Color[] {
            Color.RED,
            Color.BLUE,
            Color.GREEN
    };
    public static final Color SYSTEM_BALL = new Color(0xDEB887);
    public static final Color BOUNDARY = new Color(0xDEB887);


    public static Color mixtue(Color a, Color b, float r) {
        return new Color((int) (a.getRed() * r + b.getRed() * (1.0f - r) + 0.5),
                (int) (a.getGreen() * r + b.getGreen() * (1.0f - r) + 0.5),
                (int) (a.getBlue() * r + b.getBlue() * (1.0f - r) + 0.5));
   }
}
