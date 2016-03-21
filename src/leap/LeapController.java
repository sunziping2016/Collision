package leap;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Listener;

import java.util.logging.Logger;

/**
 * Created by Sun on 3/21/2016.
 *
 * Thread to listen and parse Leap Frame
 */
public class LeapController extends Listener {
    @Override
    public void onConnect(Controller controller) {
        logger.info("Connected.");
    }

    @Override
    public void onFrame(Controller controller) {
        //logger.info("Frame available.");
    }

    @Override
    public void onDisconnect(Controller controller) {
        logger.info("Disconnected.");
    }

    @Override
    public void onExit(Controller controller) {
        logger.info("Exit.");
    }

    public void exit() {
        leap.removeListener(this);
    }

    private Controller leap = new Controller(this);
    private final static Logger logger = Logger.getLogger(LeapController.class.getName());
}
