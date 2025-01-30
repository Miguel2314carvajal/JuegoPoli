package com.juegopoli;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Player {
    private static final float MOVEMENT_SPEED = 200;
    private static final float PLAYER_SIZE = 24; // Tamaño del búho

    private Texture texture;
    private float x;
    private float y;
    private Rectangle bounds;

    public Player(float x, float y) {
        this.x = x;
        this.y = y;
        texture = new Texture("buho.png");
        bounds = new Rectangle(x, y, PLAYER_SIZE, PLAYER_SIZE);
    }

    public void update(float deltaTime, Array<Platform> platforms) {
        float oldX = x;
        float oldY = y;

        // Movimiento horizontal
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            x -= MOVEMENT_SPEED * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            x += MOVEMENT_SPEED * deltaTime;
        }
        
        // Actualizar bounds para colisión horizontal
        bounds.setPosition(x, y);
        // Verificar colisiones horizontales
        if (checkCollision(platforms)) {
            x = oldX;
            bounds.setPosition(x, y);
        }

        // Movimiento vertical
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            y += MOVEMENT_SPEED * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            y -= MOVEMENT_SPEED * deltaTime;
        }

        // Actualizar bounds para colisión vertical
        bounds.setPosition(x, y);
        // Verificar colisiones verticales
        if (checkCollision(platforms)) {
            y = oldY;
            bounds.setPosition(x, y);
        }
    }

    private boolean checkCollision(Array<Platform> platforms) {
        for (Platform platform : platforms) {
            if (bounds.overlaps(platform.getBounds())) {
                return true;
            }
        }
        return false;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, PLAYER_SIZE, PLAYER_SIZE);
    }

    public void dispose() {
        texture.dispose();
    }

    public Rectangle getBounds() {
        return bounds;
    }
} 