public class Creature extends Entity {
    int speed;
    int hunger;
    

    public Creature(int speed, int hunger, boolean isFood, int x, int y) {
        super(x, y, isFood);
        this.speed = speed;
        this.hunger = hunger;
    }
}