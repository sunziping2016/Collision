package model;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.Contact;

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
    private static final Color[] userColor = new Color[] {
            new Color(0xff0000),
            new Color(0x0000ff),
            new Color(0x00ff00),
    };
    private static final Color systemColor = new Color(0x00ffff);

    public World world = new World(GRAVITY);
    public ArrayList<Ball> userBalls = new ArrayList<>(), systemBalls = new ArrayList<>();

    private int nUsers;
    private int gameStatus, gameCd;

    public static final int READY = 0;
    public static final int STARTED = 1;
    public static final int FINISHED = 2;

    private Body boundary;

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
            public void preSolve(Contact contact, Manifold manifold) { }

            @Override
            public void postSolve(Contact contact, ContactImpulse contactImpulse) { }
        });

        ChainShape chainShape = new ChainShape();
        chainShape.createLoop(new Vec2[] {
                new Vec2(-8, 0),
                new Vec2(8, 0),
                new Vec2(8, 10),
                new Vec2(-8, 10),
        }, 4);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.KINEMATIC;
        boundary = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = chainShape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 1.0f;
        fixtureDef.filter.categoryBits = 0x0001;
        fixtureDef.filter.maskBits = 0x0003;
        boundary.createFixture(fixtureDef);

        setnUsers(2);
    }

    public int getnUsers() {
        return nUsers;
    }

    public void setnUsers(int nUsers) {
        userBalls.clear();
        this.nUsers = nUsers;
        double space = 16.0 / (nUsers + 1);
        for (int i = 0; i < nUsers; ++i)
            userBalls.add(new Ball(world, new Vec2((float)(space * (i + 1) - 8.0), 2.0f), new Vec2(), userColor[i] , i + 1));
    }
    public void addBall(Vec2 pos, Vec2 vel) {
        systemBalls.add(new Ball(world, pos, vel, systemColor, 0));
    }
    public void addBallRandom() {
        float x = -8.0f + new Random().nextFloat() * 16.0f,
                y = new Random().nextFloat() * 10.0f,
                angel = new Random().nextFloat() * (float) Math.PI * 2.0f;
        final float speed = 12;
        addBall(new Vec2(x, y), new Vec2((float) (speed * Math.cos(angel)), (float) (speed * Math.sin(angel))));
    }
}
