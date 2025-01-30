package com.juegopoli;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Book {
    private static final float BOOK_SIZE = 24;
    private Texture texture;
    private float x, y;
    private Rectangle bounds;

    public Book(float x, float y) {
        this.x = x;
        this.y = y;
        texture = new Texture("book.png");
        bounds = new Rectangle(x, y, BOOK_SIZE, BOOK_SIZE);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, BOOK_SIZE, BOOK_SIZE);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        texture.dispose();
    }
}
