import java.util.ArrayList;
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

        List<Prey> preyCandidates = new ArrayList<>();
        for (Creature c : sim) {
            if (c instanceof Prey p) {
                preyCandidates.add(p);
            }
        }

        Optional<Prey> closest = findClosest(preyCandidates);
        closest.ifPresent(prey -> {
            double dist = Math.hypot(getX() - prey.getX(), getY() - prey.getY());
            if (dist <= EAT_RADIUS){
                sim.remove(prey);
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
            Predator offspring = new Predator(getSpeed(), 0, getX(), getY(), sim);
            sim.add(offspring);
            addStarvation(REPRODUCE_THRESHOLD);
        }
    }

    @Override
    public void movement() {
        if (getStarvation() < HUNT_THRESHOLD) {
            moveWithBounce();
            recordPosition();
            return;
        }

        List<Prey> preyCandidates = new ArrayList<>();
        for (Creature c : sim) {
            if (c instanceof Prey p) {
                preyCandidates.add(p);
            }
        }

        Optional<Prey> nearest = findNearestInRange(preyCandidates, DETECTION_RANGE);

        if (nearest.isPresent()) {
            pursue(nearest.get());
        } else {
            moveWithBounce();
        }
        recordPosition();
    }

    private void pursue(Creature target) {
        int velX = target.getVelocityX();
        int velY = target.getVelocityY();

        double huntSpeed = getSpeed() * HUNT_SPEED_MULTIPLIER;

        double distance = Math.hypot(target.getX() - getX(), target.getY() - getY());
        if (distance == 0) return;

        double lookAheadTicks = Math.min(distance / huntSpeed, 10);
        int predictedX = target.getX() + (int) (velX * lookAheadTicks);
        int predictedY = target.getY() + (int) (velY * lookAheadTicks);

        double diffX = predictedX - getX();
        double diffY = predictedY - getY();
        double dist = Math.hypot(diffX, diffY);
        if (dist == 0) return;

        double moveDistance = Math.min(huntSpeed, dist);
        int moveX = (int) Math.round((diffX / dist) * moveDistance);
        int moveY = (int) Math.round((diffY / dist) * moveDistance);

        if (moveX == 0 && Math.abs(diffX) > 0.1) moveX = (int) Math.signum(diffX);
        if (moveY == 0 && Math.abs(diffY) > 0.1) moveY = (int) Math.signum(diffY);

        setX(getX() + moveX);
        setY(getY() + moveY);
        clampToWorld();
    }
}