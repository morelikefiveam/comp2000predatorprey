public class Grass extends Entity{
    int growthTimer;
    boolean isEaten;

    public Grass (int growthTimer, boolean isEaten){
        super((int)(Math.random() * 100), (int)(Math.random() * 100), true);
        this.growthTimer = 10;
        this.isEaten = isEaten;
    }
}