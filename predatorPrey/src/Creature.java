import java.util.List;
import java.util.Optional;

public abstract class Creature extends Entity {

    protected int speed; 
    protected int starvation;

    public Creature(int speed, int starvation, boolean isFood, int x, int y) {
        super(x, y, isFood);
        this.speed = speed;
        this.starvation = starvation;
    }

    public abstract void movement();

    public abstract void eat();

    public abstract void reproduce();

    

    public int getSpeed(){
        return speed;
    }

    public void setSpeed(int speed){
        this.speed = speed;
    }

    public int getStarvation(){
        return starvation;
    }

    public void setStarvation(int starvation){
        this.starvation = starvation;
    }
}