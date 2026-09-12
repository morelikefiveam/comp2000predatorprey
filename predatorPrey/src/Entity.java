public abstract class Entity {
     private int x;
     private int y;
     private boolean isFood;
     private final int originalX;
     private final int originalY;


     public Entity(int x, int y, boolean isFood) {
         this.x = x;
         this.y = y;
         this.originalX = x;
         this.originalY = y;
         this.isFood = isFood;
     }


     public int getOriginalX(){
      return originalX;
     }


     public int getOriginalY(){
      return originalY;
     }


     public int getX(){
        return x;
     }


     public int getY(){
        return y;
     }


     public boolean isFood(){
        return isFood;
     }


     public void setX(int x){
        this.x = x;
     }


     public void setY(int y){
        this.y = y;
     }


     public abstract void update(int panelWidth, int panelHeigth);


     public boolean isNear(Entity other, int range){
      int dx = this.getX() - other.getX();
      int dy = this.getY() - other.getY();
      return Math.sqrt(dx*dx + dy*dy)<=range;
     }
}