import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public abstract class Creature extends Entity {

    private int speed; 
    private int starvation;
    private int lastX;
    private int lastY;
    private int dx;
    private int dy;

    private static final Random random = new Random();
    private static final int STARVATION_THRESHOLD = 100;

    // Sets world size, shares with rendering
    public static final int WORLD_WIDTH = 1200;
    public static final int WORLD_HEIGHT = 800;
    protected static final int SIZE = 20;

    public Creature(int speed, int starvation, boolean isFood, int x, int y) {
        super(x, y, isFood);
        this.speed = speed;
        this.starvation = starvation;
        this.lastX = x;
        this.lastY = y;
        this.dx = random.nextBoolean() ? 1 : -1;
        this.dy = random.nextBoolean() ? 1 : -1;
    }

    protected abstract int getStarvationRate();

    public boolean starve(){
        starvation += getStarvationRate();
        return starvation >= STARVATION_THRESHOLD;
    }

    protected void recordPosition(){
        lastX = getX();
        lastY = getY();
    }

    public int getVelocityX(){
        return getX() - lastX;
    }

    public int getVelocityY(){
        return getY() - lastY;
    }

    protected void moveWithBounce(){
        int newX = getX() + dx * getSpeed();
        int newY = getY() + dy * getSpeed();

        if (newX < 0 || newX > WORLD_WIDTH - SIZE) {
            dx = -dx;
            newX = getX() + dx * getSpeed();
        }
        if (newY < 0 || newY > WORLD_HEIGHT - SIZE) {
            dy = -dy;
            newY = getY() + dy * getSpeed();
        }

        setX(Math.max(0, Math.min(newX, WORLD_WIDTH - SIZE)));
        setY(Math.max(0, Math.min(newY, WORLD_HEIGHT - SIZE)));
    }

    // Keeps a creature inside the visible world after any direct setX/setY movement
    protected void clampToWorld(){
        setX(Math.max(0, Math.min(getX(), WORLD_WIDTH - SIZE)));
        setY(Math.max(0, Math.min(getY(), WORLD_HEIGHT - SIZE)));
    }

    public void resetStarvation(){
        starvation = 0;
    }

    public abstract void movement();
    public abstract void eat();
    public abstract void reproduce();

    public <E extends Entity> Optional<E> findClosest(List<E> targets){
        E closest = null;
        double minDist = Double.MAX_VALUE;

        for (E target : targets){
            double dist = Math.hypot(getX() - target.getX(), getY() - target.getY());
            if (dist < minDist) {
                minDist = dist;
                closest = target;
        }
    }
    return Optional.ofNullable(closest);
}

    public int getSpeed(){
        return speed;
    }

    public void setSpeed(int speed){
        this.speed = speed;
    }

    public int getStarvation(){
        return starvation;
    }

    protected <E extends Creature> Optional<E> findNearestInRange(List<E> candidates, int range) {
        List<E> withinRange = new ArrayList<>();
        for (E c : candidates) {
            if (Math.hypot(getX() - c.getX(), getY() - c.getY()) <= range) {
                withinRange.add(c);
            }
        }
        return findClosest(withinRange);
    }
    
}