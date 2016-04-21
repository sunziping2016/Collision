package view;

import controller.GameController;
import controller.GameListener;
import model.Ball;
import model.GameModel;
import org.jbox2d.common.Vec2;

import java.awt.*;
import java.util.HashMap;

/**
 * Created by Sun on 4/6/2016.
 *
 * GameView class.
 */
public class GameView extends View implements GameListener {
    private final GameController gameController;
    private final GameModel gameModel;

    public GameView(GameController gameController) {
        this.gameController = gameController;
        gameModel = gameController.getGameModel();
    }

    @Override
    public void onPaint(GraphicsWrapper g2) {
        if (!isActive()) return;
        g2.prepare();
        g2.fillRect(0.0f, 0.0f, 16.0f, 10.0f, Colors.BACKGROUND);
        synchronized (gameModel) {
            g2.maskRectangle(gameModel.corners[0] + 8.0f, 10.0f - gameModel.corners[1], (gameModel.corners[2] - gameModel.corners[0]), (gameModel.corners[1] - gameModel.corners[3]));
            for (int i = 0; i < gameModel.userBalls.size(); ++i){
                Ball ball = gameModel.userBalls.get(i);
                if (!ball.isDead) continue;
                Vec2 pos = ball.getBody().getPosition();
                Color color = ball.getColor();
                g2.fillBall(pos.x + 8.0f, 10.0f - pos.y, ball.getFixture().getShape().getRadius(), color);
            }
            for (Ball i : gameModel.systemBalls) {
                Vec2 pos = i.getBody().getPosition();;
                g2.fillBall(pos.x + 8.0f, 10.0f - pos.y, i.getFixture().getShape().getRadius(), i.getColor());
                //System.out.println(i + " " + i.getBody().getLinearVelocity() + " " + i.getBody().getLinearVelocity().length());
            }
            for (int i = 0; i < gameModel.userBalls.size(); ++i){
                Ball ball = gameModel.userBalls.get(i);
                if (ball.isDead) continue;
                Vec2 pos = ball.getBody().getPosition();
                Color color = ball.getColor();
                g2.fillBall(pos.x + 8.0f, 10.0f - pos.y, ball.getFixture().getShape().getRadius(), color);
            }
            g2.maskClear();
            g2.fillBoundary(gameModel.corners[0] + 8.0f, 10.0f - gameModel.corners[1], gameModel.corners[2] + 8.0f, 10.0f - gameModel.corners[3], 0.3f, Colors.BOUNDARY);
        }
        g2.drawString(gameController.getTimer().toString(), 0.3f, Color.WHITE, 13f, 1f, false);
    }

    @Override
    public void onCreate(Content content) {
        gameController.setBoundary(-7.5f, 9.5f, 7.5f, 0.4f);
        gameController.setnBalls(4);
        gameController.setnUsers(content.getInt("nusers", 1));
        gameController.setRandomSpeed(6.0f);
        gameController.clearTimer();
    }

    @Override
    public void onStart() {
        gameController.addListener(this);
        gameController.start();
    }

    @Override
    public void onStop() {
        gameController.stop();
        gameController.removeListener(this);
    }

    @Override
    public void onGameUpdate() {
        if (isActive())
            getViewManager().repaint();
    }

    @Override
    public void onKey(int keyCode) {
        if (keyCode == ' ') {
            if (gameController.isPause())
                gameController.start();
            else
                gameController.stop();
        }
    }
}
