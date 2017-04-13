package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.objects.Koala;
import com.mygdx.game.objects.Plataforma;


/**
 * Created by ALUMNEDAM on 24/03/2017.
 */

public class GameController extends ApplicationAdapter {

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private Koala koala;
    private Plataforma platform, platform2;

    /*
    private InputHandler inputHandler;
    /*private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject() {
            return new Rectangle();
        }
    };*/
   /* private Array<Rectangle> tiles = new Array<Rectangle>();*/

    /*
    private boolean debug = false;
    private ShapeRenderer debugRenderer;
*/


    @Override
    public void create() {

        AssetManager.load();

        map = AssetManager.map;

        renderer = new OrthogonalTiledMapRenderer(map, 1 / 16f);

        // create an orthographic camera, shows us 30x20 units of the world
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 30, 20);
        camera.update();

        // create the Koala we want to move around the world
        koala = new Koala(renderer, map);
        koala.getPosition().set(17, 3);

        platform = new Plataforma(renderer,koala,95, 3, 105,3);
        platform2 = new Plataforma(renderer,koala,117, 3, 127,3);

        //debugRenderer = new ShapeRenderer();

        // Assignem com a gestor d'entrada la classe InputHandler
        //Gdx.input.setInputProcessor(new InputHandler(koala));

    }

    @Override
    public void render() {
        // clear the screen
        Gdx.gl.glClearColor(0.7f, 0.7f, 1.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // get the delta time
        float deltaTime = Gdx.graphics.getDeltaTime();

        // update the koala (process input, collision detection, position update)
        platform.updatePlatform(deltaTime);
        platform2.updatePlatform(deltaTime);
        koala.updateKoala(deltaTime);



        // let the camera follow the koala, x-axis only
        if(koala.getPositionX() > 15 && koala.getPositionX() < 197) {
            camera.position.x = koala.getPositionX();
            camera.update();
        }
        // set the TiledMapRenderer view based on what the
        // camera sees, and render the map
        renderer.setView(camera);
        renderer.render();

        // render the koala
        koala.renderKoala(deltaTime);
        platform.renderPlatform(deltaTime);
        platform2.renderPlatform(deltaTime);

        // render debug rectangles
        //if (debug) renderDebug();
    }

}
