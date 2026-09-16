import java.util.ArrayList;
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
    private static final double REPRODUCE_CHANCE = 0.15;

    public Prey(int speed, int hunger,  int x, int y, List<Creature> sim, List<Grass> grassList) {
        super(speed, hunger, false, x, y);
        this.inDanger = false;
        this.sim = sim;
        this.grassList = grassList;
    }

    public boolean isInDanger(){
        return inDanger;
    }

    public void setInDanger(boolean inDanger){
        this.inDanger = inDanger;
    }

    private static final int REPRODUCE_THRESHOLD = 20;
    @Override
    public void reproduce() {
        if (sim == null){
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
        List<Predator> predatorCandidates = new ArrayList<>();
        for (Creature c : sim) {
            if (c instanceof Predator p) {
                predatorCandidates.add(p);
            }
        }

        Optional<Predator> nearestThreat = findNearestInRange(predatorCandidates, THREAT_RANGE);

        if (nearestThreat.isPresent()) {
            setInDanger(true);
            flee(nearestThreat.get());
            recordPosition();
            return;
        }
        setInDanger(false);

        if (getStarvation() < GRAZE_THRESHOLD) {
            recordPosition();
            return;
        }

        List<Grass> edibleGrass = new ArrayList<>();
        for (Grass g : grassList){
            if (g.isEdible()) {
                edibleGrass.add(g);
            }
        }

        Optional<Grass> target = findClosest(edibleGrass);
        target.ifPresent(grass -> {
            double diffX = grass.getX() - getX();
            double diffY = grass.getY() - getY();
            double dist = Math.hypot(diffX, diffY);
            if (dist == 0) return;
            int moveX = (int) Math.round((diffX / dist) * getSpeed());
            int moveY = (int) Math.round((diffY / dist) * getSpeed());
            setX(getX() + moveX);
            setY(getY() + moveY);
            clampToWorld();
        });

        recordPosition();
    }

    private void flee(Creature threat) {
        double diffX = getX() - threat.getX();
        double diffY = getY() - threat.getY();
        double distance = Math.hypot(diffX, diffY);
        if (distance == 0) {
            diffX = 1;
            distance = 1;
        }

        double boostedSpeed = getSpeed() * FLEE_SPEED_MULTIPLIER;
        int moveX = (int) Math.round((diffX / distance) * boostedSpeed);
        int moveY = (int) Math.round((diffY / distance) * boostedSpeed);

        setX(getX() + moveX);
        setY(getY() + moveY);
        clampToWorld();
    }

    @Override
    protected int getStarvationRate(){
        return 1; //Prey conserve energy more effectively
    }

    @Override
    public void eat(){
        List<Grass> edibleGrass = new ArrayList<>();
        for (Grass g : grassList){
            if (g.isEdible()) {
                edibleGrass.add(g);
            }
        }

        Optional<Grass> closest = findClosest(edibleGrass);
        closest.ifPresent(grass -> {
            double dist = Math.hypot(getX() - grass.getX(), getY() - grass.getY());
            if (dist <= EAT_RADIUS){
                grass.setEaten(true);
                resetStarvation();
            }
        });
    }
}