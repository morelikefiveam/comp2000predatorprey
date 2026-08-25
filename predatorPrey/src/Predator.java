public class Predator extends Creature {
    


    public Predator(int speed, int hunger, boolean isFood, int x, int y) {
        super(speed, hunger, isFood, x, y);
        this.isFood = isFood;
        
    }
}