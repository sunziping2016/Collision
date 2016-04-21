
/**
 * Created by Sun on 3/21/2016.
 *
 * MainFrame thread to set MVC to work.
 */

import controller.GameController;
import controller.LeapController;
import model.GameModel;
import view.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

public class MainFrame extends JFrame {
    public MainFrame() {
        super("Collision");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GameModel gameModel = new GameModel();
        GameController gameController = new GameController(gameModel);
        ViewManager viewManager = new ViewManager();
        LeapController leap = new LeapController(gameController, viewManager);
        viewManager.setLeapController(leap);

        viewManager.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F11"), "F11");
        viewManager.getActionMap().put("F11", new AbstractAction() {
            private GraphicsDevice fullscreenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.dispose();
                if (MainFrame.this.isUndecorated()) {
                    fullscreenDevice.setFullScreenWindow(null);
                    MainFrame.this.setUndecorated(false);
                } else {
                    MainFrame.this.setUndecorated(true);
                    fullscreenDevice.setFullScreenWindow(MainFrame.this);
                }
                MainFrame.this.setVisible(true);
                MainFrame.this.repaint();
            }
        });

        // add views.
        viewManager.registerView(new GameView(gameController), "game");
        viewManager.registerView(new SplashView(gameController), "splash");
        viewManager.registerView(new LeaderboardView(), "leaderboard");
        viewManager.pushView("splash");

        Container lay = getContentPane();
        lay.setLayout(new BoxLayout(lay, BoxLayout.Y_AXIS));
        lay.setBackground(Colors.LETTERBOX);
        lay.add(Box.createVerticalGlue());
        lay.add(viewManager);
        lay.add(Box.createVerticalGlue());
        lay.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), "null"));
        addKeyListener(viewManager);
        addWindowListener(viewManager);

        setSize(1280, 840);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int) ((dimension.getWidth() - getWidth()) / 2), (int) ((dimension.getHeight() - getHeight()) / 2));
    }
    public static void main(String[] args) {
        MainFrame app = new MainFrame();
        app.setVisible(true);
        Prefs.getPrefs().writeOut();
    }

    private final static Logger logger = Logger.getLogger(MainFrame.class.getName());
}
