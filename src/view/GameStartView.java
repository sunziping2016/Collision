package view;

import controller.HandsListener;
import controller.LeapController;
import model.Ball;
import org.jbox2d.common.Vec2;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Sun on 4/23/2016.
 *
 * Game start view
 */
public class GameStartView extends View {
    ArrayList<Ball> userBalls;
    HandsListener listener;
    boolean finished;
    float[][] pos;
    boolean[] online;
    boolean[] activited;
    String message;

    @Override
    public void onStart(Content content) {
        finished = false;
        message = "Please move to the balls.";
        userBalls = (ArrayList<Ball>) content.getObject("userballs", null);
        pos = new float[userBalls.size()][];
        for (int i = 0; i < pos.length; ++i)
            pos[i] = new float[2];
        online = new boolean[userBalls.size()];
        activited = new boolean[userBalls.size()];

        listener = new HandsListener() {
            @Override
            public int getnHands() {
                return userBalls.size();
            }

            @Override
            public void onHandUpdate(int index, float x, float y, long dt, boolean online) {
                GameStartView.this.online[index] = online;
                if (online) {
                    pos[index][0] = 16.0f * x;
                    pos[index][1] = 10.0f - 10.0f * y;
                    Vec2 p = userBalls.get(index).getBody().getPosition();
                    float dx = pos[index][0] - (p.x + 8.0f);
                    float dy = pos[index][1] - (10.0f - p.y);
                    float radius = userBalls.get(index).getFixture().getShape().getRadius();
                    if ((float) Math.sqrt(dx * dx + dy * dy) <=  radius + 0.25f)
                        activited[index] = true;
                    else
                        activited[index] = false;
                }
                if (index == 0) {
                    boolean finished = true;
                    for (int i = 0; i < activited.length; ++i)
                        if (!activited[i])
                            finished = false;
                    if (finished && !GameStartView.this.finished) {
                        GameStartView.this.finished = true;
                        message = "Ready!";
                        new Thread(() -> {
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException error) {
                            }
                            GameStartView.this.getViewManager().popView(new Content().putString("status", "ready"));
                        }).start();
                    }
                    else
                        GameStartView.this.getViewManager().repaint();
                }
            }
        };
        LeapController.getLeapController().addHandsListener(listener);
    }


    @Override
    public void onStop() {
        LeapController.getLeapController().removeHandsListener(listener);
    }

    @Override
    public void onPaint(GraphicsWrapper g2) {
        if (!isActive()) return;
        g2.prepare();
        g2.fillRect(0.0f, 0.0f, 16.0f, 10.0f, new Color(0x88, 0x88, 0x88, 80));
        g2.drawStringCentered(message, 1f, new Color(0xff, 0xff, 0xff, 80), 8, 4);
        for (int i = 0; i < pos.length; ++i) {
            if (!online[i]) return;
            Color c = new Color(Colors.USER_BALL[i].getRed(), Colors.USER_BALL[i].getGreen(), Colors.USER_BALL[i].getBlue(), 150);
            if (activited[i])
                g2.fillCircle(pos[i][0], pos[i][1], 0.4f, c);
            else
                g2.fillCircle(pos[i][0], pos[i][1], 0.25f, c);
        }
    }
}
