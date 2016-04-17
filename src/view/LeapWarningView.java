package view;

/**
 * Created by Sun on 4/15/2016.
 *
 * Class for displaying a warning message when leap motion controller is plugged off.
 */
public class LeapWarningView extends View {
    public void draw(GraphicsWrapper g2, boolean active) {
        if (!active) return;
        g2.fillRect(0, 0, 16, 10, Colors.LEAP_WARNING_OVERLAY);
        g2.drawStringCentered("The Leap Motion Controller is disconnected or\nthe Leap Motion Controller service is not running.\nPlease correct this before continuing.", 0.5f, Colors.LEAP_WARNING_TEXT, 8f, 7f);
        g2.drawImage("leapWarn", 8f, 4f);
    }
}
