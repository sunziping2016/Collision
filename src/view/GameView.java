package view;

import controller.GameController;
import controller.Notification;
import model.Ball;
import model.GameModel;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import java.awt.*;
import java.util.LinkedList;

/**
 * Created by Sun on 4/6/2016.
 *
 * GameView class.
 */
public class GameView extends View {
    GameModel model;
    LinkedList<Notification> notifs;

    public GameView(GameModel model) {
        super();
        this.model = model;
        notifs = new LinkedList<Notification>();
    }

    @Override
    public void draw(GraphicsWrapper g2, boolean active) {
        synchronized (model.systemBalls) {
            for (Ball i : model.systemBalls) {
                Vec2 pos = i.getBody().getPosition();;
                g2.fillCircle(pos.x + 8.0f, 10.0f - pos.y, i.getFixture().getShape().getRadius(), i.getColor());
                //System.out.println(i + " " + i.getBody().getLinearVelocity() + " " + i.getBody().getLinearVelocity().length());
            }
        }
        synchronized (model.userBalls) {
            for (int i = 0; i < model.userBalls.size(); ++i){
                Ball ball = model.userBalls.get(i);
                Vec2 pos = ball.getBody().getPosition();
                Color color = ball.getColor();
                if (!ball.isOnline || ball.isDead)
                    color = ball.getColor().darker();
                if (ball.coolDown > 0)
                    color = Colors.mixtue(ball.getColor(), Color.WHITE, 0.5f);
                g2.fillCircle(pos.x + 8.0f, 10.0f - pos.y, ball.getFixture().getShape().getRadius(), color);
            }
        }
    }
}
