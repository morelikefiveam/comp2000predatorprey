
import java.util.stream.Collector;

public final class SpeciesStats {

    private final int count;
    private final int fastest;
    private final double averageSpeed;

    private SpeciesStats(int count, int fastest, double averageSpeed) {
        this.count = count;
        this.fastest = fastest;
        this.averageSpeed = averageSpeed;
    }

    public static SpeciesStats empty() {
        return new SpeciesStats(0, 0, 0);
    }

    public int count() {
        return count;
    }

    public int fastest() {
        return fastest;
    }

    public double averageSpeed() {
        return averageSpeed;
    }

    public static Collector<Creature, long[], SpeciesStats> collector() {
        return Collector.of(
                () -> new long[3],
                (acc, c) -> {
                    acc[0]++;
                    acc[1] += c.getSpeed();
                    acc[2] = Math.max(acc[2], c.getSpeed());
                },
                (a, b) -> {
                    a[0] += b[0];
                    a[1] += b[1];
                    a[2] = Math.max(a[2], b[2]);
                    return a;
                },
                acc -> new SpeciesStats(
                        (int) acc[0],
                        (int) acc[2],
                        acc[0] == 0 ? 0 : (double) acc[1] / acc[0]));
    }
}
