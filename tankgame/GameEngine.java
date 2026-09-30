package tankgame;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.*;

public class GameEngine implements GameConstants {
    private List<Wall> walls;
    private List<Tank> tanks;
    private List<Bullet> bullets;
    private Tank player;
    private int currentLevel;
    private int gameState;
    private int enemiesToKill;
    private int enemiesKilled;
    private int score;
    private int highScore;
    private int playerColorIndex;
    
    private Thread gameThread;
    private volatile boolean running;
    private Random random;
    private long lastUpdateTime;
    
    public GameEngine() {
        walls = new ArrayList<>();
        tanks = new ArrayList<>();
        bullets = new ArrayList<>();
        currentLevel = LEVEL_1;
        gameState = STATE_MENU;
        enemiesKilled = 0;
        score = 0;
        playerColorIndex = 0;
        random = new Random();
        lastUpdateTime = System.currentTimeMillis();
        loadHighScore();
    }
    
    private void loadHighScore() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HIGH_SCORE_FILE))) {
            highScore = (int) ois.readObject();
        } catch (Exception e) {
            highScore = 0;
        }
    }
    
    private void saveHighScore() {
        if (score > highScore) {
            highScore = score;
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HIGH_SCORE_FILE))) {
                oos.writeObject(highScore);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void startGame() {
        gameState = STATE_PLAYING;
        loadLevel(currentLevel);
        startGameLoop();
    }
    
    private void loadLevel(int level) {
        walls.clear();
        tanks.clear();
        bullets.clear();
        enemiesKilled = 0;
        
        if (level == LEVEL_1) {
            createLevel1Maze();
            player = new Tank(60, 60, true, PLAYER_SPEED);
            spawnEnemyAtSafePosition(800, 600);
            spawnEnemyAtSafePosition(850, 100);
            spawnEnemyAtSafePosition(400, 80);
            spawnEnemyAtSafePosition(750, 350);
            enemiesToKill = 4;
        } else if (level == LEVEL_2) {
            createLevel2Maze();
            player = new Tank(60, 60, true, PLAYER_SPEED);
            spawnEnemyAtSafePosition(800, 600);
            spawnEnemyAtSafePosition(850, 100);
            spawnEnemyAtSafePosition(400, 80);
            spawnEnemyAtSafePosition(750, 350);
            spawnEnemyAtSafePosition(200, 600);
            spawnEnemyAtSafePosition(500, 80);
            enemiesToKill = 6;
        }
        
        tanks.add(player);
    }
    
    private boolean canPlaceWall(int x, int y) {
        Rectangle newWall = new Rectangle(x, y, WALL_SIZE, WALL_SIZE);
        
        for (Wall wall : walls) {
            if (newWall.intersects(wall.getBounds())) {
                return false;
            }
        }
        
        if (x < 0 || x + WALL_SIZE > WINDOW_WIDTH || y < 0 || y + WALL_SIZE > WINDOW_HEIGHT) {
            return false;
        }
        
        return true;
    }
    
    private void addWall(int x, int y, boolean destructible) {
        if (canPlaceWall(x, y)) {
            walls.add(new Wall(x, y, destructible));
        }
    }
    
    private boolean isPositionSafeForTank(int x, int y) {
        Rectangle tankBounds = new Rectangle(x, y, TANK_SIZE, TANK_SIZE);
        
        for (Wall wall : walls) {
            if (tankBounds.intersects(wall.getBounds())) {
                return false;
            }
        }
        
        if (x < 0 || x + TANK_SIZE > WINDOW_WIDTH || y < 0 || y + TANK_SIZE > WINDOW_HEIGHT) {
            return false;
        }
        
        for (Tank tank : tanks) {
            if (tankBounds.intersects(tank.getBounds())) {
                return false;
            }
        }
        
        return true;
    }
    
    private void spawnEnemyAtSafePosition(int x, int y) {
        int[] offsets = {0, 40, -40, 80, -80, 120, -120, 160, -160};
        
        for (int offsetX : offsets) {
            for (int offsetY : offsets) {
                int newX = x + offsetX;
                int newY = y + offsetY;
                if (isPositionSafeForTank(newX, newY)) {
                    tanks.add(new Tank(newX, newY, false, ENEMY_SPEED));
                    return;
                }
            }
        }
        
        for (int attempt = 0; attempt < 200; attempt++) {
            int randomX = 50 + random.nextInt(WINDOW_WIDTH - TANK_SIZE - 100);
            int randomY = 50 + random.nextInt(WINDOW_HEIGHT - TANK_SIZE - 100);
            if (isPositionSafeForTank(randomX, randomY)) {
                tanks.add(new Tank(randomX, randomY, false, ENEMY_SPEED));
                return;
            }
        }
        
        tanks.add(new Tank(100, 100, false, ENEMY_SPEED));
    }
    
    private void createLevel1Maze() {
        
        for (int i = 0; i < 25; i++) {
            addWall(i * WALL_SIZE, 0, false);
            addWall(i * WALL_SIZE, WINDOW_HEIGHT - WALL_SIZE, false);
        }
        for (int i = 1; i < 17; i++) {
            addWall(0, i * WALL_SIZE, false);
            addWall(WINDOW_WIDTH - WALL_SIZE, i * WALL_SIZE, false);
        }
        
        
        addWall(200, 200, false);
        addWall(200, 240, false);
        addWall(200, 280, false);
        
        addWall(750, 200, false);
        addWall(750, 240, false);
        addWall(750, 280, false);
        
        addWall(200, 450, false);
        addWall(200, 490, false);
        addWall(200, 530, false);
        
        addWall(750, 450, false);
        addWall(750, 490, false);
        addWall(750, 530, false);
        
        
        for (int i = 3; i < 22; i++) {
            if (i != 8 && i != 9 && i != 10 && i != 11) {
                addWall(i * WALL_SIZE, 120, true);
            }
            if (i != 12 && i != 13 && i != 14 && i != 15) {
                addWall(i * WALL_SIZE, 240, true);
            }
            if (i != 5 && i != 6 && i != 7) {
                addWall(i * WALL_SIZE, 360, true);
            }
            if (i != 16 && i != 17 && i != 18) {
                addWall(i * WALL_SIZE, 480, true);
            }
        }
        
        for (int i = 4; i < 15; i++) {
            if (i != 6 && i != 7 && i != 8) {
                addWall(160, i * WALL_SIZE, true);
            }
            if (i != 9 && i != 10 && i != 11) {
                addWall(320, i * WALL_SIZE, true);
            }
            if (i != 4 && i != 5 && i != 6) {
                addWall(480, i * WALL_SIZE, true);
            }
            if (i != 10 && i != 11 && i != 12) {
                addWall(640, i * WALL_SIZE, true);
            }
            if (i != 7 && i != 8 && i != 9) {
                addWall(800, i * WALL_SIZE, true);
            }
        }
        
        addWall(400, 300, true);
        addWall(440, 300, true);
        addWall(400, 340, true);
        addWall(440, 340, true);
        
        addWall(550, 180, true);
        addWall(590, 180, true);
        addWall(550, 220, true);
        addWall(590, 220, true);
    }
    
    private void createLevel2Maze() {
        
        for (int i = 0; i < 25; i++) {
            addWall(i * WALL_SIZE, 0, false);
            addWall(i * WALL_SIZE, WINDOW_HEIGHT - WALL_SIZE, false);
        }
        for (int i = 1; i < 17; i++) {
            addWall(0, i * WALL_SIZE, false);
            addWall(WINDOW_WIDTH - WALL_SIZE, i * WALL_SIZE, false);
        }
        
        
        addWall(300, 150, false);
        addWall(300, 190, false);
        addWall(600, 150, false);
        addWall(600, 190, false);
        addWall(300, 500, false);
        addWall(300, 540, false);
        addWall(600, 500, false);
        addWall(600, 540, false);
        
        addWall(450, 350, false);
        addWall(490, 350, false);
        addWall(450, 390, false);
        addWall(490, 390, false);
        
        
        for (int i = 2; i < 23; i++) {
            if (i < 6 || i > 18) {
                addWall(i * WALL_SIZE, 100, true);
            }
            if (i < 5 || i > 19) {
                addWall(i * WALL_SIZE, 200, true);
            }
            if (i < 8 || i > 16) {
                addWall(i * WALL_SIZE, 300, true);
            }
            if (i < 4 || i > 20) {
                addWall(i * WALL_SIZE, 400, true);
            }
            if (i < 7 || i > 17) {
                addWall(i * WALL_SIZE, 500, true);
            }
            addWall(i * WALL_SIZE, 600, true);
        }
        
        for (int i = 3; i < 16; i++) {
            if (i < 5 || i > 13) {
                addWall(120, i * WALL_SIZE, true);
            }
            if (i < 4 || i > 14) {
                addWall(240, i * WALL_SIZE, true);
            }
            if (i < 6 || i > 12) {
                addWall(360, i * WALL_SIZE, true);
            }
            if (i < 7 || i > 11) {
                addWall(500, i * WALL_SIZE, true);
            }
            if (i < 5 || i > 13) {
                addWall(620, i * WALL_SIZE, true);
            }
            if (i < 8 || i > 10) {
                addWall(740, i * WALL_SIZE, true);
            }
            addWall(860, i * WALL_SIZE, true);
        }
        
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if ((i + j) % 2 == 0) {
                    addWall(350 + i * 80, 250 + j * 70, true);
                }
            }
        }
    }
    
    private void startGameLoop() {
        running = true;
        gameThread = new Thread(() -> {
            while (running) {
                long currentTime = System.currentTimeMillis();
                long elapsed = currentTime - lastUpdateTime;
                
                if (elapsed >= GAME_LOOP_DELAY && gameState == STATE_PLAYING) {
                    updateGame();
                    lastUpdateTime = currentTime;
                }
                
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        gameThread.start();
    }
    
    private void updateGame() {
        List<Tank> tanksCopy = new ArrayList<>(tanks);
        for (Tank tank : tanksCopy) {
            if (tank.isActive()) {
                tank.update(walls, tanks);
            }
        }
        
        for (Tank tank : tanks) {
            if (!tank.isPlayer() && tank.isActive() && random.nextInt(100) < 3) {
                tank.aiShoot(bullets, player);
            }
        }
        
        List<Bullet> bulletsToRemove = new ArrayList<>();
        List<Wall> wallsToRemove = new ArrayList<>();
        
        for (Bullet bullet : bullets) {
            bullet.update();
            
            boolean hitSomething = false;
            
            for (Wall wall : walls) {
                if (bullet.getBounds().intersects(wall.getBounds())) {
                    if (wall.isDestructible()) {
                        if (wall.takeDamage()) {
                            wallsToRemove.add(wall);
                        }
                    }
                    bulletsToRemove.add(bullet);
                    hitSomething = true;
                    break;
                }
            }
            
            if (!hitSomething) {
                for (Tank tank : tanks) {
                    if (bullet.isActive() && tank.isActive() && 
                        bullet.isPlayerBullet() != tank.isPlayer() &&
                        bullet.getBounds().intersects(tank.getBounds())) {
                        tank.takeDamage();
                        bulletsToRemove.add(bullet);
                        
                        if (!tank.isPlayer() && !tank.isActive()) {
                            enemiesKilled++;
                            score++;
                            saveHighScore();
                        }
                        hitSomething = true;
                        break;
                    }
                }
            }
            
            if (!hitSomething && (bullet.getX() < -100 || bullet.getX() > WINDOW_WIDTH + 100 ||
                bullet.getY() < -100 || bullet.getY() > WINDOW_HEIGHT + 100)) {
                bulletsToRemove.add(bullet);
            }
        }
        
        walls.removeAll(wallsToRemove);
        bullets.removeAll(bulletsToRemove);
        
        if (enemiesKilled >= enemiesToKill && enemiesToKill > 0) {
            if (currentLevel == LEVEL_1) {
                gameState = STATE_LEVEL_COMPLETE;
            } else if (currentLevel == LEVEL_2) {
                gameState = STATE_GAME_OVER;
                saveHighScore();
            }
        }
        
        if (!player.isActive()) {
            gameState = STATE_GAME_OVER;
            saveHighScore();
        }
    }
    
    public void goToNextLevel() {
        if (currentLevel == LEVEL_1) {
            currentLevel = LEVEL_2;
            gameState = STATE_PLAYING;
            loadLevel(currentLevel);
        }
    }
    
    public void movePlayer(int dx, int dy) {
        if (gameState == STATE_PLAYING && player != null && player.isActive()) {
            player.setDirectionX(dx);
            player.setDirectionY(dy);
        }
    }
    
    public void playerShoot(int mouseX, int mouseY) {
        if (gameState == STATE_PLAYING && player != null && player.isActive()) {
            player.shoot(bullets, mouseX, mouseY, playerColorIndex);
        }
    }
    
    public void togglePause() {
        if (gameState == STATE_PLAYING) {
            gameState = STATE_PAUSED;
        } else if (gameState == STATE_PAUSED) {
            gameState = STATE_PLAYING;
        }
    }
    
    public void restartGame() {
        gameState = STATE_PLAYING;
        currentLevel = LEVEL_1;
        enemiesKilled = 0;
        score = 0;
        loadLevel(currentLevel);
    }
    
    public void exitToMenu() {
        gameState = STATE_MENU;
        currentLevel = LEVEL_1;
        enemiesKilled = 0;
        score = 0;
    }
    
    public void stopGame() {
        running = false;
        if (gameThread != null) {
            gameThread.interrupt();
        }
    }
    
    public void setPlayerColorIndex(int index) {
        this.playerColorIndex = index;
    }
    
    public int getPlayerColorIndex() {
        return playerColorIndex;
    }
    
    public List<Wall> getWalls() { return walls; }
    public List<Tank> getTanks() { return tanks; }
    public List<Bullet> getBullets() { return bullets; }
    public Tank getPlayer() { return player; }
    public int getGameState() { return gameState; }
    public int getCurrentLevel() { return currentLevel; }
    public int getEnemiesKilled() { return enemiesKilled; }
    public int getEnemiesToKill() { return enemiesToKill; }
    public int getScore() { return score; }
    public int getHighScore() { return highScore; }
    public void setGameState(int state) { this.gameState = state; }
}