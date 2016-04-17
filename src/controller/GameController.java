package controller;

import model.Ball;
import model.GameModel;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Filter;
import view.GameView;

/**
 * Created by Sun on 4/6/2016.
 *
 * Game controller class.
 */
public class GameController implements Runnable {
    private static final int PAUSE_DELAY = 100;
    private GameModel model;
    private GameView view; // Refresh.

    public GameController(GameModel m) {
        model = m;
        for (int i = 0; i < 4; ++i)
            m.addBallRandom();
        Thread t = new Thread(this);
        t.start();
    }

    public void run() {
        long time = System.currentTimeMillis(), now;
        while (true) {
            try { Thread.sleep(25); } catch (Exception e) {}
            // Update new position
            now = System.currentTimeMillis();
            //model.world.step((now - time) /1000f, 6, 2);
            model.world.step(20f /1000f, 6, 2);
            if (view != null)
                view.getViewManager().repaint();
            // Check for death
            for (int i = 0; i < model.userBalls.size(); ++i) {
                Ball ball = model.userBalls.get(i);
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
            for (int i = 0; i < model.userBalls.size(); ++i) {
                Ball ball = model.userBalls.get(i);
                if (ball.coolDown > 0)
                    --ball.coolDown;
            }
            // Clear the fallen death
            time = now;
        }
    }

    public GameView getView() {
        return view;
    }

    public void setView(GameView view) {
        this.view = view;
    }

    public synchronized void moveUserBall(int index, double x, double y, double dt) {
        final float RATE = 5000f;
        double dx = (16 * (float) x - 8) - model.userBalls.get(index).getBody().getPosition().x;
        double dy = (10 * (float) y) - model.userBalls.get(index).getBody().getPosition().y;
        model.userBalls.get(index).getBody().applyForceToCenter(new Vec2((float) (dx / dt * RATE), (float) (dy / dt * RATE)));
    }

    public int getnUsers() {
        return model.getnUsers();
    }

    public void setUserOnline(int index, boolean online) {
        model.userBalls.get(index).isOnline = online;
    }

    public GameModel getModel() {
        return model;
    }
}
