package tankgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main implements GameConstants {
    private JFrame frame;
    private GameEngine engine;
    private GameRenderer renderer;
    private MainMenu menu;
    private JPanel currentPanel;
    private boolean[] keys = new boolean[256];
    
    public Main() {
        engine = new GameEngine();
        renderer = new GameRenderer(engine);
        renderer.setMainApp(this);
        
        frame = new JFrame("Танки в лабиринте");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        
        menu = new MainMenu(engine, renderer, frame, this);
        currentPanel = menu;
        
        setupGlobalInputHandling();
        
        frame.add(menu);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        
        new Timer(16, e -> {
            if (engine.getGameState() == STATE_PLAYING || engine.getGameState() == STATE_PAUSED) {
                renderer.repaint();
            }
            if (engine.getGameState() == STATE_GAME_OVER || engine.getGameState() == STATE_LEVEL_COMPLETE) {
                renderer.repaint();
            }
        }).start();
    }
    
    public void switchToGame() {
        frame.remove(currentPanel);
        currentPanel = renderer;
        frame.add(renderer);
        frame.revalidate();
        frame.repaint();
        renderer.requestFocusInWindow();
        renderer.updateMousePosition(frame.getWidth()/2, frame.getHeight()/2);
    }
    
    public void switchToMenu() {
        frame.remove(currentPanel);
        currentPanel = menu;
        frame.add(menu);
        frame.revalidate();
        frame.repaint();
        menu.requestFocusInWindow();
    }
    
    private void setupGlobalInputHandling() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                int key = e.getKeyCode();
                
                
                if (key == KEY_NEXT_LEVEL && engine.getGameState() == STATE_LEVEL_COMPLETE) {
                    engine.goToNextLevel();
                    if (currentPanel == menu) {
                        switchToGame();
                    }
                    renderer.repaint();
                    return false;
                }
                
                
                if (key == KEY_PAUSE || key == KeyEvent.VK_ENTER) {
                    int state = engine.getGameState();
                    if (state == STATE_PLAYING) {
                        engine.togglePause();
                        renderer.repaint();
                        return false;
                    } else if (state == STATE_PAUSED) {
                        engine.togglePause();
                        renderer.repaint();
                        return false;
                    }
                }
                
                
                if (key == KEY_RESTART) {
                    engine.restartGame();
                    if (currentPanel == menu) {
                        switchToGame();
                    } else {
                        switchToGame();
                    }
                    renderer.repaint();
                    return false;
                }
                
                
                if (key == KEY_QUIT) {
                    int state = engine.getGameState();
                    if (state == STATE_GAME_OVER || state == STATE_LEVEL_COMPLETE) {
                        engine.exitToMenu();
                        switchToMenu();
                    } else {
                        System.exit(0);
                    }
                    return false;
                }
                
                
                if (engine.getGameState() == STATE_PLAYING && key < 256) {
                    keys[key] = true;
                    updatePlayerMovement();
                }
            } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                int key = e.getKeyCode();
                if (key < 256) {
                    keys[key] = false;
                    updatePlayerMovement();
                }
            }
            return false;
        });
        
        // Обработка движения мыши
        renderer.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                renderer.updateMousePosition(e.getX(), e.getY());
            }
        });
        
        // Обработка стрельбы
        renderer.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && engine.getGameState() == STATE_PLAYING) {
                    engine.playerShoot(e.getX(), e.getY());
                }
            }
        });
    }
    
    private void updatePlayerMovement() {
        int dx = 0, dy = 0;
        if (keys[KEY_UP]) dy = -1;
        if (keys[KEY_DOWN]) dy = 1;
        if (keys[KEY_LEFT]) dx = -1;
        if (keys[KEY_RIGHT]) dx = 1;
        engine.movePlayer(dx, dy);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}