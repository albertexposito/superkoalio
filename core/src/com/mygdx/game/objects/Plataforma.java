package com.mygdx.game.objects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.utils.Settings;

/**
 * Created by ALUMNEDAM on 30/03/2017.
 */

public class Plataforma {

    private Texture platformTexture;
    private float speed;
    private Vector2 startingPosition;
    private Vector2 endPosition;
    private Vector2 currentPosition;
    private boolean returning;
    OrthogonalTiledMapRenderer renderer;
    private Koala koala;
    private float width = 5, heigth = 1.5f;
    private Rectangle collisionBox;

    public Plataforma(OrthogonalTiledMapRenderer renderer, Koala koala, float x, float y, float endX, float endY) {
        platformTexture = AssetManager.platformTexture;
        startingPosition = new Vector2(x, y);
        currentPosition = new Vector2(x, y);
        endPosition = new Vector2(endX, endY);
        speed = 0.125f;
        this.renderer = renderer;
        this.koala = koala;
        returning = false;
        collisionBox = new Rectangle();
    }

    public void renderPlatform(float deltaTime) {

        // draw the koala, depending on the current velocity
        // on the x-axis, draw the koala facing either right
        // or left
        Batch batch = renderer.getBatch();
        batch.begin();
        batch.draw(AssetManager.platformTexture, currentPosition.x, currentPosition.y, 5, 1.5f);
        batch.end();
    }

    public void checkKoala() {
        Gdx.app.log("Position", "koala y: " + koala.getPosition().y + " | platform: " + currentPosition.y);
        if (koala.getPosition().x > currentPosition.x && koala.getPosition().x < currentPosition.x + width) {
            if (koala.getPosition().y > (currentPosition.y + 1.5 + 0.03) && koala.getPosition().y > (currentPosition.y + 1.5 - 0.03)) {

                if (!returning) {
                    koala.setPositionX(koala.getPositionX() + speed);
                } else {
                    koala.setPositionX(koala.getPositionX() - speed);
                }

                koala.setPositionY(currentPosition.y + 1.5f);
                koala.setVelocityY(0);
                koala.setGrounded(true);
            }

        }
    }

    public void updatePlatform(float deltaTime) {

        if (!returning) {
            currentPosition.x += speed;

            if (currentPosition.x > endPosition.x) {
                returning = true;
            }

        } else {
            currentPosition.x -= speed;

            if (currentPosition.x < startingPosition.x) {
                returning = false;
            }
        }

        collisionBox.set(currentPosition.x, currentPosition.y, width, heigth);

        //checkKoala();

        if (koala.getCollisionRect().overlaps(this.collisionBox) && (!Gdx.input.isKeyPressed(Input.Keys.SPACE))){
            Gdx.app.log("Collision", "colisionando!!");
            koala.setGrounded(true);

            koala.setVelocityY(0);

            koala.setPositionY(currentPosition.y + 1.48f);
            if (!returning) {
                koala.setPositionX(koala.getPositionX() + speed);
            } else {
                koala.setPositionX(koala.getPositionX() - speed);
            }
        }
    }

}
