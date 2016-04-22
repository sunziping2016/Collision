package controller;

/**
 * Created by Sun on 4/23/2016.
 *
 * Hands listener class.
 */
abstract public class HandsListener {
    private static final float DEFAULT_HEIGHT = 125f, DEFAULT_WIDTH = 200f, DEFAULT_ABOVE = 250f, DEFAULT_SPACE = 400f;

    int[] preferredHandsID = new int[0];

    abstract public int getnHands();
    abstract public void onHandUpdate(int index, float x, float y, long dt, boolean online);

    public LeapController.Area getArea(int index) {
        int nHands = getnHands();
        float dx = (index - nHands / 2.0f + 0.5f) * DEFAULT_SPACE;
        return new LeapController.Area(-DEFAULT_WIDTH / 2.0f + dx, DEFAULT_HEIGHT + DEFAULT_ABOVE, DEFAULT_WIDTH / 2.0f + dx, DEFAULT_ABOVE);
    }
}
