package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

import controller.LeapController;
import jdk.nashorn.internal.scripts.JD;

/**
 * Created by Sun on 3/21/2016.
 *
 * Manage and switch between different views.
 */
public class ViewManager extends JComponent implements KeyListener, WindowListener {
    private JFrame frame;

    private LeapWarningView leapWarning = new LeapWarningView();

    private ConcurrentLinkedDeque<View> views = new ConcurrentLinkedDeque<View>();
    private HashMap<String, View> registry = new HashMap<String, View>();

    public ViewManager(JFrame frame) {
        super();
        this.frame = frame;
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F11"), "F11");
        getActionMap().put("F11", new AbstractAction() {
            private GraphicsDevice fullscreenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            @Override
            public void actionPerformed(ActionEvent e) {
                ViewManager.this.frame.dispose();
                ViewManager.this.frame.setVisible(false);
                if (ViewManager.this.frame.isUndecorated()) {
                    fullscreenDevice.setFullScreenWindow(null);
                    ViewManager.this.frame.setUndecorated(false);
                } else {
                    ViewManager.this.frame.setUndecorated(true);
                    fullscreenDevice.setFullScreenWindow(ViewManager.this.frame);
                }
                ViewManager.this.frame.setVisible(true);
                ViewManager.this.frame.repaint();
            }
        });
    }
    public void registerView(View view, String viewName) {
        registry.put(viewName, view);
        view.setViewManager(this);
    }
    public View getView(String viewName) {
        return registry.get(viewName);
    }
    public boolean isActiveView(String viewName) {
        View view = getView(viewName);
        View activeView = views.peek();
        return view != null && activeView != null && view == activeView;
    }
    public boolean isActiveView(View view) {
        View activeView = views.peek();
        return view != null && activeView != null && view == activeView;
    }
    public synchronized void pushView(String viewName, Content content) {
        content.putString("method", "push");
        View view = getView(viewName);
        if (view == null) return;
        View activeView = views.peek();
        if (activeView != null)
            activeView.onStop();
        view.onStart(content);
        views.push(view);
        repaint();
    }
    public void pushView(String viewName) {
        pushView(viewName, new Content());
    }
    public synchronized View popView(Content content) {
        content.putString("method", "pop");
        View popped = views.pop();
        if (popped != null)
            popped.onStop();
        View activeView = views.peek();
        if (activeView != null)
            activeView.onStart(content);
        repaint();
        return popped;
    }
    public void popView() {
        popView(new Content());
    }
    public synchronized void popTo(String name, Content content) {
        content.putString("method", "pop");
        View activeView = null;
        while (!views.isEmpty()) {
            activeView = views.peek();
            if (getView(name) == activeView)
                break;
            views.pop();
            if (activeView != null)
                activeView.onStop();
        }
        if (activeView != null)
            activeView.onStart(content);
        repaint();
    }
    public synchronized void popTo(String name) {
        popTo(name, new Content());
    }

    @Override
    public void paintComponent(Graphics g) {
        GraphicsWrapper g2 = new GraphicsWrapper(g, this);
        Iterator<View> it = views.descendingIterator();
        while (it.hasNext()) {
            View v = it.next();
            boolean active = !it.hasNext();
            v.onPaint(g2);
        }
        if (!LeapController.getLeapController().isConnected())
            leapWarning.onPaint(g2);
    }
    public void keyReleased(KeyEvent e) {
        // escape key exits app from anywhere
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            this.close();
        View activeView = views.peek();
        if (activeView != null)
            activeView.onKey(e.getKeyCode());
    }
    public void keyPressed(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    public Dimension getPreferredSize() {
        Container parent = this.getParent();
        if (parent == null) return null;
        float parentRatio = (float)parent.getWidth() / (float)parent.getHeight();
        if (parentRatio > 1.6f) {
            float ourWidth = 1.6f * parent.getHeight();
            return new Dimension((int)Math.round(ourWidth), parent.getHeight());
        }
        else {
            float ourHeight = (float)parent.getWidth() / 1.6f;
            return new Dimension(parent.getWidth(), (int)Math.round(ourHeight));
        }
    }
    public Dimension getMaximumSize() { return getPreferredSize(); }
    public Dimension getMinimumSize() { return getPreferredSize(); }

    public void close() {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    public void windowActivated(WindowEvent event) {}

    public void windowDeactivated(WindowEvent event) {}

    public void windowClosed(WindowEvent event) {}
    public void windowClosing(WindowEvent event) {}
    public void windowDeiconified(WindowEvent event) {}
    public void windowIconified(WindowEvent event) {}
    public void windowOpened(WindowEvent event) {}
}

