
/**
 * Created by Sun on 3/21/2016.
 *
 * MainFrame thread to set MVC to work.
 */

import controller.GameController;
import controller.LeapController;
import model.GameModel;
import oracle.jrockit.jfr.JFR;
import view.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

public class MainFrame extends JFrame {
    public MainFrame() {
        super("Collision");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        GameModel gameModel = new GameModel();
        GameController gameController = new GameController(gameModel);
        ViewManager viewManager = new ViewManager(this);

        // add views.
        viewManager.registerView(new GameView(gameController), "game");
        viewManager.registerView(new SplashView(gameController), "splash");
        viewManager.registerView(new LeaderboardView(), "leaderboard");
        viewManager.registerView(new GameOverView(), "gameover");
        viewManager.registerView(new GameStartView(), "gamestart");
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
        LeapController.getLeapController().start();
        MainFrame app = new MainFrame();
        app.setVisible(true);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LeapController.getLeapController().stop();
                Leaderboard.getLeaderboard().writePrefs();
                Prefs.getPrefs().writeOut();
            }
        });
    }
}
