public class Grass extends Entity {
    private boolean isEaten;
    private int ageInDays = 0;
    public static final int LIFESPAN_DAYS = 2;
    private boolean markedForRemoval = false;

    public Grass(int x, int y, int growthTimer) {
        super(x, y, true);
        this.isEaten = false;
    }

    public boolean isEdible() {
        return !isEaten;
    }

    public boolean isEaten() {
        return isEaten;
    }

    // Eaten grass is removed immediately by Simulation (see tick()), so this
    // just flags it as no longer edible for the instant before removal.
    public void setEaten(boolean isEaten) {
        this.isEaten = isEaten;
        if (isEaten) {
            markedForRemoval = true;
        }
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    // Called once per in-game day. Grass has a natural lifespan of
    // LIFESPAN_DAYS regardless of whether it's ever eaten.
    public void onDayTick() {
        ageInDays++;
        if (ageInDays >= LIFESPAN_DAYS) {
            markedForRemoval = true;
        }
    }

    @Override
    public void update(int panelWidth, int panelHeight) {
        // No per-tick behaviour: eating triggers immediate removal, and
        // natural expiry is handled once per day via onDayTick().
    }
}