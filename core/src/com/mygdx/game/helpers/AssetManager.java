package com.mygdx.game.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;

/**
 * Created by ALUMNEDAM on 24/03/2017.
 */

public class AssetManager {

    public static Texture koalaTexture;
    public static Texture coinTexture;
    public static Texture platformTexture;
    public static TiledMap map;
    public static TextureRegion[] regions;
    public static TextureRegion coinRot;
    public static Array<StaticTiledMapTile> animTile;

    public static void load() {

        koalaTexture = new Texture("koalio.png");
        coinTexture = new Texture("coin_sprite.png");
        platformTexture = new Texture("platform_sprite.png");

        regions = TextureRegion.split(koalaTexture, 18, 26)[0];
       // coinRot = new TextureRegion(coinTexture,64,64);
        map = new TmxMapLoader().load("level1.tmx");

        animTile = new Array<StaticTiledMapTile>();

        for (int i = 0; i<6; i++) {
            animTile.add(new StaticTiledMapTile(new TextureRegion(coinTexture, i*16,0,16,16)));
        }
    }

}
