package controller;

import com.leapmotion.leap.*;
import view.LeaderboardView;
import view.ViewManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * Created by Sun on 3/21/2016.
 *
 * Thread to listen and parse Leap Frame
 */
public class LeapController extends Listener implements Runnable {
    static public class Area {
        public float leftupx, leftupy, rightbuttomx, rightbuttomy;
        public Area(float leftupx, float leftupy, float rightbuttomx, float rightbuttomy) {
            this.leftupx = leftupx;
            this.leftupy = leftupy;
            this.rightbuttomx = rightbuttomx;
            this.rightbuttomy = rightbuttomy;
        }
        public float[] getAreaCenter() {
            return new float[] {
                    (leftupx + rightbuttomx) / 2.0f,
                    (leftupy + rightbuttomy) / 2.0f,
            };
        }
    }
    private final ArrayList<PointerListener> pointerListeners = new ArrayList<>();
    private final ArrayList<HandsListener> handsListeners = new ArrayList<>();

    private Thread thread;

    private float normalize(float n, float a, float b) {
        assert a != b;
        if (n >= b) return 1f;
        if (n <= a) return 0f;
        return (n - a) / (b - a);
    }

    public boolean isConnected() {
        return leap.isConnected();
    }

    private long lastUpdate;
    public void processFrame(Frame frame) {
        long now = System.currentTimeMillis();
        long dt = (lastUpdate < 0) ? 0 : now - lastUpdate;

        if (!leap.isConnected())
            return;
        synchronized (handsListeners) {
            for (HandsListener listener: handsListeners) {
                // Get infomation from listener.
                int nHands = listener.getnHands(), nAllocated = 0;
                Area[] areas = new Area[nHands];
                float[][] areaCenters = new float[nHands][];
                for (int i = 0; i < nHands; ++i) {
                    areas[i] = listener.getArea(i);
                    areaCenters[i] = areas[i].getAreaCenter();
                }
                // Check for traced hands.
                int[] preferredHandIds = new int[nHands];
                Hand[] hands = new Hand[nHands];
                ArrayList<Hand> unallocatedHands = new ArrayList<>();
                Arrays.fill(preferredHandIds, -1);
                for (Hand hand: frame.hands())
                    if (hand.isValid())
                        unallocatedHands.add(hand);
                if (nHands == listener.preferredHandsID.length) {
                    for (int i = 0; i < nHands; ++i) {
                        if (listener.preferredHandsID[i] == -1) continue;
                        int j;
                        for (j = 0; j < unallocatedHands.size(); ++j)
                            if (listener.preferredHandsID[i] == unallocatedHands.get(j).id()) {
                                ++nAllocated;
                                preferredHandIds[i] = listener.preferredHandsID[i];
                                hands[i] = unallocatedHands.get(j);
                                break;
                            }
                        if (j != unallocatedHands.size())
                            unallocatedHands.remove(j); // Avoid corruption.
                    }
                }
                // Find the closest.
                while (nAllocated != nHands || !unallocatedHands.isEmpty()) {
                    boolean founded = false;
                    int index = 0;
                    Hand hand = null;
                    float dis = 0.0f;
                    for (int i = 0; i < nHands; ++i) {
                        if (preferredHandIds[i] != -1) continue;
                        for (Hand h: unallocatedHands) {
                            Vector handPos = h.palmPosition();
                            float dx = handPos.getX() - areaCenters[i][0], dy = handPos.getY() - areaCenters[i][1];
                            float dis2 = dx * dx + dy * dy;
                            if (!founded || dis2 < dis)  {
                                founded = true;
                                index = i;
                                hand = h;
                                dis = dis2;
                            }
                        }
                    }
                    if (founded) {
                        ++nAllocated;
                        preferredHandIds[index] = hand.id();
                        hands[index] = hand;
                        unallocatedHands.remove(hand);
                    }
                    else
                        break;
                }
                // Update the listeners.
                listener.preferredHandsID = preferredHandIds;
                for (int i = 0; i < nHands; ++i) {
                    if (preferredHandIds[i] != -1) {
                        Vector pos = hands[i].palmPosition();
                        float x = normalize(pos.getX(), areas[i].leftupx, areas[i].rightbuttomx);
                        float y = normalize(pos.getY(), areas[i].rightbuttomy, areas[i].leftupy);
                        listener.onHandUpdate(i, x, y, dt, true);
                    }
                    else
                        listener.onHandUpdate(i, 0, 0, dt, false);
                }
            }
        }

        synchronized (pointerListeners) {
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
                // Update the listeners.
                if (pointer != null) {
                    Vector pos = pointer.palmPosition();
                    Area area = listener.getArea();
                    float x = normalize(pos.getX(), area.leftupx, area.rightbuttomx);
                    float y = normalize(pos.getY(), area.rightbuttomy, area.leftupy);
                    listener.preferredPointableID = pointer.id();
                    new Thread(() -> listener.onPointerUpdate(x, y, dt, true)).start();
                } else {
                    listener.preferredPointableID = -1;
                    new Thread(() -> listener.onPointerUpdate(0.0f, 0.0f, dt, false)).start();
                }
            }
        }
        lastUpdate = now;
    }
    public void addPointerListener(PointerListener listener) {
        synchronized (pointerListeners) {
            pointerListeners.add(listener);
        }
    }
    public void removePointerListener(PointerListener listener) {
        synchronized (pointerListeners) {
            pointerListeners.remove(listener);
        }
    }
    public void addHandsListener(HandsListener listener) {
        synchronized (handsListeners) {
            handsListeners.add(listener);
        }
    }
    public void removeHandsListener(HandsListener listener) {
        synchronized (handsListeners) {
            handsListeners.remove(listener);
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
    private LeapController() {
        leap = new Controller(this);
        thread = new Thread(this);
    }

    public void start() {
        if (!thread.isAlive()) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (thread.isAlive()) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                // Do nothing.
            }
        }
    }
    private static LeapController leapController = new LeapController();

    public static LeapController getLeapController() {
        return leapController;
    }
}
