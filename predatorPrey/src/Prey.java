import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Prey extends Creature {
    private boolean inDanger;
    private List<Creature> sim;
    private List<Grass> grassList;
    private static final Random random = new Random();
    private static final int THREAT_RANGE = 120;
    private static final double FLEE_SPEED_MULTIPLIER = 1.8;
    private static final int EAT_RADIUS = 10;
    private static final double REPRODUCE_CHANCE = 0.05;
    private int starvationRate = 1;

    public Prey(int speed, int hunger, int x, int y, List<Creature> sim, List<Grass> grassList) {
        super(speed, hunger, false, x, y);
        this.inDanger = false;
        this.sim = sim;
        this.grassList = grassList;
    }

    public boolean isInDanger() {
        return inDanger;
    }

    public void setInDanger(boolean inDanger) {
        this.inDanger = inDanger;
    }

    private static final int REPRODUCE_THRESHOLD = 20;

    @Override
    public void reproduce() {
        if (sim == null) {
            throw new IllegalStateException("Cannot reproduce: no simulation list assigned");
        }
        if (getStarvation() <= REPRODUCE_THRESHOLD && random.nextDouble() < REPRODUCE_CHANCE) {
            Prey offspring = new Prey(getSpeed(), 0, getX(), getY(), sim, grassList);
            sim.add(offspring);
            addStarvation(REPRODUCE_THRESHOLD);
        }
    }

    private static final int GRAZE_THRESHOLD = 60;

    @Override
    public void movement() {
        Optional<Predator> threat = findNearestInRange(ofType(sim, Predator.class), THREAT_RANGE);
        setInDanger(threat.isPresent());

        if (threat.isPresent()) {
            setStrategy(MovementStrategies.flee(threat.get(), FLEE_SPEED_MULTIPLIER));
        } else if (getStarvation() < GRAZE_THRESHOLD) {
            setStrategy(MovementStrategies.wander());
        } else {
            setStrategy(findClosest(grassList.stream().filter(Grass::isEdible))
                    .map(MovementStrategies::seek)
                    .orElse(MovementStrategies.idle()));
        }
        applyStrategy();
    }

    @Override
    protected int getStarvationRate() {
        if (isInDanger()) {
            return starvationRate * 2; // Prey starve faster when fleeing
        }
        return starvationRate;
    }

    @Override
    public void eat() {
        Optional<Grass> closest = findClosest(grassList.stream().filter(Grass::isEdible));
        closest.ifPresent(grass -> {
            double dist = Math.hypot(getX() - grass.getX(), getY() - grass.getY());
            if (dist <= EAT_RADIUS) {
                grass.setEaten(true);
                resetStarvation();
            }
        });
    }
}