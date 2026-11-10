public final class MovementStrategies {
    private MovementStrategies() {
    }

    public static MovementStrategy wander() {
        return self -> self.moveWithBounce();
    }

    public static MovementStrategy idle() {
        return self -> {
        };
    }

    public static MovementStrategy seek(Entity target) {
        return self -> {
            double diffX = target.getX() - self.getX();
            double diffY = target.getY() - self.getY();
            double dist = Math.hypot(diffX, diffY);
            if (dist == 0)
                return;
            int moveX = (int) Math.round((diffX / dist) * self.getSpeed());
            int moveY = (int) Math.round((diffY / dist) * self.getSpeed());
            self.setX(self.getX() + moveX);
            self.setY(self.getY() + moveY);
            self.clampToWorld();
        };
    }

    public static MovementStrategy flee(Creature threat, double speedMultiplier) {
        return self -> {
            double diffX = self.getX() - threat.getX();
            double diffY = self.getY() - threat.getY();
            double distance = Math.hypot(diffX, diffY);
            if (distance == 0) {
                diffX = 1;
                distance = 1;
            }
            double boostedSpeed = self.getSpeed() * speedMultiplier;
            int moveX = (int) Math.round((diffX / distance) * boostedSpeed);
            int moveY = (int) Math.round((diffY / distance) * boostedSpeed);
            self.setX(self.getX() + moveX);
            self.setY(self.getY() + moveY);
            self.clampToWorld();
        };
    }

    public static MovementStrategy pursue(Creature target, double speedMultiplier) {
        return self -> {
            int velX = target.getVelocityX();
            int velY = target.getVelocityY();
            double huntSpeed = self.getSpeed() * speedMultiplier;

            double distance = Math.hypot(target.getX() - self.getX(), target.getY() - self.getY());
            if (distance == 0)
                return;

            double lookAheadTicks = Math.min(distance / huntSpeed, 10);
            int predictedX = target.getX() + (int) (velX * lookAheadTicks);
            int predictedY = target.getY() + (int) (velY * lookAheadTicks);

            double diffX = predictedX - self.getX();
            double diffY = predictedY - self.getY();
            double dist = Math.hypot(diffX, diffY);
            if (dist == 0)
                return;

            double moveDistance = Math.min(huntSpeed, dist);
            int moveX = (int) Math.round((diffX / dist) * moveDistance);
            int moveY = (int) Math.round((diffY / dist) * moveDistance);

            if (moveX == 0 && Math.abs(diffX) > 0.1)
                moveX = (int) Math.signum(diffX);
            if (moveY == 0 && Math.abs(diffY) > 0.1)
                moveY = (int) Math.signum(diffY);

            self.setX(self.getX() + moveX);
            self.setY(self.getY() + moveY);
            self.clampToWorld();
        };
    }
}