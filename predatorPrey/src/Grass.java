import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Grass extends Entity{
    private int growthTimer;
    private final int maxGrowthTimer = 1000;
    private boolean isEaten;

    public Grass (int x, int y, int growthTimer, boolean isEaten) {
        super(x, y, true);
        this.growthTimer = growthTimer;
        this.isEaten = isEaten;
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