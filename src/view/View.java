package view;

import java.awt.*;

/**
 * Created by Sun on 4/6/2016.
 *
 * View Class.
 */
abstract public class View {
    private ViewManager viewManager = null;

    abstract public void draw(GraphicsWrapper g2, boolean active);
    public void onActive() {}
    public void onKey(int keyCode) {}

    public ViewManager getViewManager() {
        return viewManager;
    }

    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }
}
