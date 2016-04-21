package view;

import javax.sound.sampled.*;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Sun on 4/19/2016.
 *
 * Sound manager class.
 */
public class SoundManager {
    private static boolean muted = false;

    private static HashMap<String, Clip> clips = new HashMap<String, Clip>();

    private static void load(String clipName) throws Exception {
        URL url = SoundManager.class.getResource("../sounds/" + clipName+".wav");
        AudioInputStream stream = AudioSystem.getAudioInputStream(url);
        AudioFormat format = stream.getFormat();

        // specify what kind of line we want to create
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        // create the line
        Clip clip = (Clip)AudioSystem.getLine(info);
        // load the samples from the stream
        clip.open(stream);
        clips.put(clipName, clip);
    }

    public static boolean ensureLoaded(String clipName) {
        if(!clips.containsKey(clipName)) {
            try {
                load(clipName);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    public static void play(String clipName) {
        if(!muted && ensureLoaded(clipName)){
            Clip clip = clips.get(clipName);
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public static void setMuted(boolean m) {
        muted = m;
    }
    public static void toggleMuted(){
        muted = !muted;
    }
    public static boolean isMuted(){
        return muted;
    }
}
