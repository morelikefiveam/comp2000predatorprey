public class Creature extends Entity {
    int speed;
    int starvation;

    public Creature(int x, int y, int speed) {
        super(x, y, false);
        this.speed = speed; 
    }
    
}
