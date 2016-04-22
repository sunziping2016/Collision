package view;

import controller.GameController;
import controller.GameListener;
import controller.LeapController;
import model.Ball;
import model.GameModel;
import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;


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
        if (!isActive() && !getViewManager().isActiveView("gamestart")) return;
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
                if (ball.coolDown > 0)
                    color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 160);
                g2.fillBall(pos.x + 8.0f, 10.0f - pos.y, ball.getFixture().getShape().getRadius(), color);
            }
            g2.maskClear();
            g2.fillBoundary(gameModel.corners[0] + 8.0f, 10.0f - gameModel.corners[1], gameModel.corners[2] + 8.0f, 10.0f - gameModel.corners[3], 0.3f, Colors.BOUNDARY);
        }
        g2.drawString("Time: " + gameController.getTimer().toString(), 0.3f, Color.WHITE, 13f, 1f, false);
    }

    @Override
    public void onStart(Content content) {
        String status = content.getString("status", null);
        if (status == null) {
            gameController.setBoundary(-7.5f, 9.5f, 7.5f, 0.4f);
            gameController.setnBalls(4);
            gameController.setnUsers(content.getInt("nusers", 1));
            gameController.setRandomSpeed(6.0f);
            gameController.clearTimer();
            content.putObject("userballs", gameController.getGameModel().userBalls);
            new Thread(() -> getViewManager().pushView("gamestart", content)).start();
        }
        else if (status == "ready") {
            gameController.start();
            gameController.addListener(this);
            LeapController.getLeapController().addHandsListener(gameController.getGameListener());
        }
    }

    @Override
    public void onStop() {
        gameController.stop();
        gameController.removeListener(this);
        LeapController.getLeapController().removeHandsListener(gameController.getGameListener());
    }

    @Override
    public void onGameUpdate() {
        if (gameController.isGameOver()) {
            gameController.stop();
            Content content = new Content();
            if (gameController.getnUsers() == 1) {
                content.putObject("time", gameController.getTimer());
                long score = gameController.getTimer().getElapse();
                if (Leaderboard.getLeaderboard().isChampionShip(score)) {
                    Leaderboard.Entry entry = new Leaderboard.Entry(null, score);
                    int binSearch = Collections.binarySearch(Leaderboard.getLeaderboard().topList, entry);
                    int insertIndex = binSearch < 0 ? -binSearch - 1 : binSearch + 1;
                    //String msg = String.format("New high score of %s (placed #%d)!\nWhat is your name?", gameController.getTimer().toString(), insertIndex + 1);
                    //String name = (String) JOptionPane.showInputDialog(getViewManager(), msg, "New High Score", JOptionPane.QUESTION_MESSAGE, ImageManager.getIcon("leaderboard"), null, "");
                    String name = "Anonymous";
                    if (name != null) {
                        entry.name = name;
                        Leaderboard.getLeaderboard().topList.add(insertIndex, entry);
                        while (Leaderboard.getLeaderboard().topList.size() > Leaderboard.N_ENTRIES)
                            Leaderboard.getLeaderboard().topList.remove(Leaderboard.getLeaderboard().topList.size()-1);
                        content.putInt("high", 1);
                    }
                }
            }
            getViewManager().pushView("gameover", content);
        }
        else if (isActive())
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
