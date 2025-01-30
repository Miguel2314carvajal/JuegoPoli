package com.juegopoli;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Player {
    private static final float MOVEMENT_SPEED = 150;
    private static final float PLAYER_SIZE = 24;
    
    private Texture texture;
    private float x;
    private float y;
    private Rectangle bounds;
    private int stars;

    public Player(float x, float y) {
        this.x = x;
        this.y = y;
        this.stars = 0;
        texture = new Texture(Gdx.files.internal("owl.png"));
        bounds = new Rectangle(x + 4, y + 4, PLAYER_SIZE - 8, PLAYER_SIZE - 8);
    }

    public void update(float deltaTime, Array<Platform> platforms) {
        float oldX = x;
        float oldY = y;
        boolean moved = false;

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

        if (Gdx.input.isTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.input.getY();
            float centerX = Gdx.graphics.getWidth() / 2f;
            float centerY = Gdx.graphics.getHeight() / 2f;

            if (touchX < centerX - 50) {
                x -= MOVEMENT_SPEED * deltaTime;
                moved = true;
            } else if (touchX > centerX + 50) {
                x += MOVEMENT_SPEED * deltaTime;
                moved = true;
            }

            if (touchY < centerY - 50) {
                y += MOVEMENT_SPEED * deltaTime;
                moved = true;
            } else if (touchY > centerY + 50) {
                y -= MOVEMENT_SPEED * deltaTime;
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
