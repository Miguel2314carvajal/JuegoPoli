package com.juegopoli;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Star {
    private static final float STAR_SIZE = 24;
    private Texture texture;
    private float x, y;
    private Rectangle bounds;
    private boolean collected;

    public Star(float x, float y) {
        this.x = x;
        this.y = y;
        texture = new Texture("star.png");
        bounds = new Rectangle(x, y, STAR_SIZE, STAR_SIZE);
        collected = false;
    }

    public void render(SpriteBatch batch) {
        if (!collected) {
            batch.draw(texture, x, y, STAR_SIZE, STAR_SIZE);
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
    }

    public void dispose() {
        texture.dispose();
    }
} 