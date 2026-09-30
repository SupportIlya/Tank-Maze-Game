package tankgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenu extends JPanel implements GameConstants {
    private GameEngine engine;
    private GameRenderer renderer;
    private Main mainApp;
    private int selectedIndex = 0;
    private int selectedColorIndex = 0;
    private boolean inColorMenu = false;
    private String[] mainOptions = {"Начать игру", "Выбрать цвет танка", "Выход"};
    
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
    
    private String[] colorNames = {
        "Зеленый", "Синий", "Оранжевый", "Фиолетовый",
        "Желтый", "Бирюзовый", "Розовый", "Серебристый"
    };
    
    public MainMenu(GameEngine engine, GameRenderer renderer, JFrame frame, Main app) {
        this.engine = engine;
        this.renderer = renderer;
        this.mainApp = app;
        
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setFocusable(true);
        setBackground(BACKGROUND_COLOR);
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                
                if (inColorMenu) {
                    switch (key) {
                        case KeyEvent.VK_UP:
                            selectedColorIndex = (selectedColorIndex - 1 + colorNames.length) % colorNames.length;
                            repaint();
                            break;
                        case KeyEvent.VK_DOWN:
                            selectedColorIndex = (selectedColorIndex + 1) % colorNames.length;
                            repaint();
                            break;
                        case KeyEvent.VK_ENTER:
                            renderer.setPlayerColor(selectedColorIndex);
                            engine.setPlayerColorIndex(selectedColorIndex);
                            inColorMenu = false;
                            repaint();
                            break;
                        case KeyEvent.VK_ESCAPE:
                            inColorMenu = false;
                            repaint();
                            break;
                    }
                } else {
                    switch (key) {
                        case KeyEvent.VK_UP:
                            selectedIndex = (selectedIndex - 1 + mainOptions.length) % mainOptions.length;
                            repaint();
                            break;
                        case KeyEvent.VK_DOWN:
                            selectedIndex = (selectedIndex + 1) % mainOptions.length;
                            repaint();
                            break;
                        case KeyEvent.VK_ENTER:
                            handleSelection();
                            break;
                    }
                }
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                
                if (inColorMenu) {
                    int startY = 250;
                    int lineHeight = 45;
                    for (int i = 0; i < colorNames.length; i++) {
                        int y = startY + i * lineHeight;
                        int xStart = WINDOW_WIDTH / 2 - 150;
                        int xEnd = WINDOW_WIDTH / 2 + 180;
                        if (mouseY >= y - 20 && mouseY <= y + 20 && mouseX >= xStart && mouseX <= xEnd) {
                            selectedColorIndex = i;
                            renderer.setPlayerColor(selectedColorIndex);
                            engine.setPlayerColorIndex(selectedColorIndex);
                            inColorMenu = false;
                            repaint();
                            break;
                        }
                    }
                    int backY = startY + colorNames.length * lineHeight + 20;
                    if (mouseY >= backY - 15 && mouseY <= backY + 15 && 
                        mouseX >= WINDOW_WIDTH / 2 - 80 && mouseX <= WINDOW_WIDTH / 2 + 80) {
                        inColorMenu = false;
                        repaint();
                    }
                } else {
                    for (int i = 0; i < mainOptions.length; i++) {
                        int y = 400 + i * 70;
                        int xStart = WINDOW_WIDTH / 2 - 120;
                        int xEnd = WINDOW_WIDTH / 2 + 120;
                        if (mouseY >= y - 20 && mouseY <= y + 20 && mouseX >= xStart && mouseX <= xEnd) {
                            selectedIndex = i;
                            repaint();
                            handleSelection();
                            break;
                        }
                    }
                }
            }
        });
    }
    
    private void handleSelection() {
        if (mainOptions[selectedIndex].equals("Начать игру")) {
            engine.startGame();
            mainApp.switchToGame();
        } else if (mainOptions[selectedIndex].equals("Выбрать цвет танка")) {
            inColorMenu = true;
            repaint();
        } else if (mainOptions[selectedIndex].equals("Выход")) {
            System.exit(0);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        GradientPaint gradient = new GradientPaint(0, 0, new Color(30, 30, 40), 
            WINDOW_WIDTH, WINDOW_HEIGHT, new Color(15, 15, 25));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 56));
        g2d.setColor(playerColors[selectedColorIndex]);
        String title = "ТАНКИ В ЛАБИРИНТЕ";
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (WINDOW_WIDTH - fm.stringWidth(title)) / 2;
        g2d.drawString(title, titleX, 120);
        
        if (inColorMenu) {
            drawColorMenu(g2d);
        } else {
            drawMainMenu(g2d);
        }
    }
    
    private void drawMainMenu(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        g2d.setColor(new Color(180, 180, 180));
        String subtitle = "Уничтожь все вражеские танки!";
        g2d.drawString(subtitle, (WINDOW_WIDTH - g2d.getFontMetrics().stringWidth(subtitle)) / 2, 180);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(new Color(255, 215, 0));
        String highScoreText = "★ ЛУЧШИЙ РЕКОРД: " + engine.getHighScore() + " ★";
        g2d.drawString(highScoreText, (WINDOW_WIDTH - g2d.getFontMetrics().stringWidth(highScoreText)) / 2, 240);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.setColor(new Color(150, 150, 150));
        String currentColor = "Текущий цвет: " + colorNames[selectedColorIndex];
        g2d.drawString(currentColor, WINDOW_WIDTH / 2 - 100, 290);
        
        drawMiniTank(g2d, WINDOW_WIDTH / 2 + 100, 280, playerColors[selectedColorIndex]);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 28));
        for (int i = 0; i < mainOptions.length; i++) {
            int y = 370 + i * 70;
            if (i == selectedIndex) {
                g2d.setColor(playerColors[selectedColorIndex]);
                g2d.drawString("> " + mainOptions[i] + " <", WINDOW_WIDTH / 2 - 140, y);
            } else {
                g2d.setColor(new Color(150, 150, 150));
                g2d.drawString(mainOptions[i], WINDOW_WIDTH / 2 - 120, y);
            }
        }
        
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 13));
        g2d.setColor(new Color(100, 100, 100));
        
        String controls1 = "WASD - движение | Мышь - прицел | ЛКМ - стрельба";
        String controls2 = "P/Enter - пауза | R - рестарт | Q - выход | N - след. уровень";
        
        int controls1X = (WINDOW_WIDTH - g2d.getFontMetrics().stringWidth(controls1)) / 2;
        int controls2X = (WINDOW_WIDTH - g2d.getFontMetrics().stringWidth(controls2)) / 2;
        int controlsY = WINDOW_HEIGHT - 65;
        
        g2d.drawString(controls1, controls1X, controlsY);
        g2d.drawString(controls2, controls2X, controlsY + 22);
    }
    
    private void drawColorMenu(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        g2d.setColor(Color.WHITE);
        String title = "Выберите цвет танка";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(title, (WINDOW_WIDTH - fm.stringWidth(title)) / 2, 200);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 22));
        
        int startY = 250;
        int lineHeight = 45;
        
        for (int i = 0; i < colorNames.length; i++) {
            int y = startY + i * lineHeight;
            
            // Цветной квадратик
            g2d.setColor(playerColors[i]);
            g2d.fillRect(WINDOW_WIDTH / 2 - 120, y - 12, 20, 20);
            g2d.setColor(Color.WHITE);
            g2d.drawRect(WINDOW_WIDTH / 2 - 120, y - 12, 20, 20);
            
            // Название цвета
            if (i == selectedColorIndex) {
                g2d.setColor(playerColors[i]);
                g2d.drawString("> " + colorNames[i] + " <", WINDOW_WIDTH / 2 - 90, y);
            } else {
                g2d.setColor(new Color(180, 180, 180));
                g2d.drawString(colorNames[i], WINDOW_WIDTH / 2 - 70, y);
            }
            
            // Мини-танк
            drawMiniTank(g2d, WINDOW_WIDTH / 2 + 120, y - 10, playerColors[i]);
        }
        
        int backY = startY + colorNames.length * lineHeight + 20;
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.setColor(new Color(150, 50, 50));
        String backText = "[ Назад (ESC) ]";
        int backX = (WINDOW_WIDTH - g2d.getFontMetrics().stringWidth(backText)) / 2;
        g2d.drawString(backText, backX, backY);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.setColor(new Color(100, 100, 100));
        String hint = "Используйте стрелки ↑↓ и Enter, или кликните мышью";
        int hintX = (WINDOW_WIDTH - g2d.getFontMetrics().stringWidth(hint)) / 2;
        g2d.drawString(hint, hintX, backY + 35);
    }
    
    private void drawMiniTank(Graphics2D g2d, int x, int y, Color color) {
        g2d.setColor(color);
        g2d.fillRoundRect(x, y, 30, 30, 5, 5);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(x, y, 30, 30, 5, 5);
        
        g2d.setColor(color.darker());
        g2d.fillOval(x + 10, y + 10, 10, 10);
        
        g2d.setColor(color);
        g2d.fillRect(x + 20, y + 13, 15, 4);
    }
}