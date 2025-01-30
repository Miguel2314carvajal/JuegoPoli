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

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private Player player;
    private Array<Platform> platforms;
    private Coin coin;
    private boolean gameWon = false;
    private BitmapFont font;
    private static final int TILE_SIZE = 32;
    private Position[] startEndPositions;
    private int[][] currentMaze;
    private static final int MAZE_WIDTH = 20;
    private static final int MAZE_HEIGHT = 15;
    private GlyphLayout layout = new GlyphLayout();

    private class Position {
        float x, y;
        Position(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);
        
        platforms = new Array<>();
        generateRandomMaze();
        createMaze();
        startEndPositions = findStartEndPositions();
        resetGame();
        
        font = new BitmapFont();
        font.getData().setScale(2);
    }

    private void resetGame() {
        generateRandomMaze();
        createMaze();
        startEndPositions = findStartEndPositions();
        Position start = startEndPositions[0];
        Position end = startEndPositions[1];
        player = new Player(start.x, start.y);
        coin = new Coin(end.x, end.y);
        gameWon = false;
    }

    private Position[] findStartEndPositions() {
        Array<Position> validPositions = new Array<>();
        
        for(int row = 0; row < currentMaze.length; row++) {
            for(int col = 0; col < currentMaze[row].length; col++) {
                if(currentMaze[row][col] == 0) {
                    validPositions.add(new Position(
                        col * TILE_SIZE,
                        (currentMaze.length - 1 - row) * TILE_SIZE
                    ));
                }
            }
        }

        // Elegir dos posiciones opuestas
        Position start = validPositions.first();
        Position end = validPositions.get(validPositions.size - 1);
        float maxDistance = 0;

        for(int i = 0; i < validPositions.size; i++) {
            for(int j = i + 1; j < validPositions.size; j++) {
                Position p1 = validPositions.get(i);
                Position p2 = validPositions.get(j);
                float distance = distance(p1, p2);
                
                if(distance > maxDistance) {
                    maxDistance = distance;
                    start = p1;
                    end = p2;
                }
            }
        }

        return new Position[]{start, end};
    }

    private float distance(Position p1, Position p2) {
        float dx = p1.x - p2.x;
        float dy = p1.y - p2.y;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Actualizar jugador y verificar colisión con moneda
        if (!gameWon) {
            player.update(Gdx.graphics.getDeltaTime(), platforms);
            if (checkCoinCollision()) {
                gameWon = true;
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            resetGame();
        }

        // Renderizar el juego
        batch.begin();
        for(Platform platform : platforms) {
            platform.render(batch);
        }
        player.render(batch);
        
        if (gameWon) {
            // Centrar y mejorar el mensaje de victoria
            String message = "¡FELICIDADES, TE TITULASTE!";
            String subMessage = "Presiona ESPACIO para un nuevo desafío";
            
            // Sombra
            font.setColor(0, 0, 0, 1);
            layout.setText(font, message);
            font.draw(batch, message, 
                     320 - layout.width / 2, 240 + layout.height / 2); // Offset para sombra
            layout.setText(font, subMessage);
            font.draw(batch, subMessage, 
                     320 - layout.width / 2, 180 + layout.height / 2);
            
            // Texto principal
            font.setColor(1, 1, 0, 1); // Amarillo
            layout.setText(font, message);
            font.draw(batch, message, 
                     320 - layout.width / 2, 240);
            font.setColor(1, 1, 1, 1); // Blanco
            layout.setText(font, subMessage);
            font.draw(batch, subMessage, 
                     320 - layout.width / 2, 180);
        }
        batch.end();

        // Renderizar moneda si el juego no ha terminado
        if (!gameWon) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            coin.render(shapeRenderer);
            shapeRenderer.end();
        }
    }

    private boolean checkCoinCollision() {
        return player.getBounds().overlaps(coin.getBounds());
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        player.dispose();
        for(Platform platform : platforms) {
            platform.dispose();
        }
        font.dispose();
    }

    private void generateRandomMaze() {
        currentMaze = new int[MAZE_HEIGHT][MAZE_WIDTH];
        
        // Llenar bordes
        for(int i = 0; i < MAZE_HEIGHT; i++) {
            currentMaze[i][0] = 1;
            currentMaze[i][MAZE_WIDTH-1] = 1;
        }
        for(int j = 0; j < MAZE_WIDTH; j++) {
            currentMaze[0][j] = 1;
            currentMaze[MAZE_HEIGHT-1][j] = 1;
        }

        // Generar paredes aleatorias internas
        for(int i = 1; i < MAZE_HEIGHT-1; i++) {
            for(int j = 1; j < MAZE_WIDTH-1; j++) {
                currentMaze[i][j] = Math.random() < 0.3 ? 1 : 0;
            }
        }

        // Asegurar camino desde inicio hasta fin
        ensurePath(1, 1, MAZE_HEIGHT-2, MAZE_WIDTH-2);
    }

    private void ensurePath(int startX, int startY, int endX, int endY) {
        // Crear un camino simple
        currentMaze[startY][startX] = 0;
        currentMaze[endY][endX] = 0;
        
        // Camino vertical
        for(int y = Math.min(startY, endY); y <= Math.max(startY, endY); y++) {
            currentMaze[y][startX] = 0;
        }
        // Camino horizontal
        for(int x = Math.min(startX, endX); x <= Math.max(startX, endX); x++) {
            currentMaze[endY][x] = 0;
        }
    }

    private void createMaze() {
        platforms.clear();
        for(int row = 0; row < currentMaze.length; row++) {
            for(int col = 0; col < currentMaze[row].length; col++) {
                if(currentMaze[row][col] == 1) {
                    platforms.add(new Platform(
                        col * TILE_SIZE,
                        (currentMaze.length - 1 - row) * TILE_SIZE
                    ));
                }
            }
        }
    }
}
