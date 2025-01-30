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
        
        // Crear el jugador en la posición correcta
        player = new Player(TILE_SIZE + 4, TILE_SIZE + 4);
        
        createMaze();
        generateStars();
        book = new Book(13 * TILE_SIZE, 10 * TILE_SIZE);
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, 640, 480);
        camera.update();
    }

    private void resetGame() {
        // Generar nuevo laberinto
        createMaze();
        
        // Encontrar posiciones de inicio y fin
        Position start = findStartPosition();
        Position end = findEndPosition();
        
        // Crear jugador y libro
        player = new Player(start.x, start.y);
        book = new Book(end.x, end.y);
        
        // Limpiar y regenerar estrellas
        if (stars != null) {
            stars.clear();
        } else {
            stars = new Array<>();
        }
        generateStars();
        
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
        
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        player.update(Gdx.graphics.getDeltaTime(), platforms);
        
        // Verificar colisiones
        for(Star star : stars) {
            if (!star.isCollected() && player.getBounds().overlaps(star.getBounds())) {
                star.collect();
                player.collectStar();
            }
        }
        
        if (!gameWon && player.getBounds().overlaps(book.getBounds()) && player.getStars() >= 5) {
            gameWon = true;
        }
        
        // Renderizar elementos del juego
        batch.begin();
        for(Platform platform : platforms) {
            platform.render(batch);
        }
        
        for(Star star : stars) {
            if (!star.isCollected()) {
                star.render(batch);
            }
        }
        
        player.render(batch);
        if (!gameWon) {
            book.render(batch);
        }
        
        // Mostrar textos
        font.draw(batch, "Estrellas: " + player.getStars(), 10, 470);
        font.draw(batch, "¡Recolecta todas las estrellas", 400, 470);
        font.draw(batch, "para poder titularte!", 400, 450);
        batch.end();
        
        // Renderizar mensaje de victoria
        if (gameWon) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.7f);
            float messageX = 320 - layout.width/2;
            float messageY = 240;
            shapeRenderer.rect(messageX - 10, messageY - 30, 
                             layout.width + 20, 60);
            shapeRenderer.end();
            
            batch.begin();
            font.getData().setScale(1.2f);
            String message = "¡FELICIDADES, TE TITULASTE!";
            font.draw(batch, message, messageX, messageY + 10);
            font.getData().setScale(1f);
            batch.end();
        }
        
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            renderMobileControls();
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

    private void generateStars() {
        stars.clear();
        // Posiciones de estrellas basadas en el valor 2 en la matriz
        stars.add(new Star(13 * TILE_SIZE + 4, 9 * TILE_SIZE + 4));  // Estrella superior
        stars.add(new Star(7 * TILE_SIZE + 4, 8 * TILE_SIZE + 4));   // Estrella media alta
        stars.add(new Star(1 * TILE_SIZE + 4, 6 * TILE_SIZE + 4));   // Estrella media
        stars.add(new Star(11 * TILE_SIZE + 4, 5 * TILE_SIZE + 4));  // Estrella media baja
        stars.add(new Star(9 * TILE_SIZE + 4, 3 * TILE_SIZE + 4));   // Estrella inferior
    }

    private void renderMobileControls() {
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 0.2f);
            
            float screenWidth = Gdx.graphics.getWidth();
            float screenHeight = Gdx.graphics.getHeight();
            
            // Dividir la pantalla en cuatro cuadrantes
            shapeRenderer.rect(0, 0, screenWidth/2, screenHeight/2); // Abajo-izquierda
            shapeRenderer.rect(screenWidth/2, 0, screenWidth/2, screenHeight/2); // Abajo-derecha
            shapeRenderer.rect(0, screenHeight/2, screenWidth/2, screenHeight/2); // Arriba-izquierda
            shapeRenderer.rect(screenWidth/2, screenHeight/2, screenWidth/2, screenHeight/2); // Arriba-derecha
            
            shapeRenderer.end();
        }
    }
}
