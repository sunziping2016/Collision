package view;

/**
 * Created by Sun on 4/6/2016.
 *
 * View Class.
 */
public class View {
    private ViewManager viewManager = null;

    public void onCreate(Content content) {}
    public void onStart() {}
    public void onPaint(GraphicsWrapper g2) {}
    public void onStop() {}
    public void onDestroy() {}

    public void onKey(int keyCode) {}

    public boolean isActive() {
        return getViewManager().isActiveView(this);
    }

    public ViewManager getViewManager() {
        return viewManager;
    }
    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }
}

