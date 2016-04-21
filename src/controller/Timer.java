package controller;

/**
 * Created by Sun on 4/21/2016.
 *
 * Timer class.
 */
public class Timer {
    public enum TimerState {PAUSED, RESUMED};

    private long start, elapse;
    private TimerState state;

    public Timer() {
        state = TimerState.PAUSED;
    }
    public void resume() {
        if (state == TimerState.PAUSED) {
            start = System.currentTimeMillis();
            state = TimerState.RESUMED;
        }
    }
    public void pause() {
        if (state == TimerState.RESUMED) {
            elapse += System.currentTimeMillis() - start;
            state = TimerState.PAUSED;
        }
    }
    public TimerState getState() {
        return state;
    }
    public long getElapse() {
        if (state == TimerState.PAUSED)
            return elapse;
        else
            return elapse + (System.currentTimeMillis() - start);
    }

    @Override
    public String toString() {
        long time = getElapse();
        return String.format("Time: %02d:%02d.%02d", time / 60000, (time / 1000) % 60, (time / 10) % 100);
    }
}
