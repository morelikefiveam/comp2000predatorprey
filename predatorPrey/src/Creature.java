public class Creature extends Entity {
    private int speed;
    private int hunger;
    

    public Creature(int speed, int hunger, boolean isFood, int x, int y) {
        super(x, y, isFood);
        this.speed = speed;
        this.hunger = hunger;
    }

    public int getSpeed(){
        return speed;
    }

    public void setSpeed(int speed){
        this.speed = speed;
    }

    public int getHunger(){
        return hunger;
    }

    public void setHunger(int hunger){
        this.hunger = hunger;
    }
}