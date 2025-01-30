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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.graphics.Texture;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private Player player;
    private Array<Platform> platforms;
    private Array<Star> stars;
    private Book book;
    private BitmapFont font;
    private GlyphLayout layout;
    private boolean gameWon = false;
    private static final int TILE_SIZE = 32;
    private static final int MAZE_WIDTH = 15;
    private static final int MAZE_HEIGHT = 12;
    private Texture background;

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
        
        Position startPosition = findStartPosition();
        player = new Player(startPosition.x, startPosition.y, camera.viewportWidth, camera.viewportHeight);
        
        createMaze();
        generateStars();
        book = new Book(13 * TILE_SIZE, 10 * TILE_SIZE);
        background = new Texture("background.png");
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, 640, 480);
        camera.update();
    }

    private void resetGame() {
        // Limpiar y regenerar el laberinto
        platforms.clear();
        createMaze();
        
        // Reiniciar jugador
        Position startPosition = findStartPosition();
        player = new Player(startPosition.x, startPosition.y, camera.viewportWidth, camera.viewportHeight);
        
        // Regenerar estrellas
        generateStars();
        
        // Reposicionar libro
        book = new Book(13 * TILE_SIZE, 10 * TILE_SIZE);
        
        // Reiniciar estado del juego
        gameWon = false;
    }

    private Position findStartPosition() {
        // Implementa la lógica para encontrar la posición inicial del laberinto
        // Esto puede ser una posición específica o una posición aleatoria
        return new Position(TILE_SIZE, TILE_SIZE); // Placeholder, actual implementación necesaria
    }

    private Position findEndPosition() {
        // Implementa la lógica para encontrar la posición final del laberinto
        // Esto puede ser una posición específica o una posición aleatoria
        return new Position(13 * TILE_SIZE, 10 * TILE_SIZE); // Placeholder, actual implementación necesaria
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Actualizar jugador
        player.update(Gdx.graphics.getDeltaTime(), platforms);
        
        // Verificar colisiones con estrellas
        for (Star star : stars) {
            if (!star.isCollected() && player.checkStarCollision(star)) {
                star.collect();
                player.collectStar();
            }
        }
        
        // Verificar victoria
        if (player.getStars() == 5 && player.checkBookCollision(book)) {
            gameWon = true;
        }
        
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        batch.begin();
        // Renderizar elementos del juego
        for (Platform platform : platforms) {
            platform.render(batch);
        }
        for (Star star : stars) {
            star.render(batch);
        }
        player.render(batch);
        book.render(batch);
        
        // Mostrar textos
        font.draw(batch, "Estrellas: " + player.getStars(), 10, 470);
        font.draw(batch, "¡Recolecta todas las estrellas", 400, 470);
        font.draw(batch, "para poder titularte!", 400, 450);
        batch.end();
        
        // Renderizar controles móviles
        renderMobileControls();
        
        // Renderizar mensaje de victoria
        if (gameWon) {
            // Fondo semi-transparente para todo el laberinto
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.7f);
            shapeRenderer.rect(0, 0, camera.viewportWidth, camera.viewportHeight);
            shapeRenderer.end();
            
            batch.begin();
            // Mensaje de victoria
            font.getData().setScale(2.0f);
            String message = "¡FELICIDADES, TE TITULASTE!";
            layout.setText(font, message);
            float messageX = camera.viewportWidth/2 - layout.width/2;
            float messageY = camera.viewportHeight/2 + 30;
            font.draw(batch, message, messageX, messageY);
            
            // Texto para reiniciar según plataforma
            if (Gdx.app.getType() == Application.ApplicationType.Android) {
                font.getData().setScale(1.0f);
                String resetText = "Toca aquí para jugar de nuevo";
                layout.setText(font, resetText);
                float resetX = camera.viewportWidth/2 - layout.width/2;
                font.draw(batch, resetText, resetX, messageY - 40);
            } else {
                font.getData().setScale(1.0f);
                String resetText = "Presiona ESPACIO para jugar de nuevo";
                layout.setText(font, resetText);
                float resetX = camera.viewportWidth/2 - layout.width/2;
                font.draw(batch, resetText, resetX, messageY - 40);
            }
            font.getData().setScale(1f);
            batch.end();
            
            // Verificar reinicio del juego
            if (Gdx.app.getType() == Application.ApplicationType.Android && Gdx.input.justTouched()) {
                float touchY = camera.viewportHeight - Gdx.input.getY() * (camera.viewportHeight / Gdx.graphics.getHeight());
                float touchX = Gdx.input.getX() * (camera.viewportWidth / Gdx.graphics.getWidth());
                
                // Área de toque más grande para reiniciar
                if (touchY >= messageY - 60 && touchY <= messageY - 20 &&
                    touchX >= camera.viewportWidth/2 - 100 && touchX <= camera.viewportWidth/2 + 100) {
                    resetGame();
                }
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                resetGame();
            }
        }
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
        background.dispose();
    }

    private void createMaze() {
        platforms.clear();
        
        // Matriz del laberinto actualizada para coincidir con la imagen
        int[][] mazeLayout = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,1,0,1,0,1,1,1,1,1,0,1},
            {1,0,0,0,1,0,1,0,0,0,0,0,1,0,1},
            {1,1,1,0,1,0,1,1,1,1,1,0,1,0,1},
            {1,0,0,0,1,0,0,0,0,0,1,0,1,0,1},
            {1,0,1,1,1,1,1,1,1,0,1,0,0,0,1},
            {1,0,0,0,0,0,0,0,1,0,1,1,1,0,1},
            {1,0,1,1,1,1,1,0,1,0,0,0,0,0,1},
            {1,0,0,0,0,0,1,0,1,1,1,1,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        };
        
        // Crear las plataformas del laberinto
        for(int y = 0; y < mazeLayout.length; y++) {
            for(int x = 0; x < mazeLayout[y].length; x++) {
                if(mazeLayout[y][x] == 1) {
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
        // Posiciones fijas en los caminos negros (espacios vacíos) del laberinto
        stars.add(new Star(TILE_SIZE * 4, TILE_SIZE * 2));     // Primera estrella
        stars.add(new Star(TILE_SIZE * 9, TILE_SIZE * 4));     // Segunda estrella
        stars.add(new Star(TILE_SIZE * 6, TILE_SIZE * 6));     // Tercera estrella
        stars.add(new Star(TILE_SIZE * 3, TILE_SIZE * 8));     // Cuarta estrella
        stars.add(new Star(TILE_SIZE * 10, TILE_SIZE * 8));    // Quinta estrella
    }

    private void renderMobileControls() {
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            
            float screenWidth = Gdx.graphics.getWidth();
            float screenHeight = Gdx.graphics.getHeight();
            float padSize = screenHeight * 0.3f;
            float padding = screenHeight * 0.1f;
            
            // Color semi-transparente para el pad
            shapeRenderer.setColor(1, 1, 1, 0.3f);
            
            // Círculo central del pad direccional
            float padX = screenWidth - padding - padSize/2;
            float padY = padding + padSize/2;
            float buttonSize = padSize/3;
            
            // Botón central (más grande y transparente)
            shapeRenderer.setColor(1, 1, 1, 0.1f);
            shapeRenderer.circle(padX, padY, buttonSize);
            
            // Botones direccionales
            shapeRenderer.setColor(1, 1, 1, 0.3f);
            shapeRenderer.circle(padX, padY + buttonSize, buttonSize/2);      // Arriba
            shapeRenderer.circle(padX, padY - buttonSize, buttonSize/2);      // Abajo
            shapeRenderer.circle(padX - buttonSize, padY, buttonSize/2);      // Izquierda
            shapeRenderer.circle(padX + buttonSize, padY, buttonSize/2);      // Derecha
            
            shapeRenderer.end();
            
            // Dibujar flechas
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 1, 1, 0.5f);
            float arrowSize = buttonSize * 0.4f;
            
            drawArrow(padX, padY + buttonSize, arrowSize, "up");
            drawArrow(padX, padY - buttonSize, arrowSize, "down");
            drawArrow(padX - buttonSize, padY, arrowSize, "left");
            drawArrow(padX + buttonSize, padY, arrowSize, "right");
            
            shapeRenderer.end();
        }
    }

    private void drawArrow(float x, float y, float size, String direction) {
        switch(direction) {
            case "up":
                shapeRenderer.line(x, y - size/2, x, y + size/2);
                shapeRenderer.line(x, y + size/2, x - size/4, y + size/4);
                shapeRenderer.line(x, y + size/2, x + size/4, y + size/4);
                break;
            case "down":
                shapeRenderer.line(x, y + size/2, x, y - size/2);
                shapeRenderer.line(x, y - size/2, x - size/4, y - size/4);
                shapeRenderer.line(x, y - size/2, x + size/4, y - size/4);
                break;
            case "left":
                shapeRenderer.line(x + size/2, y, x - size/2, y);
                shapeRenderer.line(x - size/2, y, x - size/4, y + size/4);
                shapeRenderer.line(x - size/2, y, x - size/4, y - size/4);
                break;
            case "right":
                shapeRenderer.line(x - size/2, y, x + size/2, y);
                shapeRenderer.line(x + size/2, y, x + size/4, y + size/4);
                shapeRenderer.line(x + size/2, y, x + size/4, y - size/4);
                break;
        }
    }

    private class Position {
        float x, y;
        Position(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
