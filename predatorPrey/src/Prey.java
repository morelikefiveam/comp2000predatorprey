public class Prey extends Creature {
    private Boolean inDanger;

    public Prey(int speed, int hunger, boolean isFood, int x, int y) {
        super(speed, hunger, isFood, x, y);
        this.inDanger = false;
    }

    public boolean isInDanger(){
        return inDanger;
    }

    public void setInDanger(boolean inDanger){
        this.inDanger = inDanger;
    }
   
    public void movement() {

    }

    @Override
    public void update(int panelWidth, int panelHeight){
        if (target != null) {
            moveTowards(target);
        } else{
            moveWithBounce(panelWidth, panelHeight);
        }
        recordPosition();
    }
}
