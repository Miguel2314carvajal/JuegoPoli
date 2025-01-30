package com.juegopoli;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Coin {
    private static final float COIN_SIZE = 16;
    private float x, y;
    private Rectangle bounds;

    public Coin(float x, float y) {
        this.x = x;
        this.y = y;
        bounds = new Rectangle(x, y, COIN_SIZE, COIN_SIZE);
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(1, 1, 0, 1); // Color amarillo
        shapeRenderer.circle(x + COIN_SIZE/2, y + COIN_SIZE/2, COIN_SIZE/2);
    }

    public Rectangle getBounds() {
        return bounds;
    }
} 