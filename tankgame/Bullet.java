package tankgame;

import java.awt.Rectangle;
import java.awt.Color;

public class Bullet {
    private int x, y;
    private int dx, dy;
    private boolean active;
    private boolean isPlayerBullet;
    private Rectangle bounds;
    private Color color;
    
    public Bullet(int x, int y, int targetX, int targetY, boolean isPlayerBullet, int playerColorIndex) {
        this.x = x + GameConstants.TANK_SIZE / 2 - GameConstants.BULLET_SIZE / 2;
        this.y = y + GameConstants.TANK_SIZE / 2 - GameConstants.BULLET_SIZE / 2;
        this.isPlayerBullet = isPlayerBullet;
        this.active = true;
        
        
        if (isPlayerBullet) {
            this.color = GameConstants.PLAYER_BULLET_COLORS[playerColorIndex];
        } else {
            this.color = GameConstants.ENEMY_BULLET_COLOR;
        }
        
        double angle = Math.atan2(targetY - this.y, targetX - this.x);
        this.dx = (int) (GameConstants.BULLET_SPEED * Math.cos(angle));
        this.dy = (int) (GameConstants.BULLET_SPEED * Math.sin(angle));
        
        this.bounds = new Rectangle(this.x, this.y, GameConstants.BULLET_SIZE, GameConstants.BULLET_SIZE);
    }
    
    public void update() {
        x += dx;
        y += dy;
        bounds.setLocation(x, y);
        
        if (x < -50 || x > GameConstants.WINDOW_WIDTH + 50 || 
            y < -50 || y > GameConstants.WINDOW_HEIGHT + 50) {
            active = false;
        }
    }
    
    public Rectangle getBounds() { return bounds; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isPlayerBullet() { return isPlayerBullet; }
    public int getX() { return x; }
    public int getY() { return y; }
    public Color getColor() { return color; }
}