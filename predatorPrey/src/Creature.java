
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Creature extends Entity {

    private int speed;
    private int starvation;
    private int lastX;
    private int lastY;
    private int dx;
    private int dy;
    private MovementStrategy strategy;
    private boolean alive = true;

    private static final Random random = new Random();
    private static final int STARVATION_THRESHOLD = 100;


    private static final int BASE_SPEED = 5;
    private static final int MIN_SPEED = 2;
    private static final int MAX_SPEED = 9;
    private static final double METABOLIC_STEP = 0.25;

    // Sets world size, shares with rendering
    public static final int WORLD_WIDTH = 1500;
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

    public boolean starve() {
        starvation += Math.max(0, getStarvationRate() + metabolicAdjustment());
        return starvation >= STARVATION_THRESHOLD;
    }

    private int metabolicAdjustment() {
        int delta = speed - BASE_SPEED;
        double chance = Math.min(1.0, Math.abs(delta) * METABOLIC_STEP);
        return random.nextDouble() < chance ? Integer.signum(delta) : 0;
    }

// Offspring speed: the parent's speed nudged by -1, 0 or +1
    protected int mutatedSpeed() {
        int mutated = speed + random.nextInt(3) - 1;
        return Math.max(MIN_SPEED, Math.min(MAX_SPEED, mutated));
    }

    protected void recordPosition() {
        lastX = getX();
        lastY = getY();
    }

    public int getVelocityX() {
        return getX() - lastX;
    }

    public int getVelocityY() {
        return getY() - lastY;
    }

    protected void moveWithBounce() {
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
    protected void clampToWorld() {
        setX(Math.max(0, Math.min(getX(), WORLD_WIDTH - SIZE)));
        setY(Math.max(0, Math.min(getY(), WORLD_HEIGHT - SIZE)));
    }

    public void resetStarvation() {
        starvation = 0;
    }

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        alive = false;
    }

    protected void addStarvation(int amount) {
        starvation += amount;
    }

    protected abstract void chooseStrategy();

    public void movement() {
        chooseStrategy();
        applyStrategy();
    }

    public abstract void eat();

    public abstract void reproduce();

    public double distanceTo(Entity other) {
        return Math.hypot(getX() - other.getX(), getY() - other.getY());
    }

    protected static <T extends Creature> List<T> ofType(List<? extends Creature> all, Class<T> type) {
        return all.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toList());
    }

    public <E extends Entity> Optional<E> findClosest(List<E> targets) {
        return findClosest(targets.stream());
    }

    public <E extends Entity> Optional<E> findClosest(Stream<E> targets) {
        return targets.min(Comparator.comparingDouble(this::distanceTo));
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getStarvation() {
        return starvation;
    }

    protected <E extends Creature> Optional<E> findNearestInRange(List<E> candidates, int range) {
        return findClosest(candidates.stream().filter(c -> distanceTo(c) <= range));

    }

    protected void setStrategy(MovementStrategy strategy) {
        this.strategy = strategy;
    }

    protected void applyStrategy() {
        strategy.move(this);
        recordPosition();
    }

}
