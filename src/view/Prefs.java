package view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Sun on 4/21/2016.
 *
 * Preference class.
 */
public class Prefs {
    private Properties store;
    private File file;

    public String getString(String key, String defaultValue) {
        return store.getProperty(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(store.getProperty(key, Integer.toString(defaultValue)));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public void putString(String key, String val) {
        store.setProperty(key, val);
    }

    public void putInt(String key, int val) {
        store.setProperty(key, Integer.toString(val));
    }

    public void reset() {
        store = new Properties();
    }

    public void writeOut() {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            store.store(fos, "Collision settings file... do not tamper with this!");
            fos.close();
        } catch (Exception e) {}
    }
    private Prefs(File file) {
        this.file = file;
        store = new Properties();
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                store.load(fis);
                fis.close();
            } catch (IOException error) {}
        }
    }

    private static Prefs prefs = new Prefs(new File("prefs.txt"));
    public static Prefs getPrefs() {
        return prefs;
    }
}