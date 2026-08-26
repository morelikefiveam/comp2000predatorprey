public class Grass extends Entity{
    private int growthTimer;
    private boolean isEaten;

    public Grass (int x, int y, int growthTimer){
        super(x, y, true);
        this.growthTimer = growthTimer;
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
    }
}