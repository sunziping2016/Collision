package view;

import controller.PointerListener;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Sun on 4/21/2016.
 *
 * Leaderboard view.
 */
public class LeaderboardView extends View implements RadialMenuListener {
    private class Entry implements Comparable<Entry> {
        String name;
        long score;
        public Entry(String name, int score) {
            this.name = name;
            this.score = score;
        }
        public int compareTo(Entry other) {
            return (int) (other.score - this.score); //intentionally backwards comparator
        }
        public boolean equals(Object other) {
            return this == other;
        }
    }

    private static final int N_ENTRIES = 10;
    private ArrayList<Entry> topList = new ArrayList<>();

    private RadialMenu menu;
    private static final int LEADERBOARD_MENU_CLEAR = 0;
    private static final int LEADERBOARD_MENU_EXIT = 1;

    public LeaderboardView() {
        menu = new RadialMenu(8, 11.5f, this);
        menu.addItem(new RadialMenuItem(LEADERBOARD_MENU_EXIT, "Back", "menuReturn", 90, 20, RadialMenuItem.ORIENT_TOP));
        menu.addItem(new RadialMenuItem(LEADERBOARD_MENU_CLEAR, "Reset Scores", "clearLeaderboard", 70, 20, RadialMenuItem.ORIENT_TOP));
    }

    public void readPrefs() {
        Prefs prefs = Prefs.getPrefs();
        for (int i = 1; i <= N_ENTRIES; i++) {
            String name = prefs.getString("name_"+i, null);
            int score = prefs.getInt("score_"+i, Integer.MIN_VALUE);
            if (name == null || score == Integer.MIN_VALUE)
                break;
            Entry e = new Entry(name, score);
            topList.add(e);
        }
        Collections.sort(topList);
    }

    public void writePrefs() {
        Prefs prefs = Prefs.getPrefs();
        for (int i = 0; i < topList.size(); i++) {
            Entry e = topList.get(i);
            prefs.putString("name_"+(i+1), e.name);
            prefs.putInt("score_"+(i+1), (int) e.score);
        }
        prefs.writeOut();
    }

    @Override
    public void onPaint(GraphicsWrapper g2) {
        if (!isActive()) return;
        g2.prepare();
        g2.fillRect(0.0f, 0.0f, 16.0f, 10.0f, Colors.BACKGROUND);
        if (topList.size() > 0) {
            for (int i = 0; i < topList.size(); i++) {
                Entry e = topList.get(i);
                String msg = String.format("#%d: %s:  %d points", i+1, e.name, e.score);
                g2.drawStringCentered(msg, 0.5f, Colors.LEADERBOARD, 8, 1.8f + 0.6f * i);
            }
        }
        else
            g2.drawStringCentered("No High Scores", 1, Colors.LEADERBOARD, 8, 5);
        menu.draw(g2);
    }

    @Override
    public void onStart() {
        getViewManager().getLeapController().addListener(menu);
    }

    @Override
    public void onStop() {
        getViewManager().getLeapController().removeListener(menu);
    }

    public void clearLeaderboard() {
        getViewManager().getLeapController().removeListener(menu);
        int confirmation = JOptionPane.showConfirmDialog(getViewManager(), "Are you sure you want to clear all high scores?", "Clear High Scores", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, ImageManager.getIcon("clearLeaderboard"));
        if (confirmation == JOptionPane.OK_OPTION) {
            Prefs prefs = Prefs.getPrefs();
            prefs.reset();
            prefs.writeOut();
            topList = new ArrayList<Entry>();
        }
        getViewManager().getLeapController().addListener(menu);
    }

    @Override
    public void onMenuSelection(int id) {
        if (id != -1) {
            SoundManager.play("menuChoice");
            switch (id) {
                case LEADERBOARD_MENU_CLEAR:
                    clearLeaderboard();
                    break;
                case LEADERBOARD_MENU_EXIT:
                    getViewManager().popView();
                    break;
                default:
                    break;
            }
        }
        getViewManager().repaint();
    }
}
