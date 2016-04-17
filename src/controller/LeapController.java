package controller;

import com.leapmotion.leap.*;
import view.ViewManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * Created by Sun on 3/21/2016.
 *
 * Thread to listen and parse Leap Frame
 */
public class LeapController extends Listener implements Runnable {
    private boolean windowFocused;
    private ViewManager viewManager;
    private GameController gameController;

    private int[] preferredHandIDs = null;

    static final double SPACE_WIDTH = 200f;
    static final double SPACE_HEIGHT = 200f;

    private long lastUpdate;

    private double normalize(double n, double a, double b) {
        assert a != b;
        if (n >= b) return 1f;
        if (n <= a) return 0f;
        return (n - a) / (b - a);
    }

    public void processFrame(Frame frame) {
        long now = System.currentTimeMillis();
        long dt = (lastUpdate < 0) ? 0 : now-lastUpdate;

        viewManager.setLeapWarningVisible(!leap.isConnected());
        //if (!windowFocused || !leap.isConnected())
        //    return;
        if (viewManager.isActiveView("game")) {
            // Allocate hands to the users.
            int[] newPreferredHandIds = new int[gameController.getnUsers()];
            Hand[] hands = new Hand[gameController.getnUsers()];
            ArrayList<Hand> unallocatedHands = new ArrayList<Hand>();
            for (Hand i: frame.hands())
                unallocatedHands.add(i);
            for (int i = 0; i < newPreferredHandIds.length; ++i) {
                if (preferredHandIDs != null && i < preferredHandIDs.length && preferredHandIDs[i] != -1) {
                    int  j;

                    for (j = 0; j < unallocatedHands.size(); ++j)
                        if (preferredHandIDs[i] == unallocatedHands.get(j).id()) {
                            newPreferredHandIds[i] = preferredHandIDs[i];
                            hands[i] = unallocatedHands.get(j);
                            break;
                        }
                    if (j != unallocatedHands.size()) {
                        unallocatedHands.remove(j);
                        continue;
                    }
                }
                newPreferredHandIds[i] = -1;
                hands[i] = null;
            }
            preferredHandIDs = newPreferredHandIds;
            unallocatedHands.sort((a, b) -> {return (int)(a.palmPosition().getX() - b.palmPosition().getX());});
            for (int i = 0; i < preferredHandIDs.length; ++i) {
                if (preferredHandIDs[i] == -1 && !unallocatedHands.isEmpty()) {
                    preferredHandIDs[i] = unallocatedHands.get(0).id();
                    hands[i] = unallocatedHands.get(0);
                    unallocatedHands.remove(0);
                }
            }
            // Update game.
            for (int i = 0; i < hands.length; ++i) {
                gameController.setUserOnline(i, hands[i] != null);
                if (hands[i] == null || gameController.getModel().userBalls.get(i).isDead) {
                    continue;
                }
                Vector handPos = hands[i].palmPosition();
                double dx = (i - hands.length / 2.0 + 0.5) * 2 * SPACE_HEIGHT;
                double x = normalize(handPos.getX(), -SPACE_WIDTH/2.0f + dx, SPACE_WIDTH/2.0f + dx);
                double y = normalize(handPos.getY() - 150f, 0f, SPACE_HEIGHT);
                gameController.moveUserBall(i, x, y, dt);
            }
        }

        lastUpdate = now;
    }

    public void notifyWindowState(boolean focused) {
        this.windowFocused = focused;
    }

    private Controller leap;
    private final static Logger logger = Logger.getLogger(LeapController.class.getName());

    public void run() {
        while (true) {
            processFrame(leap.frame());
            try { Thread.sleep(50); } catch (Exception e) {}
        }
    }

    public LeapController(GameController gameController, ViewManager viewManager) {
        this.gameController = gameController;
        this.viewManager = viewManager;

        leap = new Controller(this);

        Thread t = new Thread(this);
        t.start();

    }
}
