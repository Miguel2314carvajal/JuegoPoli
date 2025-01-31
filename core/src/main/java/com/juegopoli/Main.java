package com.juegopoli;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Texture;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private static final int TILE_SIZE = 32;
    private static final int MAZE_WIDTH = 15;
    private static final int MAZE_HEIGHT = 12;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private Player player;
    private Array<Platform> platforms;
    private Array<Star> stars;
    private Book book;
    private BitmapFont font;
    private GlyphLayout layout;
    private Texture background;
    private boolean gameWon = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 384);

        platforms = new Array<>();
        stars = new Array<>();
        font = new BitmapFont();
        layout = new GlyphLayout();
        background = new Texture("background.png");

        resetGame();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, 640, 480);
        camera.update();
    }

    private void resetGame() {
        platforms.clear();
        createMaze();
        player = new Player(TILE_SIZE, TILE_SIZE, camera.viewportWidth, camera.viewportHeight);
        generateStars();
        book = new Book(13 * TILE_SIZE, 10 * TILE_SIZE);
        gameWon = false;
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        player.update(Gdx.graphics.getDeltaTime(), platforms);

        for (Star star : stars) {
            if (!star.isCollected() && player.checkStarCollision(star)) {
                star.collect();
                player.collectStar();
            }
        }

        if (player.getStars() == 5 && player.checkBookCollision(book)) {
            gameWon = true;
        }

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        for (Platform platform : platforms) platform.render(batch);
        for (Star star : stars) star.render(batch);
        player.render(batch);
        book.render(batch);

        font.draw(batch, "Estrellas: " + player.getStars(), 10, 470);
        font.draw(batch, "¡Recolecta todas las estrellas", 400, 470);
        font.draw(batch, "para poder titularte!", 400, 450);

        if (gameWon) renderVictoryMessage();
        batch.end();

        renderMobileControls();

        if (gameWon && checkResetInput()) {
            resetGame();
        }
    }

    private void renderVictoryMessage() {
        font.getData().setScale(2.0f);
        String message = "¡FELICIDADES, TE TITULASTE!";
        layout.setText(font, message);
        float messageX = camera.viewportWidth / 2 - layout.width / 2;
        float messageY = camera.viewportHeight / 2 + 30;

        font.setColor(0.2f, 0.2f, 0.2f, 0.5f);
        font.draw(batch, message, messageX + 2, messageY - 2);
        font.setColor(1, 0.8f, 0, 1);
        font.draw(batch, message, messageX, messageY);

        font.getData().setScale(1.0f);
        String resetText = (Gdx.app.getType() == Application.ApplicationType.Android) 
                ? "Toca aquí para jugar de nuevo" 
                : "Presiona ESPACIO para jugar de nuevo";
        layout.setText(font, resetText);
        font.setColor(1, 1, 1, 0.9f);
        font.draw(batch, resetText, camera.viewportWidth / 2 - layout.width / 2, messageY - 40);
    }

    private boolean checkResetInput() {
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            return Gdx.input.justTouched();
        }
        return Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        player.dispose();
        for (Platform platform : platforms) platform.dispose();
        font.dispose();
        background.dispose();
    }

    private void createMaze() {
        int[][] mazeLayout = getMazeLayout();
        for (int y = 0; y < mazeLayout.length; y++) {
            for (int x = 0; x < mazeLayout[y].length; x++) {
                if (mazeLayout[y][x] == 1) {
                    platforms.add(new Platform(x * TILE_SIZE, (MAZE_HEIGHT - y - 1) * TILE_SIZE));
                }
            }
        }
    }

    private int[][] getMazeLayout() {
        return new int[][] {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,1,0,0,0,0,0,0,0,1},
            {1,0,1,1,1,0,1,0,1,1,1,1,1,0,1},
            {1,0,0,0,1,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,0,1,1,1,1,1,0,1,1,1,0,1},
            {1,0,0,0,0,0,0,0,1,0,0,0,0,0,1},
            {1,0,1,1,1,1,1,0,1,0,1,1,1,0,1},
            {1,0,0,0,0,0,1,0,1,0,0,0,0,0,1},
            {1,0,1,1,1,0,0,0,0,0,1,1,1,0,1},
            {1,0,0,0,0,0,1,1,1,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        };
    }

    private void generateStars() {
        stars.clear();
        int[][] starPositions = {
            {4, 2}, {9, 4}, {6, 6}, {3, 8}, {10, 8}
        };
        for (int[] pos : starPositions) {
            stars.add(new Star(pos[0] * TILE_SIZE, pos[1] * TILE_SIZE));
        }
    }
}
