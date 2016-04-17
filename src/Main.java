
/**
 * Created by Sun on 3/21/2016.
 *
 * Main thread to set MVC to work.
 */

import controller.GameController;
import controller.LeapController;
import model.GameModel;
import view.GameView;
import view.ViewManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        GameModel gameModel = new GameModel();
        GameController gameController = new GameController(gameModel);

        ViewManager viewManager = new ViewManager();

        GameView gameView = new GameView(gameModel);
        viewManager.registerView(gameView, "game");
        viewManager.pushView("game");
        gameController.setView(gameView);

        JFrame frame = new JFrame("Collision");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container lay = frame.getContentPane();
        lay.setLayout(new BoxLayout(lay, BoxLayout.Y_AXIS));
        lay.setBackground(Color.GRAY);
        lay.add(Box.createVerticalGlue());
        lay.add(viewManager);
        lay.add(Box.createVerticalGlue());
        lay.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), "null"));
        frame.addKeyListener(viewManager);
        frame.addWindowListener(viewManager);

        LeapController leap = new LeapController(gameController, viewManager);

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(1280, 840);
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
        frame.setVisible(true);
    }

    private final static Logger logger = Logger.getLogger(Main.class.getName());
}
