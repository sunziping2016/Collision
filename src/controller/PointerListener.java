package controller;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;

import java.awt.*;

/**
 * Created by Sun on 4/20/2016.
 *
 * Pointer listener class.
 */
abstract public class PointerListener {
    private static final float DEFAULT_HEIGHT = 125f, DEFAULT_WIDTH = 200f, DEFAULT_ABOVE = 250f;

    int preferredPointableID = -1;

    abstract public void onPointerUpdate(float x, float y, long t, boolean online);

    public LeapController.Area getArea() {
        return new LeapController.Area(-DEFAULT_WIDTH / 2.0f, DEFAULT_HEIGHT + DEFAULT_ABOVE, DEFAULT_WIDTH / 2.0f, DEFAULT_ABOVE);
    }
}
