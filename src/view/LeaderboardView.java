package view;

import controller.LeapController;
import controller.Timer;

import javax.swing.*;
import java.util.*;

/**
 * Created by Sun on 4/21/2016.
 *
 * Leaderboard view.
 */
public class LeaderboardView extends View implements RadialMenuListener {
    private RadialMenu menu;
    private static final int LEADERBOARD_MENU_CLEAR = 0;
    private static final int LEADERBOARD_MENU_EXIT = 1;
    private static final Leaderboard leaderboard = Leaderboard.getLeaderboard();

    public LeaderboardView() {
        menu = new RadialMenu(8, 11.5f, this);
        menu.addItem(new RadialMenuItem(LEADERBOARD_MENU_EXIT, "Back", "menuReturn", 90, 20, RadialMenuItem.ORIENT_TOP));
        menu.addItem(new RadialMenuItem(LEADERBOARD_MENU_CLEAR, "Reset Scores", "clearLeaderboard", 70, 20, RadialMenuItem.ORIENT_TOP));
    }

    @Override
    public void onPaint(GraphicsWrapper g2) {
        if (!isActive()) return;
        g2.prepare();
        g2.fillRect(0.0f, 0.0f, 16.0f, 10.0f, Colors.BACKGROUND);
        if (leaderboard.topList.size() > 0) {
            for (int i = 0; i < leaderboard.topList.size(); i++) {
                Leaderboard.Entry e = leaderboard.topList.get(i);
                String msg = String.format("#%d: %10s:  ", i + 1, e.name) + Timer.toString(e.score);
                g2.drawString(msg, 0.5f, Colors.LEADERBOARD, 5, 1.8f + 0.6f * i);
            }
        }
        else
            g2.drawStringCentered("No High Scores", 1, Colors.LEADERBOARD, 8, 5);
        menu.draw(g2);
    }

    @Override
    public void onStart(Content content) {
        LeapController.getLeapController().addPointerListener(menu);
    }

    @Override
    public void onStop() {
        LeapController.getLeapController().removePointerListener(menu);
    }

    public void clearLeaderboard() {
        //LeapController.getLeapController().removePointerListener(menu);
        //int confirmation = JOptionPane.showConfirmDialog(getViewManager(), "Are you sure you want to clear all high scores?", "Clear High Scores", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, ImageManager.getIcon("clearLeaderboard"));
        //if (confirmation == JOptionPane.OK_OPTION)
            leaderboard.clear();
        //LeapController.getLeapController().addPointerListener(menu);
    }

    @Override
    public void onMenuSelection(int id) {
        if (id != -1) {
            SoundManager.play("menuChoice");
            switch (id) {
                case LEADERBOARD_MENU_CLEAR:
                    clearLeaderboard();
                    break;
                case LEADERBOARD_MENU_EXIT:
                    getViewManager().popView();
                    break;
                default:
                    break;
            }
        }
        getViewManager().repaint();
    }
}
