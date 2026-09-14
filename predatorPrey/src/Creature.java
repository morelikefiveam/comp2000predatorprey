import java.util.List;
import java.util.Optional;

public abstract class Creature extends Entity {

    private int speed; 
    private int starvation;
    private int lastX;
    private int lastY;
    private int dx = 1;
    private int dy = 1;

    private static final int STARVATION_THRESHOLD = 100;

    public Creature(int speed, int starvation, boolean isFood, int x, int y) {
        super(x, y, isFood);
        this.speed = speed;
        this.starvation = starvation;
        this.lastX = x;
        this.lastY = y;
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

    protected void moveWithBounce(int panelWidth, int panelHeight){
        int newX = getX() + dx * getSpeed();
        int newY = getY() + dy * getSpeed();

        if (newX < 0 || newX > panelWidth - 20) {
            dx = -dx;
            newX = getX() + dx * getSpeed();
        }
        if (newY < 0 || newY > panelHeight - 20) {
            dy = -dy;
            newY = getY() + dy * getSpeed();
        }

        setX(Math.max(0, Math.min(newX, panelWidth - 20)));
        setY(Math.max(0, Math.min(newY, panelHeight - 20)));
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

    
    
}