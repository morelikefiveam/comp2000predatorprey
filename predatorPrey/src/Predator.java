import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Predator extends Creature {
    private List<Creature> sim;
    private static final Random random = new Random();
    private static final int DETECTION_RANGE = 200;
    private static final int EAT_RADIUS = 22;
    private static final int REPRODUCE_THRESHOLD = 20;
    private static final double REPRODUCE_CHANCE = 0.08;
    private static final int HUNT_THRESHOLD = 70;
    private static final double HUNT_SPEED_MULTIPLIER = 2.2; // clear edge over Prey's FLEE_SPEED_MULTIPLIER (1.8)

    public Predator(int speed, int hunger, int x, int y, List<Creature> sim) {
        super(speed, hunger, false, x, y);
        this.sim = sim;
    }

    @Override
    protected int getStarvationRate(){
        return 3; //Predators require more energy to hunt so starvation rate is higher
    }

    @Override
    public void eat() {
        if (getStarvation() < HUNT_THRESHOLD) {
            return;
        }

        Optional<Prey> closest = findClosest(ofType(sim, Prey.class));
        closest.ifPresent(prey -> {
            double dist = Math.hypot(getX() - prey.getX(), getY() - prey.getY());
            if (dist <= EAT_RADIUS) {
                sim.remove(prey);
                prey.kill();
                resetStarvation();  
            }
        });
    }

    @Override
    public void reproduce() {
        if (sim == null){
            throw new IllegalStateException("Cannot reproduce: no simulation list assigned");
        }
        if (getStarvation() <= REPRODUCE_THRESHOLD && random.nextDouble() < REPRODUCE_CHANCE) {
            Predator offspring = new Predator(mutatedSpeed(), 0, getX(), getY(), sim);
            sim.add(offspring);
            addStarvation(REPRODUCE_THRESHOLD);
        }
    }

    @Override
    protected void chooseStrategy() {
        if (getStarvation() < HUNT_THRESHOLD) {
            setStrategy(MovementStrategies.wander());
        } else {
            setStrategy(findNearestInRange(ofType(sim, Prey.class), DETECTION_RANGE)
                    .map(prey -> MovementStrategies.pursue(prey, HUNT_SPEED_MULTIPLIER))
                    .orElse(MovementStrategies.wander()));
        }
    }
}