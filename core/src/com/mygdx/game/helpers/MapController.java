package com.mygdx.game.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.objects.Koala;

/**
 * Created by Albert on 26/03/2017.
 */

public class MapController {

    private Koala koala;
    private Pool<Rectangle> rectPool;
    private Array<Rectangle> tiles;


    TiledMapTileLayer layerSolid;
    TiledMapTileLayer layerHidden;
    TiledMapTileLayer layerBreakable;
    TiledMapTileLayer layerSpikes;
    TiledMapTileLayer layerCoins;

    enum LayerType {
        Solid, Breakable, Hidden, none
    }

    public MapController(Koala koala, TiledMap map) {
        this.koala = koala;

        rectPool = koala.getRectPool();
        tiles = koala.getTiles();

        layerSolid = (TiledMapTileLayer) koala.getMap().getLayers().get("solid-block");
        layerHidden = (TiledMapTileLayer) koala.getMap().getLayers().get("hidden-block");
        layerBreakable = (TiledMapTileLayer) koala.getMap().getLayers().get("breakable-block");
        layerSpikes = (TiledMapTileLayer) koala.getMap().getLayers().get("spikes");
        layerCoins = (TiledMapTileLayer) koala.getMap().getLayers().get("coins");

        resetTiles();

    }

    public void checkCollision() {

        Rectangle koalaRect = rectPool.obtain();
        koalaRect.set(koala.getPosition().x, koala.getPosition().y, koala.getWidth(), koala.getHeight());
        int startX, startY, endX, endY;

        if (koala.getVelocity().x > 0) {
            startX = endX = (int) (koala.getPosition().x + koala.getWidth() + koala.getVelocity().x);
        } else {
            startX = endX = (int) (koala.getPosition().x + koala.getVelocity().x);
        }

        startY = (int) (koala.getPosition().y);
        endY = (int) (koala.getPosition().y + koala.getHeight());

        getTiles(startX, startY, endX, endY, tiles);

        koalaRect.x += koala.getVelocity().x;

        for (Rectangle tile : tiles) {
            if (koalaRect.overlaps(tile)) {
                koala.setVelocityX(0);
                break;
            }
        }
        koalaRect.x = koala.getPosition().x;

        // if the koala is moving upwards, check the tiles to the top of its
        // top bounding box edge, otherwise check the ones to the bottom
        if (koala.getVelocity().y > 0) {
            startY = endY = (int) (koala.getPosition().y + koala.getHeight() + koala.getVelocity().y);
        } else {
            startY = endY = (int) (koala.getPosition().y + koala.getVelocity().y);
        }
        startX = (int) (koala.getPosition().x);
        endX = (int) (koala.getPosition().x + koala.getWidth());
        getTiles(startX, startY, endX, endY, tiles);
        koalaRect.y += koala.getVelocity().y;

        for (Rectangle tile : tiles) {
            if (koalaRect.overlaps(tile)) {
                // we actually reset the koala y-position here
                // so it is just below/above the tile we collided with
                // this removes bouncing :)

                if (koala.getVelocity().y > 0) {
                    koala.setPositionY(tile.y - koala.getHeight());
                    // we hit a block jumping upwards, let's destroy it!
                    layerBreakable.setCell((int) tile.x, (int) tile.y, null);
                    if (layerHidden.getCell((int) tile.x, (int) tile.y) != null) {
                        layerHidden.getCell((int) tile.x, (int) tile.y).setTile(koala.getMap().getTileSets().getTile(158));
                    }

                } else {
                    koala.setPositionY(tile.y + tile.height);
                    // if we hit the ground, mark us as grounded so we can jump
                    koala.setGrounded(true);
                }
                koala.setVelocityY(0);
                break;
            }
        }
        rectPool.free(koalaRect);
    }

    private void getTiles(int startX, int startY, int endX, int endY, Array<Rectangle> tiles) {
        rectPool.freeAll(tiles);
        tiles.clear();

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                TiledMapTileLayer.Cell cell = layerSolid.getCell(x, y);
                if (cell != null) {
                    Rectangle rect = rectPool.obtain();
                    rect.set(x, y, 1, 1);
                    tiles.add(rect);
                    break;
                }

                cell = layerBreakable.getCell(x, y);
                if (cell != null) {
                    Rectangle rect = rectPool.obtain();
                    rect.set(x, y, 1, 1);
                    tiles.add(rect);
                    break;
                }

                cell = layerHidden.getCell(x, y);
                if (cell != null) {
                    Rectangle rect = rectPool.obtain();
                    rect.set(x, y, 1, 1);
                    tiles.add(rect);
                    break;
                }

                cell = layerCoins.getCell(x, y);
                if (cell != null) {
                    Rectangle rect = rectPool.obtain();
                    rect.set(x, y, 1, 1);
                    cell.setTile(null);
                    break;
                }

                cell = layerSpikes.getCell(x, y);
                if (cell != null) {
                    if (koala.getPosition().y == y) {
                        koala.die();
                    }
                }

            }
        }
    }

    public void resetTiles() {

        //Tornem els blocs de la hiddenLayer invisibles
        for (int y = 0; y < layerHidden.getHeight(); y++) {
            for (int x = 0; x < layerHidden.getWidth(); x++) {
                if (layerHidden.getCell(x, y) != null) {
                    layerHidden.getCell(x, y).setTile(null);
                }
            }
        }

        //Inicialitzem les monedes
        AnimatedTiledMapTile test = new AnimatedTiledMapTile(0.075f, AssetManager.animTile);

        for (int y = 0; y < layerCoins.getHeight(); y++) {
            for (int x = 0; x < layerCoins.getWidth(); x++) {
                if (layerCoins.getCell(x, y) != null) {
                    layerCoins.getCell(x, y).setTile(test);
                }
            }
        }

    }

}
