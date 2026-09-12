public abstract class Creature extends Entity {
    private int speed;
    private int hunger;
    private int dx =1;
    private int dy =1;
    protected int energyMeter = 0;
    protected boolean fedToday = false;
    protected Entity target;
    private int lastX;
    private int lastY;
    private boolean facingRight = true;

    public Creature(int speed, int hunger, boolean isFood, int x, int y) {
        super(x, y, isFood);
        validateSpeed(speed);
        validateHunger(hunger);
        this.speed = speed;
        this.hunger = hunger;
        this.lastX = x;
        this.lastY = y;
    }


    public int getSpeed(){
        return speed;
    }

    public boolean isFacingRight(){
        return facingRight;
    }

    protected void updateFacing (int moveX){
        if(moveX>0){
            facingRight = true;
        } else if(moveX<0){
            facingRight = false;
        }
    }

    public void setSpeed(int speed){
        validateSpeed(speed);
        this.speed = speed;
    }


    public int getHunger(){
        return hunger;
    }


    public void setHunger(int hunger){
        validateHunger(hunger);
        this.hunger = hunger;
    }

    public void setFedToday(boolean fedToday){
        this.fedToday = fedToday;
    }


    public boolean isStarved(int deathThreshold){
        return energyMeter <= deathThreshold;
    }


    private void validateSpeed(int speed){
        if(speed <=0){
            throw new InvalidCreatureStateException("Speed must be positive, got: " + speed);
        }
    }


    private void validateHunger(int hunger){
        if(hunger<0){
            throw new InvalidCreatureStateException("Hunger cannot be negative, got: "+ hunger);
        }
    }


    public boolean isDead(){
        return hunger <= 0;
    }


    protected void depleteHunger(){
        hunger--;
    }


    public void setTarget (Entity target){
        this.target = target;
    }


    public int getVelocityX(){
        return getX() - lastX;
    }


    public int getVelocityY(){
        return getY() - lastY;
    }


    protected void recordPosition(){
        lastX = getX();
        lastY = getY();
    }


    protected void moveWithBounce(int panelWidth, int panelHeight){
        int newX = getX() + dx*speed;
        int newY = getY() + dy*speed;


        if(newX<0 || newX>panelWidth){
            dx = -dx;
            newX = getX() + dx*speed;
        }


        if(newY<0 || newY>panelHeight){
            dy = -dy;
            newY = getY() + dy*speed;
        }

        updateFacing(newX - getX());
        setX(newX);
        setY(newY);
    }


    public void onDayTick(){
        if(fedToday){
            energyMeter++;
        } else {
            energyMeter--;
        }
        fedToday = false;
    }


    public boolean shouldReproduce (int reproduceThreshold){
        if(energyMeter >= reproduceThreshold){
            energyMeter = 0;
            return true;
        }
        return false;
    }


    protected void moveTowards(Entity target) {
        double diffX = target.getX() - getX();
        double diffY = target.getY() - getY();
        double distance = Math.sqrt(diffX * diffX + diffY * diffY);

        if (distance == 0) {
            return;
        }

        double moveDistance = Math.min(getSpeed(), distance);
        double stepX = (diffX / distance) * moveDistance;
        double stepY = (diffY / distance) * moveDistance;

        // Round the decimal instead of directly casting to int to prevent truncation
        int moveX = (int) Math.round(stepX);
        int moveY = (int) Math.round(stepY);

        // Enforce a minimum movement of 1 pixel if a fractional step exists
        if (moveX == 0 && Math.abs(stepX) > 0.1) moveX = (int) Math.signum(stepX);
        if (moveY == 0 && Math.abs(stepY) > 0.1) moveY = (int) Math.signum(stepY);

        updateFacing(moveX);
        setX(getX() + moveX);
        setY(getY() + moveY);
    }


    protected void pursue(Creature target) {
        int velX = target.getVelocityX();
        int velY = target.getVelocityY();

        double distance = Math.sqrt(Math.pow(target.getX() - getX(), 2) + Math.pow(target.getY() - getY(), 2));
        
        if (distance == 0) return;

        double lookAheadTicks = distance / getSpeed();

        int predictedX = target.getX() + (int) (velX * lookAheadTicks);
        int predictedY = target.getY() + (int) (velY * lookAheadTicks);

        double diffX = predictedX - getX();
        double diffY = predictedY - getY();
        double dist = Math.sqrt(diffX * diffX + diffY * diffY);

        if (dist == 0) return;

        double moveDistance = Math.min(getSpeed(), dist);
        double stepX = (diffX / dist) * moveDistance;
        double stepY = (diffY / dist) * moveDistance;

        int moveX = (int) Math.round(stepX);
        int moveY = (int) Math.round(stepY);

        if (moveX == 0 && Math.abs(stepX) > 0.1) moveX = (int) Math.signum(stepX);
        if (moveY == 0 && Math.abs(stepY) > 0.1) moveY = (int) Math.signum(stepY);

        updateFacing(moveX);
        setX(getX() + moveX);
        setY(getY() + moveY);
    }

    public void resetState(int startHunger, int startX, int startY) {
        validateHunger(startHunger);
        this.hunger = startHunger;
        this.energyMeter = 0;
        this.fedToday = false;
        this.target = null;
        this.dx = 1;
        this.dy = 1;
        this.lastX = startX;
        this.lastY = startY;
        this.facingRight = true;
    }   
}
