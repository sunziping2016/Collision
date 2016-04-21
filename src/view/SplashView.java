package view;

import controller.GameController;
import controller.GameListener;
import model.Ball;
import model.GameModel;
import org.jbox2d.common.Vec2;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Sun on 4/19/2016.
 *
 * Splash view.
 */
public class SplashView extends View implements RadialMenuListener, GameListener {
    private RadialMenu menu;

    private static final int SPLASH_MENU_EXIT_GAME = 0;
    private static final int SPLASH_MENU_LEADERBOARD = 1;
    private static final int SPLASH_MENU_PLAY_ONE = 2;
    private static final int SPLASH_MENU_PLAY_TWO = 3;

    private GameController gameController;
    private final GameModel gameModel;

    public SplashView(GameController gameController) {
        this.gameController = gameController;
        this.gameModel = gameController.getGameModel();
        menu = new RadialMenu(16.5f, 5f, this);
        menu.addItem(new RadialMenuItem(SPLASH_MENU_LEADERBOARD, "Leaderboard", "leaderboard", 130, 20, RadialMenuItem.ORIENT_LEFT));
        menu.addItem(new RadialMenuItem(SPLASH_MENU_PLAY_ONE, "Single Player", "play", 150, 30, RadialMenuItem.ORIENT_LEFT));
        menu.addItem(new RadialMenuItem(SPLASH_MENU_PLAY_TWO, "Multiple Player", "twoplayer", 180, 30, RadialMenuItem.ORIENT_LEFT));
        menu.addItem(new RadialMenuItem(SPLASH_MENU_EXIT_GAME, "Exit Game", "exit", 210, 20, RadialMenuItem.ORIENT_LEFT));
    }

    @Override
    public void onStart() {
        getViewManager().getLeapController().addListener(menu);
        gameController.setBoundary();
        gameController.setnUsers(0);
        gameController.setnBalls(6);
        gameController.setRandomSpeed(12.0f);
        gameController.addListener(this);
        gameController.start();
    }

    @Override
    public void onStop() {
        gameController.stop();
        gameController.removeListener(this);
        getViewManager().getLeapController().removeListener(menu);
    }

    @Override
    public void onPaint(GraphicsWrapper g2) {
        if (!isActive()) return;
        g2.prepare();
        g2.fillRect(0f, 0f, 16f, 10f, Colors.PAUSED);
        synchronized (gameModel) {
            ArrayList<Ball> balls = gameController.getGameModel().systemBalls;
            for (Ball i : balls) {
                Vec2 pos = i.getBody().getPosition();;
                g2.fillCircle(pos.x + 8.0f, 10.0f - pos.y, i.getFixture().getShape().getRadius(), Colors.mixtue(i.getColor(), Colors.PAUSED, 0.5f));
            }
        }
        g2.drawImage("gameLogo", 5f, 4f);
        g2.drawStringCentered("Welcome to Collision.", 0.4f, Color.WHITE, 8f, 9f);
        menu.draw(g2);
    }

    @Override
    public void onMenuSelection(int id) {
        switch (id) {
            case SPLASH_MENU_PLAY_ONE:
                getViewManager().pushView("game", new Content().putInt("nusers", 1));
                break;
            case SPLASH_MENU_PLAY_TWO:
                getViewManager().pushView("game", new Content().putInt("nusers", 2));
                break;
            case SPLASH_MENU_LEADERBOARD:
                getViewManager().pushView("leaderboard");
                break;
            case SPLASH_MENU_EXIT_GAME:
                System.exit(0);
                break;
            default:
                break;
        }
        if (id != -1)
            SoundManager.play("menuChoice");
    }

    @Override
    public void onGameUpdate() {
        if (isActive())
            getViewManager().repaint();
    }
}
