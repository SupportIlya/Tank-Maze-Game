package tankgame;

import java.awt.Color;

public interface GameConstants {
    
    int WINDOW_WIDTH = 1000;
    int WINDOW_HEIGHT = 700;
    
    
    int TANK_SIZE = 40;
    int BULLET_SIZE = 8;
    int WALL_SIZE = 40;
    
    
    int PLAYER_SPEED = 5;
    int ENEMY_SPEED = 2;
    int BULLET_SPEED = 10;
    
    
    int PLAYER_SHOOT_DELAY = 400;
    int ENEMY_SHOOT_DELAY = 1500;
    int GAME_LOOP_DELAY = 16;
    
    
    Color[] PLAYER_COLORS = {
        new Color(50, 180, 50),   // Зеленый
        new Color(50, 150, 255),  // Синий
        new Color(255, 100, 50),  // Оранжевый
        new Color(200, 50, 200),  // Фиолетовый
        new Color(255, 220, 50),  // Желтый
        new Color(50, 200, 200),  // Бирюзовый
        new Color(255, 80, 150),  // Розовый
        new Color(150, 150, 150)  // Серебристый
    };
    
    
    Color[] PLAYER_BULLET_COLORS = {
        new Color(100, 255, 100), // Зеленый снаряд
        new Color(100, 200, 255), // Синий снаряд
        new Color(255, 150, 100), // Оранжевый снаряд
        new Color(255, 100, 255), // Фиолетовый снаряд
        new Color(255, 255, 100), // Желтый снаряд
        new Color(100, 255, 255), // Бирюзовый снаряд
        new Color(255, 150, 200), // Розовый снаряд (Для Гриши)
        new Color(200, 200, 200)  // Серебристый снаряд
    };
    
    String[] COLOR_NAMES = {
        "Зеленый", "Синий", "Оранжевый", "Фиолетовый",
        "Желтый", "Бирюзовый", "Розовый", "Серебристый"
    };
    
    
    Color ENEMY_COLOR = new Color(200, 50, 50);
    Color ENEMY_BULLET_COLOR = new Color(255, 100, 100);
    Color BACKGROUND_COLOR = new Color(30, 30, 35);
    Color WALL_COLOR = new Color(100, 70, 40);
    Color DESTRUCTIBLE_WALL_COLOR = new Color(220, 180, 50);
    Color UI_TEXT_COLOR = new Color(220, 220, 220);
    Color UI_PANEL_COLOR = new Color(0, 0, 0, 180);
    
    
    int KEY_UP = 87;
    int KEY_DOWN = 83;
    int KEY_LEFT = 65;
    int KEY_RIGHT = 68;
    int KEY_PAUSE = 80;
    int KEY_RESTART = 82;
    int KEY_QUIT = 81;
    int KEY_NEXT_LEVEL = 78;
    
    
    int STATE_MENU = 0;
    int STATE_PLAYING = 1;
    int STATE_PAUSED = 2;
    int STATE_GAME_OVER = 3;
    int STATE_LEVEL_COMPLETE = 4;
    
    
    int LEVEL_1 = 1;
    int LEVEL_2 = 2;
    
    
    String HIGH_SCORE_FILE = "highscore.dat";
}