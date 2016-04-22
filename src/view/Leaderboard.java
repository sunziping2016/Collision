package view;

import controller.LeapController;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Sun on 4/23/2016.
 *
 * Leader board class.
 */
public class Leaderboard {
    public static class Entry implements Comparable<Entry> {
        String name;
        long score;
        public Entry(String name, long score) {
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
    public static final int N_ENTRIES = 10;

    public ArrayList<Entry> topList = new ArrayList<>();
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
    public void clear() {
        Prefs prefs = Prefs.getPrefs();
        prefs.reset();
        topList = new ArrayList<Entry>();
    }
    public boolean isChampionShip(long score) {
        if (topList.size() < N_ENTRIES)
            return true;
        return score > topList.get(N_ENTRIES - 1).score;
    }
    private Leaderboard() {
        readPrefs();
    }
    private static Leaderboard leaderboard = new Leaderboard();
    public static Leaderboard getLeaderboard() {
        return leaderboard;
    }
}
