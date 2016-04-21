package model;

import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

import java.awt.*;

/**
 * Created by Sun on 4/6/2016.
 *
 * Ball class
 */
public class Ball {
    // only valid for user controlled balls.
    public boolean isOnline, isDead;
    public enum BallState {ALIVE, DEAD, REMOVED};
    public int coolDown;

    private Body body;
    private Fixture fixture;
    private Color color;
    private int user;
    private int numContact;

    public Ball(World world, Vec2 pos, Color color, int user) {
        BodyDef bodyDef = new BodyDef();
        if (user == 0)
            bodyDef.type = BodyType.DYNAMIC;
        else {
            bodyDef.type = BodyType.DYNAMIC;
            bodyDef.linearDamping = 10f;
            coolDown = 150;
        }
        bodyDef.position.set(pos);
        bodyDef.gravityScale = 0.0f;
        body = world.createBody(bodyDef);
        body.setUserData(this);

        CircleShape shape = new CircleShape();
        //shape.setRadius((float)(Math.random() * 0.3 + 0.25));
        shape.setRadius(0.4f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 1.0f;
        if (user == 0) {    // Controlled by system
            fixtureDef.filter.categoryBits = 0x0001;
            fixtureDef.filter.maskBits = 0x0003;
        }
        else {  // Controlled by users.
            fixtureDef.filter.categoryBits = 0x0002;
            fixtureDef.filter.maskBits = 0x0001;
        }
        fixture = body.createFixture(fixtureDef);

        this.color = color;
        this.user = user;
    }
    public void remove(World world) {
        world.destroyBody(body);
    }

    public Body getBody() {
        return body;
    }

    public Fixture getFixture() {
        return fixture;
    }

    public Color getColor() {
        return color;
    }

    public int getUser() {
        return user;
    }

    public void startContact() { ++numContact; }

    public void endContact() { --numContact; }

    public int getNumContact() {
        return numContact;
    }
}
