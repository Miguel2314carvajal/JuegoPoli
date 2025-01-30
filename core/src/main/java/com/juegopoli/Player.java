package com.juegopoli;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Application;

public class Player {
    private static final float MOVEMENT_SPEED = 150;
    private static final float PLAYER_SIZE = 24;
    
    private Texture texture;
    private float x;
    private float y;
    private Rectangle bounds;
    private int stars;
    private float viewportWidth;
    private float viewportHeight;

    public Player(float x, float y, float viewportWidth, float viewportHeight) {
        this.x = x;
        this.y = y;
        this.stars = 0;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        texture = new Texture(Gdx.files.internal("owl.png"));
        bounds = new Rectangle(x + 4, y + 4, PLAYER_SIZE - 8, PLAYER_SIZE - 8);
    }

    public void update(float deltaTime, Array<Platform> platforms) {
        float oldX = x;
        float oldY = y;
        boolean moved = false;

        // Controles PC
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            x -= MOVEMENT_SPEED * deltaTime;
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            x += MOVEMENT_SPEED * deltaTime;
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            y += MOVEMENT_SPEED * deltaTime;
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            y -= MOVEMENT_SPEED * deltaTime;
            moved = true;
        }

        // Controles móviles
        if (Gdx.app.getType() == Application.ApplicationType.Android && Gdx.input.isTouched()) {
            float screenWidth = Gdx.graphics.getWidth();
            float screenHeight = Gdx.graphics.getHeight();
            float padSize = screenHeight * 0.3f;
            float padding = screenHeight * 0.1f;
            float padX = screenWidth - padding - padSize/2;
            float padY = padding + padSize/2;
            float buttonSize = padSize/3;
            
            float touchX = Gdx.input.getX();
            float touchY = screenHeight - Gdx.input.getY();

            // Verificar toques en cada dirección
            if (checkTouchCircle(touchX, touchY, padX, padY + buttonSize, buttonSize/2)) {
                y += MOVEMENT_SPEED * deltaTime; // Arriba
                moved = true;
            }
            if (checkTouchCircle(touchX, touchY, padX, padY - buttonSize, buttonSize/2)) {
                y -= MOVEMENT_SPEED * deltaTime; // Abajo
                moved = true;
            }
            if (checkTouchCircle(touchX, touchY, padX - buttonSize, padY, buttonSize/2)) {
                x -= MOVEMENT_SPEED * deltaTime; // Izquierda
                moved = true;
            }
            if (checkTouchCircle(touchX, touchY, padX + buttonSize, padY, buttonSize/2)) {
                x += MOVEMENT_SPEED * deltaTime; // Derecha
                moved = true;
            }
        }

        if (moved) {
            bounds.setPosition(x + 4, y + 4);
            if (checkCollision(platforms)) {
                x = oldX;
                y = oldY;
                bounds.setPosition(x + 4, y + 4);
            }
        }
    }

    private boolean checkTouchCircle(float touchX, float touchY, float circleX, float circleY, float radius) {
        float dx = touchX - circleX;
        float dy = touchY - circleY;
        return dx * dx + dy * dy <= radius * radius;
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

    public Rectangle getBounds() {
        return bounds;
    }

    public int getStars() {
        return stars;
    }

    public void collectStar() {
        stars++;
    }

    public void dispose() {
        texture.dispose();
    }

    public boolean checkStarCollision(Star star) {
        return bounds.overlaps(star.getBounds());
    }

    public boolean checkBookCollision(Book book) {
        return bounds.overlaps(book.getBounds());
    }
}
