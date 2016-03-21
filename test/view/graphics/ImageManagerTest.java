package view.graphics;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Sun on 3/21/2016.
 *
 * Unit test for ImageManager.java.
 */
public class ImageManagerTest {
    @Test
    public void imageLoadTest() throws Exception {
        ImageManager.getImageInstance("leapWarn", 1.0f, 1.0f, 0.0f);
    }
}