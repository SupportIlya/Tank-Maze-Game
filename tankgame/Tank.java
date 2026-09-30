package tankgame;

import java.awt.Rectangle;
import java.util.List;

public class Tank {
    private int x, y;
    private int speed;
    private boolean isPlayer;
    private int health;
    private int directionX, directionY;
    private Rectangle bounds;
    private long lastShotTime;
    private boolean active;
    
    public Tank(int x, int y, boolean isPlayer, int speed) {
        this.x = x;
        this.y = y;
        this.isPlayer = isPlayer;
        this.speed = speed;
        this.health = 1;
        this.active = true;
        this.directionX = 0;
        this.directionY = 0;
        this.lastShotTime = 0;
        this.bounds = new Rectangle(x, y, GameConstants.TANK_SIZE, GameConstants.TANK_SIZE);
    }
    
    public void update(List<Wall> walls, List<Tank> tanks) {
        if (!active) return;
        
        int newX = x + directionX * speed;
        int newY = y + directionY * speed;
        
        Rectangle newBounds = new Rectangle(newX, newY, GameConstants.TANK_SIZE, GameConstants.TANK_SIZE);
        
        boolean collision = false;
        for (Wall wall : walls) {
            if (newBounds.intersects(wall.getBounds())) {
                collision = true;
                break;
            }
        }
        
        if (!collision) {
            for (Tank other : tanks) {
                if (other != this && other.isActive() && newBounds.intersects(other.getBounds())) {
                    collision = true;
                    break;
                }
            }
        }
        
        if (newX < 0 || newX + GameConstants.TANK_SIZE > GameConstants.WINDOW_WIDTH ||
            newY < 0 || newY + GameConstants.TANK_SIZE > GameConstants.WINDOW_HEIGHT) {
            collision = true;
        }
        
        if (!collision) {
            x = newX;
            y = newY;
            bounds.setLocation(x, y);
        }
        
        if (!isPlayer && active && Math.random() < 0.02) {
            directionX = (int)(Math.random() * 3) - 1;
            directionY = (int)(Math.random() * 3) - 1;
            if (directionX != 0 && directionY != 0) {
                directionX = 0;
                directionY = (int)(Math.random() * 3) - 1;
            }
        }
    }
    
    public boolean canShoot() {
        long now = System.currentTimeMillis();
        long delay = isPlayer ? GameConstants.PLAYER_SHOOT_DELAY : GameConstants.ENEMY_SHOOT_DELAY;
        return now - lastShotTime >= delay;
    }
    
    public void shoot(List<Bullet> bullets, int targetX, int targetY, int playerColorIndex) {
        if (canShoot() && active) {
            bullets.add(new Bullet(x, y, targetX, targetY, isPlayer, playerColorIndex));
            lastShotTime = System.currentTimeMillis();
        }
    }
    
    public void aiShoot(List<Bullet> bullets, Tank player) {
        if (canShoot() && active && player != null && player.isActive()) {
            int targetX = player.getX() + GameConstants.TANK_SIZE / 2;
            int targetY = player.getY() + GameConstants.TANK_SIZE / 2;
            bullets.add(new Bullet(x, y, targetX, targetY, isPlayer, 0));
            lastShotTime = System.currentTimeMillis();
        }
    }
    
    public void takeDamage() {
        health--;
        if (health <= 0) {
            active = false;
        }
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public Rectangle getBounds() { return bounds; }
    public boolean isPlayer() { return isPlayer; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public void setDirectionX(int dx) { this.directionX = dx; }
    public void setDirectionY(int dy) { this.directionY = dy; }
}