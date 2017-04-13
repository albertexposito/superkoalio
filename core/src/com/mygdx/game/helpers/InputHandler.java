package com.mygdx.game.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.mygdx.game.Superkoalio;
import com.mygdx.game.objects.Koala;

/**
 * Created by Albert on 25/03/2017.
 */

public class InputHandler {

    private Koala koala;

    public InputHandler(Koala koala) {
        this.koala = koala;
    }

    public static boolean isTouched(float startX, float endX) {
        return false;
    }

    public void checkInput() {

        if ((Gdx.input.isKeyPressed(Input.Keys.SPACE) || isTouched(0.5f, 1)) && koala.isGrounded()) {
            koala.jump();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A) || isTouched(0, 0.25f)) {
            koala.moveLeft();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D) || isTouched(0.25f, 0.5f)) {
            koala.moveRight();
        }

    }

}
