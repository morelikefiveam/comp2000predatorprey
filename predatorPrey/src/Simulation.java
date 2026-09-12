import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Simulation {
    private List<Entity> entities;
    private List<Entity> initialEntities = new ArrayList<>();
    private SimulationPanel panel;
    private static final int TICKS_PER_DAY = 152;
    private int tickCounter = 0;
    private Random random = new Random();

    public Simulation(SimulationPanel panel) {
        this.entities = new ArrayList<>();
        this.panel = panel;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
        initialEntities.add(entity);
    }

    public void tick() {
        List<Prey> preyList = entities.stream()
            .filter(e -> e instanceof Prey)
            .map(e -> (Prey) e)
            .toList();

        List<Predator> predators = entities.stream()
            .filter(e -> e instanceof Predator)
            .map(e -> (Predator) e)
            .toList();

        List<Grass> grassList = entities.stream()
            .filter(e -> e instanceof Grass)
            .map(e -> (Grass) e)
            .filter(Grass::isEdible)
            .toList();

        List<Prey> availablePrey = new ArrayList<>(preyList);

        for (Predator predator : predators) {
            Entity nearestPrey = findNearest(predator, availablePrey, 200);
            predator.setTarget(nearestPrey);
            if (nearestPrey != null) {
                availablePrey.remove(nearestPrey);
            }
        }

        for (Prey prey : preyList) {
            Entity nearestGrass = findNearest(prey, grassList, 100);
            prey.setTarget(nearestGrass);
        }

        int width = panel.getWidth();
        int height = panel.getHeight();
        for (Entity entity : entities) {
            entity.update(width, height);
        }

        // Anti-merging logic (Separation) for Prey
        for (int i = 0; i < preyList.size(); i++) {
            for (int j = i + 1; j < preyList.size(); j++) {
                Prey p1 = preyList.get(i);
                Prey p2 = preyList.get(j);

                if (p1.isNear(p2, 15)) {
                    int dx = p1.getX() - p2.getX();
                    int dy = p1.getY() - p2.getY();

                    if (dx == 0 && dy == 0) {
                        dx = random.nextBoolean() ? 1 : -1;
                        dy = random.nextBoolean() ? 1 : -1;
                    }

                    p1.setX(p1.getX() + (int) Math.signum(dx) * 2);
                    p1.setY(p1.getY() + (int) Math.signum(dy) * 2);

                    p2.setX(p2.getX() - (int) Math.signum(dx) * 2);
                    p2.setY(p2.getY() - (int) Math.signum(dy) * 2);
                }
            }
        }

        // Anti-merging logic (Separation) for Predators
        for (int i = 0; i < predators.size(); i++) {
            for (int j = i + 1; j < predators.size(); j++) {
                Predator p1 = predators.get(i);
                Predator p2 = predators.get(j);

                if (p1.isNear(p2, 20)) {
                    int dx = p1.getX() - p2.getX();
                    int dy = p1.getY() - p2.getY();

                    if (dx == 0 && dy == 0) {
                        dx = random.nextBoolean() ? 1 : -1;
                        dy = random.nextBoolean() ? 1 : -1;
                    }

                    p1.setX(p1.getX() + (int) Math.signum(dx) * 2);
                    p1.setY(p1.getY() + (int) Math.signum(dy) * 2);

                    p2.setX(p2.getX() - (int) Math.signum(dx) * 2);
                    p2.setY(p2.getY() - (int) Math.signum(dy) * 2);
                }
            }
        }

        Set<Prey> consumedPrey = new HashSet<>();

        for (Predator predator : predators) {
            for (Prey prey : preyList) {
                if (!consumedPrey.contains(prey) && predator.isNear(prey, 35)) {
                    predator.setHunger(predator.getHunger() + 50);
                    predator.setFedToday(true);
                    prey.setHunger(0);
                    consumedPrey.add(prey);
                }
            }
        }

        for (Prey prey : preyList) {
            for (Grass grass : grassList) {
                if (prey.isNear(grass, 35) && grass.isEdible()) {
                    prey.setHunger(prey.getHunger() + 30);
                    prey.setFedToday(true);
                    grass.setEaten(true);
                }
            }
        }

        // Remove dead creatures and grass that was just eaten this tick
        entities.removeIf(e -> e instanceof Creature && ((Creature) e).isDead());
        entities.removeIf(e -> e instanceof Grass g && g.isMarkedForRemoval());

        tickCounter++;
        if (tickCounter >= TICKS_PER_DAY) {
            tickCounter = 0;
            advanceDay();
        }
    }

    private void advanceDay() {
        List<Entity> newborns = new ArrayList<>();

        for (Entity e : entities) {
            if (e instanceof Predator p) {
                p.onDayTick();
                if (p.shouldReproduce(4)) {
                    int offsetX = random.nextInt(21) - 10;
                    int offsetY = random.nextInt(21) - 10;
                    newborns.add(new Predator(p.getSpeed(), 100, p.getX() + offsetX, p.getY() + offsetY));
                }
            } else if (e instanceof Prey p) {
                p.onDayTick();
                if (p.shouldReproduce(3)) {
                    int offsetX = random.nextInt(21) - 10;
                    int offsetY = random.nextInt(21) - 10;
                    newborns.add(new Prey(p.getSpeed(), 100, false, p.getX() + offsetX, p.getY() + offsetY));
                }
            } else if (e instanceof Grass g) {
                g.onDayTick();
            }
        }

        entities.removeIf(e -> e instanceof Predator p && p.isStarved(-5));
        entities.removeIf(e -> e instanceof Prey p && p.isStarved(-4));
        entities.removeIf(e -> e instanceof Grass g && g.isMarkedForRemoval());

        int newGrassCount = 5 + random.nextInt(6);
        for (int i = 0; i < newGrassCount; i++) {
            newborns.add(new Grass(random.nextInt(panel.getWidth()), random.nextInt(panel.getHeight()), 0));
        }
        entities.addAll(newborns);
    }

    public void reset() {
        entities.clear();
        entities.addAll(initialEntities);
        for (Entity entity : entities) {
            entity.setX(entity.getOriginalX());
            entity.setY(entity.getOriginalY());
            if (entity instanceof Creature c) {
                c.resetState(100, entity.getOriginalX(), entity.getOriginalY());
            }
            if (entity instanceof Grass g) {
                g.setEaten(false);
            }
        }
    }

    private Entity findNearest(Entity from, List<? extends Entity> candidates, int range) {
        Entity nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (Entity candidate : candidates) {
            double dx = from.getX() - candidate.getX();
            double dy = from.getY() - candidate.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist <= range && dist < nearestDist) {
                nearest = candidate;
                nearestDist = dist;
            }
        }
        return nearest;
    }
}
