package com.juegopoli;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Platform {
    private static final int WALL_SIZE = 32; // Tamaño estándar para las paredes
    private Texture texture;
    private float x, y;
    private Rectangle bounds;

    public Platform(float x, float y) {
        this.x = x;
        this.y = y;
        texture = new Texture("wall.png");  // Nueva textura más visible
        bounds = new Rectangle(x + 2, y + 2, WALL_SIZE - 4, WALL_SIZE - 4); // Colisión más precisa
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, WALL_SIZE, WALL_SIZE);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        texture.dispose();
    }
} 