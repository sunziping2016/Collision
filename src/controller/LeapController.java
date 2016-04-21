package controller;

import com.leapmotion.leap.*;
import view.LeaderboardView;
import view.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * Created by Sun on 3/21/2016.
 *
 * Thread to listen and parse Leap Frame
 */
public class LeapController extends Listener implements Runnable {
    private ViewManager viewManager;
    private GameController gameController;

    private final ArrayList<PointerListener> pointerListeners = new ArrayList<>();

    private int[] preferredHandIDs = null;

    private static final double SPACE_WIDTH = 200f;
    private static final double SPACE_HEIGHT = 200f;

    public static final double THUMB_YAW_THRESHOLD = 65f / 180f * Math.PI;
    public static final double THUMB_LENGTH_THRESHOLD = 30;


    private long lastUpdate;

    private double normalize(double n, double a, double b) {
        assert a != b;
        if (n >= b) return 1f;
        if (n <= a) return 0f;
        return (n - a) / (b - a);
    }

    public boolean isConnected() {
        return leap.isConnected();
    }

    public void processFrame(Frame frame) {
        long now = System.currentTimeMillis();
        long dt = (lastUpdate < 0) ? 0 : now-lastUpdate;

        if (!leap.isConnected())
            return;
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
                if (hands[i] == null || gameController.getGameModel().userBalls.get(i).isDead) {
                    continue;
                }
                Vector handPos = hands[i].palmPosition();
                double dx = (i - hands.length / 2.0 + 0.5) * 2 * SPACE_HEIGHT;
                double x = normalize(handPos.getX(), -SPACE_WIDTH/2.0f + dx, SPACE_WIDTH/2.0f + dx);
                double y = normalize(handPos.getY() - 150f, 0f, SPACE_HEIGHT);
                gameController.moveUserBall(i, x, y, dt);
            }
        }

        synchronized (pointerListeners) {
            if (!pointerListeners.isEmpty()) {
                boolean hasDefault = false;
                Hand defaultHand = null;
                // Find the best pointer.
                for (PointerListener listener : pointerListeners) {
                    Hand pointer = frame.hand(listener.preferredPointableID);
                    if (!pointer.isValid()) {
                        if (!hasDefault) {
                            for (Hand p : frame.hands()) {
                                if (defaultHand != null && Math.abs(defaultHand.palmPosition().getX()) < Math.abs(p.palmPosition().getX()))
                                    continue;
                                if (defaultHand != null && defaultHand.timeVisible() > p.timeVisible()) continue;
                                defaultHand = p;
                            }
                            hasDefault = true;
                        }
                        pointer = defaultHand;
                    }
                    // Update pointer.
                    if (pointer != null) {
                        Vector pos = pointer.palmPosition();
                        double x = normalize(pos.getX(), -SPACE_WIDTH / 2.0f, SPACE_WIDTH / 2.0f) * 16;
                        double y = 10.0 - 10.0 * normalize(pos.getY() - 150f, 50f, SPACE_HEIGHT * 0.75f);
                        new Thread(() -> listener.onPointerUpdate((float) x, (float) y, true)).start();
                        listener.preferredPointableID = pointer.id();
                    } else {
                        new Thread(() -> listener.onPointerUpdate(0.0f, 0.0f, false)).start();
                        listener.preferredPointableID = -1;
                    }
                }
            }
        }

        lastUpdate = now;
    }
    public void addListener(PointerListener listener) {
        synchronized (pointerListeners) {
            pointerListeners.add(listener);
        }
    }
    public void removeListener(PointerListener listener) {
        synchronized (pointerListeners) {
            pointerListeners.remove(listener);
        }
    }

    private Controller leap;
    private final static Logger logger = Logger.getLogger(LeapController.class.getName());

    public void run() {
        while (true) {
            processFrame(leap.frame());
            try {
                Thread.sleep(50);
            }
            catch (InterruptedException error) {
                break;
            }
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
