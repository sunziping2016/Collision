
/**
 * Created by Sun on 3/21/2016.
 *
 * Main thread to set MVC to work.
 */

import leap.LeapController;
import java.util.logging.Logger;
import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) {
        LeapController leapController = new LeapController();
        try {
            sleep(100000);
        }
        catch (InterruptedException error) {
            logger.warning(error.getMessage());
        }
    }

    private final static Logger logger = Logger.getLogger(Main.class.getName());
}
