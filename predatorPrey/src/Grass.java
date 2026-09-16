public class Grass extends Entity{
    private int growthTimer;
    private final int maxGrowthTimer;
    private boolean isEaten;

    public Grass (int x, int y, int growthTimer){
        super(x, y, true);
        this.growthTimer = growthTimer;
        this.maxGrowthTimer = growthTimer;
        this.isEaten = false;
    }

    public boolean isEdible(){
        return !isEaten;
    }

    public int getGrowthTimer(){
        return growthTimer;
    }

    public void setGrowthTimer(int growthTimer){
        this.growthTimer = growthTimer;
    }

    public boolean isEaten(){
        return isEaten;
    }

    public void setEaten(boolean isEaten){
        this.isEaten = isEaten;
        if (isEaten) {
            this.growthTimer = maxGrowthTimer;
        }
    }

    public void tickGrowth(){
        if (isEaten) {
            growthTimer--;
            if (growthTimer <= 0) {
                isEaten = false;
                growthTimer = maxGrowthTimer;
            }
        }
    }
}