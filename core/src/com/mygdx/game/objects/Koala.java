package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.helpers.MapController;
import com.mygdx.game.helpers.InputHandler;
import com.mygdx.game.utils.Settings;

/**
 * Created by ALUMNEDAM on 24/03/2017.
 */

public class Koala {

    private Texture koalaTexture;

    private Animation<TextureRegion> stand;
    private Animation<TextureRegion> walk;
    private Animation<TextureRegion> jump;
    //////

    OrthogonalTiledMapRenderer renderer;

    private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject() {
            return new Rectangle();
        }
    };

    private Array<Rectangle> tiles = new Array<Rectangle>();

    private TiledMap map;

    //////
    private float height;
    private float width;

    private int collectedCoins;

    private Vector2 position;
    private Vector2 velocity;
    private State state;
    private float stateTime;
    private boolean facesRight;
    private boolean grounded;

    private InputHandler inputHandler;
    private MapController collision;

    private Rectangle collisionRect;

    enum State {
        Standing, Walking, Jumping
    }

    public Koala(OrthogonalTiledMapRenderer renderer, TiledMap map) {

        koalaTexture = AssetManager.koalaTexture;

        createAnimations(AssetManager.regions);

        inputHandler = new InputHandler(this);

        this.map = map;
        collision = new MapController(this, map);

        /////

        this.renderer = renderer;

        /////
        position = new Vector2();
        velocity = new Vector2();

        state = State.Walking;

        stateTime = 0;

        facesRight = true;

        grounded = false;
        collectedCoins = 0;
        collisionRect = new Rectangle();
    }

    /**
     * Crea les animacions
     *
     * @param regions
     */
    public void createAnimations(TextureRegion[] regions) {
        stand = new Animation(0, regions[0]);
        jump = new Animation(0, regions[1]);
        walk = new Animation(0.15f, regions[2], regions[3], regions[4]);
        walk.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        width = 1 / 16f * regions[0].getRegionWidth();
        height = 1 / 16f * regions[0].getRegionHeight();
    }

    public void renderKoala(float deltaTime) {

        // based on the koala state, get the animation frame
        TextureRegion frame = null;
        switch (state) {
            case Standing:
                frame = stand.getKeyFrame(stateTime);
                break;
            case Walking:
                frame = walk.getKeyFrame(stateTime);
                break;
            case Jumping:
                frame = jump.getKeyFrame(stateTime);
                break;
        }

        // draw the koala, depending on the current velocity
        // on the x-axis, draw the koala facing either right
        // or left
        Batch batch = renderer.getBatch();
        batch.begin();

        if (facesRight) {
            batch.draw(frame, position.x, position.y, width, height);
        } else {
            batch.draw(frame, position.x + width, position.y, -width, height);
        }
        batch.end();
    }

    public void updateKoala(float deltaTime) {
        if (deltaTime == 0) return;

        if (deltaTime > 0.1f)
            deltaTime = 0.1f;

        //koala.stateTime += deltaTime;
        updateStateTime(deltaTime);

        inputHandler.checkInput();

        /*
        if (Gdx.input.isKeyJustPressed(Input.Keys.B))
            debug = !debug;
        */
        // apply gravity if we are falling
        velocity.add(0, Settings.GRAVITY);

        // clamp the velocity to the maximum, x-axis only
        velocity.x = MathUtils.clamp(velocity.x,
                -Settings.MAX_VELOCITY, Settings.MAX_VELOCITY);

        // If the velocity is < 1, set it to 0 and set state to Standing
        if (Math.abs(velocity.x) < 1) {
            velocity.x = 0;
            if (grounded) state = Koala.State.Standing;
        }

        // multiply by delta time so we know how far we go
        // in this frame
        velocity.scl(deltaTime);

        collision.checkCollision();
        collisionRect.set(position.x,position.y,width,height);

        // unscale the velocity by the inverse delta time and set
        // the latest position
        position.add(velocity);
        velocity.scl(1 / deltaTime);

        // Apply damping to the velocity on the x-axis so we don't
        // walk infinitely once a key was pressed
        velocity.x *= Settings.DAMPING;

        if (position.y <= -20 || position.x >= 200) {
            die();
        }

    }

    public void moveRight() {
        velocity.x = Settings.MAX_VELOCITY;
        if (grounded) state = Koala.State.Walking;
        facesRight = true;
    }

    public void moveLeft() {
        velocity.x = -Settings.MAX_VELOCITY;
        if (grounded) state = Koala.State.Walking;
        facesRight = false;
    }

    public void jump() {
        velocity.y += Settings.JUMP_VELOCITY;
        state = Koala.State.Jumping;
        grounded = false;
    }

    public void die(){
        Gdx.app.log("muerte", "Koala Dead");
        velocity.y = 0;
        collision.resetTiles();
        position.set(16, 3);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPositionX(float posX) {
        position.x = posX;
    }

    public void setPositionY(float posY) {
        position.y = posY;
    }

    public void setVelocityX(float velX) {
        velocity.x = velX;
    }

    public void setVelocityY(float velY) {
        velocity.y = velY;
    }


    public void updateStateTime(float deltaTime) {
        stateTime += deltaTime;
    }

    public float getPositionX() {
        return position.x;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
    }

    public TiledMap getMap() {
        return map;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Pool<Rectangle> getRectPool() {
        return rectPool;
    }

    public Array<Rectangle> getTiles() {
        return tiles;
    }

    public Rectangle getCollisionRect() {
        return collisionRect;
    }
}
