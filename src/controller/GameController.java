package controller;

import model.Ball;
import model.GameModel;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Filter;

import java.awt.*;
import java.sql.Time;
import java.util.ArrayList;

/**
 * Created by Sun on 4/6/2016.
 *
 * Game controller class.
 */
public class GameController implements Runnable {
    private static final int PAUSE_DELAY = 100;
    private final GameModel gameModel;
    private final ArrayList<GameListener> listeners = new ArrayList<>();
    private Thread thread;
    private Timer timer = new Timer();

    public GameController(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    public void start() {
        if (isPause()) {
            thread = new Thread(this);
            thread.start();
            timer.resume();
        }
    }

    public void stop() {
        if (!isPause()) {
            thread.interrupt();
            try {
                thread.join();
                timer.pause();
            } catch (InterruptedException e) {
                // Do nothing.
            }
        }
    }
    public void clearTimer() {
        timer = new Timer();
    }

    public boolean isPause() {
        return thread == null || !thread.isAlive();
    }

    public void run() {
        long time = System.currentTimeMillis(), now;
        while (true) {
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                break;
            }
            // Update new position
            now = System.currentTimeMillis();
            synchronized (gameModel) {
                //gameModel.world.step((now - time) /1000f, 6, 2);
                gameModel.world.step(30f /1000f, 6, 2);
                // Check for death
                for (int i = 0; i < gameModel.userBalls.size(); ++i) {
                    Ball ball = gameModel.userBalls.get(i);
                    if (ball.getNumContact() != 0 && ball.coolDown == 0) {
                        if (!ball.isDead) {
                            ball.isDead = true;
                            Filter filter = ball.getFixture().getFilterData();
                            filter.maskBits = 0;
                            ball.getFixture().setFilterData(filter);
                            ball.getBody().setGravityScale(1.0f);
                            ball.getBody().setLinearDamping(0.0f);
                        }
                        System.out.println(i + " dead");
                    }
                }
                // Update cool time
                for (int i = 0; i < gameModel.userBalls.size(); ++i) {
                    Ball ball = gameModel.userBalls.get(i);
                    if (ball.coolDown > 0)
                        --ball.coolDown;
                }
            }
            synchronized (listeners) {
                for (GameListener i: listeners)
                    i.onGameUpdate();
            }
            // Clear the fallen death
            time = now;
        }
    }

    public void setnUsers(int n) {
        gameModel.setnUsers(n);
    }
    public void setnBalls(int n) {
        gameModel.setnBalls(n);
    }
    public void setRandomSpeed(float speed) {
        gameModel.setRandomSpeed(speed);
    }

    public void addListener(GameListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }
    public void removeListener(GameListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public synchronized void moveUserBall(int index, double x, double y, double dt) {
        final float RATE = 5000f;
        double dx = (16 * (float) x - 8) - gameModel.userBalls.get(index).getBody().getPosition().x;
        double dy = (10 * (float) y) - gameModel.userBalls.get(index).getBody().getPosition().y;
        gameModel.userBalls.get(index).getBody().applyForceToCenter(new Vec2((float) (dx / dt * RATE), (float) (dy / dt * RATE)));
    }

    public int getnUsers() {
        return gameModel.getnUsers();
    }

    public void setUserOnline(int index, boolean online) {
        gameModel.setUserOnline(index, online);
    }

    public GameModel getGameModel() {
        return gameModel;
    }

    public void setBoundary(float leftupx, float leftupy, float rightbuttomx, float rightbuttomy) {
        gameModel.setBoundary(leftupx, leftupy, rightbuttomx, rightbuttomy);
    }

    public void setBoundary() {
        gameModel.setBoundary();
    }

    public void addBall(Vec2 pos, Color color) {
        gameModel.addBall(pos, color);
    }

    public void addBallRandom() {
        gameModel.addBallRandom();
    }

    public Timer getTimer() {
        return timer;
    }
}
