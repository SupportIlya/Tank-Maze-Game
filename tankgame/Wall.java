package tankgame;

import java.awt.Rectangle;

public class Wall {
    private int x, y;
    private Rectangle bounds;
    private boolean destructible;
    private int health;
    
    public Wall(int x, int y, boolean destructible) {
        this.x = x;
        this.y = y;
        this.destructible = destructible;
        this.health = destructible ? 1 : 999; // Разрушаемые стены ломаются с 1 попадания
        this.bounds = new Rectangle(x, y, GameConstants.WALL_SIZE, GameConstants.WALL_SIZE);
    }
    
    public Rectangle getBounds() {
        return bounds;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isDestructible() { return destructible; }
    public int getHealth() { return health; }
    
    public boolean takeDamage() {
        if (destructible) {
            health--;
            return health <= 0;
        }
        return false;
    }
}