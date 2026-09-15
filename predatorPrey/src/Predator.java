import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



public class Predator extends Creature {
    private List<Creature> sim;
    private static final int DETECTION_RANGE = 200;

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
        resetStarvation();

        // TODO: Needs further work
    }

    @Override 
    public void movement() {
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
            moveWithBounce(800, 600);
        }
        recordPosition();
    }

    private void pursue(Creature target) {
        int velX = target.getVelocityX();
        int velY = target.getVelocityY();

        double distance = Math.hypot(target.getX() - getX(), target.getY() - getY());
        if (distance == 0) return;

        double lookAheadTicks = Math.min(distance / getSpeed(), 10);
        int predictedX = target.getX() + (int) (velX * lookAheadTicks);
        int predictedY = target.getY() + (int) (velY * lookAheadTicks);

        double diffX = predictedX - getX();
        double diffY = predictedY - getY();
        double dist = Math.hypot(diffX, diffY);
        if (dist == 0) return;

        double moveDistance = Math.min(getSpeed(), dist);
        int moveX = (int) Math.round((diffX / dist) * moveDistance);
        int moveY = (int) Math.round((diffY / dist) * moveDistance);

        if (moveX == 0 && Math.abs(diffX) > 0.1) moveX = (int) Math.signum(diffX);
        if (moveY == 0 && Math.abs(diffY) > 0.1) moveY = (int) Math.signum(diffY);

        setX(getX() + moveX);
        setY(getY() + moveY);
    }

    @Override 
    public void reproduce() {
        //TODO
    }
}