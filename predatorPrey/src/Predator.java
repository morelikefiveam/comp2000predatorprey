public class Predator extends Creature {
   
    public Predator(int speed, int hunger, int x, int y) {
        super(speed, hunger, false, x, y);
    }


    @Override
    public void update(int panelWidth, int panelHeight){
        if (target instanceof Prey c) {
            pursue(c);
        } else if (target != null) {
            moveTowards(target);
        } else {
            moveWithBounce(panelWidth, panelHeight);
        }
        recordPosition();
    }
}
