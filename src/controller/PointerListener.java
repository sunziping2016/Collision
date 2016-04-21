package controller;

/**
 * Created by Sun on 4/20/2016.
 *
 * Pointer listener class.
 */
abstract public class PointerListener {
    int preferredPointableID = -1;
    abstract public void onPointerUpdate(float x, float y, boolean online);
}
