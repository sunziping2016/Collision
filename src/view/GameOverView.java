package view;

import controller.LeapController;
import controller.Timer;

import java.awt.*;

/**
 * Created by Sun on 4/21/2016.
 *
 * Game over view.
 */
public class GameOverView extends View implements RadialMenuListener {
    private RadialMenu menu;
    private Timer time;
    private boolean high;

    private static final int GAMEOVER_MENU_RETURN = 0;
    private static final int GAMEOVER_MENU_NEWGAME = 1;
    private static final int GAMEOVER_MENU_EXIT_GAME = 2;
    private static final int GAMEOVER_MENU_LEADERBOARD = 3;

    public GameOverView() {
        menu = new RadialMenu(8, 11.5f, this);
        menu.addItem(new RadialMenuItem(GAMEOVER_MENU_RETURN, "Back", "menuReturn", 120, 20, RadialMenuItem.ORIENT_TOP));
        menu.addItem(new RadialMenuItem(GAMEOVER_MENU_NEWGAME, "New Game", "newGame", 90, 30, RadialMenuItem.ORIENT_TOP));
        menu.addItem(new RadialMenuItem(GAMEOVER_MENU_LEADERBOARD, "High Scores", "leaderboard", 60, 30, RadialMenuItem.ORIENT_TOP));
        menu.addItem(new RadialMenuItem(GAMEOVER_MENU_EXIT_GAME, "Exit Game", "exit", 40, 20, RadialMenuItem.ORIENT_TOP));
    }

    @Override
    public void onStart(Content content) {
        if (content.getString("method", "").equals("push"))
            time = (Timer) content.getObject("time", null);
        if (content.getInt("high", 0) == 1)
            high = true;
        LeapController.getLeapController().addPointerListener(menu);
    }

    @Override
    public void onStop() {
        LeapController.getLeapController().removePointerListener(menu);
    }

    @Override
    public void onMenuSelection(int id) {
        switch (id) {
            case GAMEOVER_MENU_RETURN:
                getViewManager().popTo("splash");
                break;
            case GAMEOVER_MENU_NEWGAME:
                getViewManager().popView();
                break;
            case GAMEOVER_MENU_EXIT_GAME:
                getViewManager().close();
                break;
            case GAMEOVER_MENU_LEADERBOARD:
                getViewManager().pushView("leaderboard");
                break;
            default:
                break;
        }
        if (id != -1)
            SoundManager.play("menuChoice");
        getViewManager().repaint();
    }

    @Override
    public void onPaint(GraphicsWrapper g2) {
        if (!isActive()) return;
        g2.prepare();
        getViewManager().getView("game").onPaint(g2);
        g2.fillRect(0.0f, 0.0f, 16.0f, 10.0f, Colors.BACKGROUND);
        g2.drawStringCentered("GAME OVER", 2.0f, Colors.PAUSED_TEXT, 8, 3);
        if (time != null)
            g2.drawStringCentered("Time: " + time, 0.5f, Color.WHITE, 8, 5);
        if (high)
            g2.drawStringCentered("A new high score!", 0.5f, Color.WHITE, 8, 5.6f);
        menu.draw(g2);
    }
}
