package model;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.Contact;
import view.Colors;
import view.SoundManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Sun on 4/6/2016.
 *
 * Game model class.
 */
public class GameModel {
    private static final Vec2 GRAVITY = new Vec2(0.0f, -40.0f);
    private static final float INITIAL_SPEED = 8.0f;

    public World world = new World(GRAVITY);
    public final ArrayList<Ball> userBalls = new ArrayList<>(), systemBalls = new ArrayList<>();

    private int nUsers;
    private int gameStatus, gameCd;

    public static final int READY = 0;
    public static final int STARTED = 1;
    public static final int FINISHED = 2;

    private Body boundary;
    public float[] corners = new float[4];

    public GameModel() {
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Object a = contact.getFixtureA().getBody().getUserData(),
                        b = contact.getFixtureB().getBody().getUserData();
                if (a instanceof Ball && b instanceof Ball) {
                    Ball balla = (Ball)a, ballb = (Ball)b;
                    if(balla.getUser() == 0 && ballb.getUser() != 0)
                        ballb.startContact();
                    else if(balla.getUser() != 0 && ballb.getUser() == 0)
                        balla.startContact();
                }
            }
            @Override
            public void endContact(Contact contact) {
                Object a = contact.getFixtureA().getBody().getUserData(),
                        b = contact.getFixtureB().getBody().getUserData();
                if (a instanceof Ball && b instanceof Ball) {
                    Ball balla = (Ball)a, ballb = (Ball)b;
                    if(balla.getUser() == 0 && ballb.getUser() != 0)
                        ballb.endContact();
                    else if(balla.getUser() != 0 && ballb.getUser() == 0)
                        balla.endContact();
                }
            }
            @Override
            public void preSolve(Contact contact, Manifold manifold) {}
            @Override
            public void postSolve(Contact contact, ContactImpulse contactImpulse) {}
        });
    }
    public void setBoundary(float leftupx, float leftupy, float rightbuttomx, float rightbuttomy) {
        synchronized (this) {
            if (boundary != null)
                world.destroyBody(boundary);
            corners[0] = leftupx;
            corners[1] = leftupy;
            corners[2] = rightbuttomx;
            corners[3] = rightbuttomy;
            ChainShape chainShape = new ChainShape();
            chainShape.createLoop(new Vec2[]{
                    new Vec2(leftupx, rightbuttomy),
                    new Vec2(rightbuttomx, rightbuttomy),
                    new Vec2(rightbuttomx, leftupy),
                    new Vec2(leftupx, leftupy),
            }, 4);
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyType.KINEMATIC;
            boundary = world.createBody(bodyDef);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = chainShape;
            fixtureDef.density = 1.0f;
            fixtureDef.friction = 0.0f;
            fixtureDef.restitution = 1.00f;
            fixtureDef.filter.categoryBits = 0x0001;
            fixtureDef.filter.maskBits = 0x0003;
            boundary.createFixture(fixtureDef);
        }
    }
    public void setBoundary() {
        setBoundary(-8, 10, 8, 0);
    }

    public int getnUsers() {
        return nUsers;
    }

    public void setnUsers(int nUsers) {
        synchronized (this) {
            for (Ball i : userBalls)
                i.remove(world);
            userBalls.clear();
            this.nUsers = nUsers;
            double space = 16.0 / (nUsers + 1);
            for (int i = 0; i < nUsers; ++i)
                userBalls.add(new Ball(world, new Vec2((float) (space * (i + 1) - 8.0), 2.0f), Colors.USER_BALL[i % Colors.USER_BALL.length], i + 1));
        }
    }
    public void setnBalls(int nBalls) {
        synchronized (this) {
            for (Ball i : systemBalls)
                i.remove(world);
            systemBalls.clear();
            for (int i = 0; i < nBalls; ++i)
                addBallRandom();
        }
    }
    public void setRandomSpeed(float speed) {
        synchronized (this) {
            for (Ball i : systemBalls) {
                double angel = new Random().nextFloat() * (float) Math.PI * 2.0f;
                i.getBody().setLinearVelocity(new Vec2((float) (speed * Math.cos(angel)), (float) (speed * Math.sin(angel))));
            }
        }
    }

    public void addBall(Vec2 pos, Color color) {
        synchronized (this) {
            systemBalls.add(new Ball(world, pos, color, 0));
        }
    }
    public void addBallRandom() {
        float x = corners[0] + new Random().nextFloat() * (corners[2] - corners[0]),
                y = corners[1] + new Random().nextFloat() * (corners[3] - corners[1]);
        addBall(new Vec2(x, y), Colors.SYSTEM_BALL);
    }

    public void setUserOnline(int index, boolean online) {
        synchronized (this) {
            userBalls.get(index).isOnline = online;
        }
    }
}
