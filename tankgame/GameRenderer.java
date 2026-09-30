package tankgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class GameRenderer extends JPanel implements GameConstants {
    private GameEngine engine;
    private int mouseX, mouseY;
    private Main mainApp;
    private int playerColorIndex = 0;
    
    private Color[] playerColors = {
        new Color(50, 180, 50),
        new Color(50, 150, 255),
        new Color(255, 100, 50),
        new Color(200, 50, 200),
        new Color(255, 220, 50),
        new Color(50, 200, 200),
        new Color(255, 80, 150),
        new Color(150, 150, 150)
    };
    
    public GameRenderer(GameEngine engine) {
        this.engine = engine;
        this.mouseX = WINDOW_WIDTH / 2;
        this.mouseY = WINDOW_HEIGHT / 2;
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setFocusable(true);
        setBackground(BACKGROUND_COLOR);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int state = engine.getGameState();
                int mouseX = e.getX();
                int mouseY = e.getY();
                
                if (state == STATE_LEVEL_COMPLETE) {
                    if (mouseX >= WINDOW_WIDTH/2 - 150 && mouseX <= WINDOW_WIDTH/2 + 150 &&
                        mouseY >= WINDOW_HEIGHT/2 + 50 && mouseY <= WINDOW_HEIGHT/2 + 100) {
                        engine.goToNextLevel();
                        if (mainApp != null) {
                            mainApp.switchToGame();
                        }
                        return;
                    }
                    if (mouseX >= WINDOW_WIDTH/2 - 150 && mouseX <= WINDOW_WIDTH/2 + 150 &&
                        mouseY >= WINDOW_HEIGHT/2 + 120 && mouseY <= WINDOW_HEIGHT/2 + 170) {
                        engine.exitToMenu();
                        if (mainApp != null) {
                            mainApp.switchToMenu();
                        }
                        return;
                    }
                }
                
                if (state == STATE_GAME_OVER) {
                    boolean isWin = (engine.getEnemiesKilled() >= engine.getEnemiesToKill() && engine.getCurrentLevel() == 2);
                    
                    int buttonY1 = isWin ? WINDOW_HEIGHT/2 + 70 : WINDOW_HEIGHT/2 + 30;
                    int buttonY2 = isWin ? WINDOW_HEIGHT/2 + 140 : WINDOW_HEIGHT/2 + 100;
                    
                    if (mouseX >= WINDOW_WIDTH/2 - 150 && mouseX <= WINDOW_WIDTH/2 + 150 &&
                        mouseY >= buttonY1 && mouseY <= buttonY1 + 50) {
                        engine.restartGame();
                        if (mainApp != null) {
                            mainApp.switchToGame();
                        }
                        return;
                    }
                    
                    if (mouseX >= WINDOW_WIDTH/2 - 150 && mouseX <= WINDOW_WIDTH/2 + 150 &&
                        mouseY >= buttonY2 && mouseY <= buttonY2 + 50) {
                        engine.exitToMenu();
                        if (mainApp != null) {
                            mainApp.switchToMenu();
                        }
                        return;
                    }
                }
            }
        });
    }
    
    public void setMainApp(Main app) {
        this.mainApp = app;
    }
    
    public void setPlayerColor(int colorIndex) {
        this.playerColorIndex = colorIndex;
        engine.setPlayerColorIndex(colorIndex);
        repaint();
    }
    
    public void updateMousePosition(int x, int y) {
        this.mouseX = x;
        this.mouseY = y;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        GradientPaint gradient = new GradientPaint(0, 0, new Color(40, 40, 45), 
            WINDOW_WIDTH, WINDOW_HEIGHT, new Color(20, 20, 25));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        int gameState = engine.getGameState();
        
        if (gameState == STATE_PLAYING || gameState == STATE_PAUSED) {
            drawGame(g2d);
            if (gameState == STATE_PAUSED) {
                drawPauseOverlay(g2d);
            }
        } else if (gameState == STATE_LEVEL_COMPLETE) {
            drawGame(g2d);
            drawLevelCompleteScreen(g2d);
        } else if (gameState == STATE_GAME_OVER) {
            drawGame(g2d);
            if (engine.getEnemiesKilled() >= engine.getEnemiesToKill() && engine.getCurrentLevel() == 2) {
                drawWinScreen(g2d);
            } else {
                drawGameOver(g2d);
            }
        }
    }
    
    private void drawGame(Graphics2D g2d) {
        
        for (Wall wall : engine.getWalls()) {
            if (wall.isDestructible()) {
                g2d.setColor(DESTRUCTIBLE_WALL_COLOR);
                g2d.fill3DRect(wall.getX(), wall.getY(), WALL_SIZE, WALL_SIZE, true);
                g2d.setColor(new Color(180, 140, 30));
                g2d.drawRect(wall.getX(), wall.getY(), WALL_SIZE, WALL_SIZE);
                g2d.setColor(new Color(150, 110, 20));
                g2d.drawLine(wall.getX() + 10, wall.getY() + 10, wall.getX() + 30, wall.getY() + 30);
                g2d.drawLine(wall.getX() + 30, wall.getY() + 10, wall.getX() + 10, wall.getY() + 30);
                g2d.fillRect(wall.getX() + 15, wall.getY() + 18, 10, 4);
            } else {
                g2d.setColor(WALL_COLOR);
                g2d.fill3DRect(wall.getX(), wall.getY(), WALL_SIZE, WALL_SIZE, true);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(wall.getX(), wall.getY(), WALL_SIZE, WALL_SIZE);
                g2d.setColor(new Color(80, 60, 30));
                g2d.fillRect(wall.getX() + 5, wall.getY() + 5, 8, 8);
                g2d.fillRect(wall.getX() + 27, wall.getY() + 27, 8, 8);
                g2d.fillRect(wall.getX() + 15, wall.getY() + 15, 10, 10);
            }
        }
        
        
        for (Tank tank : engine.getTanks()) {
            if (!tank.isActive()) continue;
            
            if (tank.isPlayer()) {
                g2d.setColor(playerColors[playerColorIndex]);
            } else {
                g2d.setColor(ENEMY_COLOR);
            }
            
            RoundRectangle2D tankBody = new RoundRectangle2D.Double(
                tank.getX(), tank.getY(), TANK_SIZE, TANK_SIZE, 10, 10);
            g2d.fill(tankBody);
            g2d.setColor(Color.BLACK);
            g2d.draw(tankBody);
            
            int centerX = tank.getX() + TANK_SIZE / 2;
            int centerY = tank.getY() + TANK_SIZE / 2;
            
            double angle;
            if (tank.isPlayer()) {
                angle = Math.atan2(mouseY - centerY, mouseX - centerX);
            } else {
                Tank player = engine.getPlayer();
                if (player != null && player.isActive()) {
                    angle = Math.atan2(player.getY() + TANK_SIZE/2 - centerY, 
                                       player.getX() + TANK_SIZE/2 - centerX);
                } else {
                    angle = 0;
                }
            }
            
            int barrelLength = 25;
            int barrelWidth = 8;
            int barrelX = centerX + (int)(barrelLength * Math.cos(angle)) - barrelWidth/2;
            int barrelY = centerY + (int)(barrelLength * Math.sin(angle)) - barrelWidth/2;
            g2d.setColor(tank.isPlayer() ? playerColors[playerColorIndex].brighter() : new Color(220, 80, 80));
            g2d.fillRect(barrelX, barrelY, barrelWidth, barrelWidth);
            
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillOval(centerX - 8, centerY - 8, 16, 16);
        }
        
        
        for (Bullet bullet : engine.getBullets()) {
            g2d.setColor(bullet.getColor());
            g2d.fillOval(bullet.getX(), bullet.getY(), BULLET_SIZE, BULLET_SIZE);
            g2d.setColor(new Color(bullet.getColor().getRed(), bullet.getColor().getGreen(), bullet.getColor().getBlue(), 100));
            g2d.fillOval(bullet.getX() - 2, bullet.getY() - 2, BULLET_SIZE + 4, BULLET_SIZE + 4);
        }
        
        drawUI(g2d);
    }
    
    private void drawUI(Graphics2D g2d) {
        // Верхняя панель
        g2d.setColor(UI_PANEL_COLOR);
        g2d.fillRect(0, 0, WINDOW_WIDTH, 55);
        
        g2d.setColor(UI_TEXT_COLOR);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
        g2d.drawString("Уровень: " + engine.getCurrentLevel(), 15, 30);
        
        int enemiesLeft = engine.getEnemiesToKill() - engine.getEnemiesKilled();
        g2d.drawString("Врагов: " + enemiesLeft, 150, 30);
        
        
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
        String controls = "WASD-движение | Мышь-прицел | ЛКМ-стрельба | P-пауза | R-рестарт | Q-выход";
        g2d.drawString(controls, 15, 50);
        
        
        g2d.setFont(new Font("Arial", Font.BOLD, 28));
        g2d.setColor(new Color(255, 215, 0));
        String scoreText = "СЧЁТ: " + engine.getScore();
        FontMetrics fm = g2d.getFontMetrics();
        int scoreX = WINDOW_WIDTH - fm.stringWidth(scoreText) - 20;
        int scoreY = WINDOW_HEIGHT - 20;
        g2d.drawString(scoreText, scoreX, scoreY);
        
        
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(new Color(100, 200, 255));
        String highScoreText = "РЕКОРД: " + engine.getHighScore();
        int highScoreX = 20;
        int highScoreY = WINDOW_HEIGHT - 20;
        g2d.drawString(highScoreText, highScoreX, highScoreY);
        
        
        if (engine.getGameState() == STATE_PLAYING) {
            g2d.setColor(new Color(255, 255, 255, 150));
            g2d.drawLine(mouseX - 12, mouseY, mouseX + 12, mouseY);
            g2d.drawLine(mouseX, mouseY - 12, mouseX, mouseY + 12);
            g2d.drawOval(mouseX - 8, mouseY - 8, 16, 16);
        }
    }
    
    private void drawPauseOverlay(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 64));
        g2d.setColor(Color.WHITE);
        String text = "ПАУЗА";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(text, (WINDOW_WIDTH - fm.stringWidth(text)) / 2, WINDOW_HEIGHT / 2);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        g2d.setColor(new Color(200, 200, 200));
        g2d.drawString("Нажмите P или Enter для продолжения", 
                       WINDOW_WIDTH / 2 - 210, WINDOW_HEIGHT / 2 + 60);
    }
    
    private void drawLevelCompleteScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 220));
        g2d.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 56));
        g2d.setColor(new Color(80, 255, 80));
        String text = "УРОВЕНЬ 1 ПРОЙДЕН!";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(text, (WINDOW_WIDTH - fm.stringWidth(text)) / 2, WINDOW_HEIGHT / 2 - 80);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(new Color(150, 255, 150));
        String scoreText = "Ваш счёт: " + engine.getScore();
        g2d.drawString(scoreText, (WINDOW_WIDTH - g2d.getFontMetrics().stringWidth(scoreText)) / 2, WINDOW_HEIGHT / 2 - 20);
        
        drawButton(g2d, WINDOW_WIDTH/2 - 150, WINDOW_HEIGHT/2 + 40, 300, 50, "СЛЕДУЮЩИЙ УРОВЕНЬ", new Color(50, 150, 50));
        drawButton(g2d, WINDOW_WIDTH/2 - 150, WINDOW_HEIGHT/2 + 110, 300, 50, "ВЫХОД В МЕНЮ", new Color(150, 50, 50));
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.setColor(new Color(150, 150, 150));
        g2d.drawString("Нажмите N для перехода на следующий уровень", WINDOW_WIDTH / 2 - 220, WINDOW_HEIGHT / 2 + 200);
    }
    
    private void drawGameOver(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 220));
        g2d.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 56));
        g2d.setColor(new Color(255, 80, 80));
        String text = "ИГРА ОКОНЧЕНА";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(text, (WINDOW_WIDTH - fm.stringWidth(text)) / 2, WINDOW_HEIGHT / 2 - 100);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(new Color(255, 150, 150));
        String reason = "Ваш танк уничтожен!";
        g2d.drawString(reason, (WINDOW_WIDTH - g2d.getFontMetrics().stringWidth(reason)) / 2, WINDOW_HEIGHT / 2 - 40);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 28));
        g2d.setColor(new Color(255, 215, 0));
        String scoreText = "Ваш счёт: " + engine.getScore();
        g2d.drawString(scoreText, (WINDOW_WIDTH - g2d.getFontMetrics().stringWidth(scoreText)) / 2, WINDOW_HEIGHT / 2 + 10);
        
        drawButton(g2d, WINDOW_WIDTH/2 - 150, WINDOW_HEIGHT/2 + 50, 300, 50, "ИГРАТЬ СНОВА", new Color(50, 150, 50));
        drawButton(g2d, WINDOW_WIDTH/2 - 150, WINDOW_HEIGHT/2 + 120, 300, 50, "ВЫХОД В МЕНЮ", new Color(150, 50, 50));
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.setColor(new Color(150, 150, 150));
        g2d.drawString("Нажмите R для рестарта, Q для выхода", WINDOW_WIDTH / 2 - 220, WINDOW_HEIGHT / 2 + 210);
    }
    
    private void drawWinScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 220));
        g2d.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 56));
        g2d.setColor(new Color(80, 255, 80));
        String text = "ПОБЕДА!";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(text, (WINDOW_WIDTH - fm.stringWidth(text)) / 2, WINDOW_HEIGHT / 2 - 80);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(new Color(150, 255, 150));
        String reason = "Вы прошли всю игру!";
        g2d.drawString(reason, (WINDOW_WIDTH - g2d.getFontMetrics().stringWidth(reason)) / 2, WINDOW_HEIGHT / 2 - 20);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 28));
        g2d.setColor(new Color(255, 215, 0));
        String scoreText = "Финальный счёт: " + engine.getScore();
        g2d.drawString(scoreText, (WINDOW_WIDTH - g2d.getFontMetrics().stringWidth(scoreText)) / 2, WINDOW_HEIGHT / 2 + 20);
        
        drawButton(g2d, WINDOW_WIDTH/2 - 150, WINDOW_HEIGHT/2 + 70, 300, 50, "ИГРАТЬ СНОВА", new Color(50, 150, 50));
        drawButton(g2d, WINDOW_WIDTH/2 - 150, WINDOW_HEIGHT/2 + 140, 300, 50, "ВЫХОД В МЕНЮ", new Color(150, 50, 50));
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.setColor(new Color(150, 150, 150));
        g2d.drawString("Нажмите R для рестарта, Q для выхода", WINDOW_WIDTH / 2 - 220, WINDOW_HEIGHT / 2 + 230);
    }
    
    private void drawButton(Graphics2D g2d, int x, int y, int width, int height, String text, Color color) {
        g2d.setColor(color);
        g2d.fillRoundRect(x, y, width, height, 15, 15);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, width, height, 15, 15);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = x + (width - fm.stringWidth(text)) / 2;
        int textY = y + (height + fm.getAscent()) / 2 - 2;
        g2d.drawString(text, textX, textY);
    }
}