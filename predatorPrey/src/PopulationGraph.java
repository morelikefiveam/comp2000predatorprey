import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;

public class PopulationGraph extends JPanel {
    private static final int MAX_SAMPLES = 150;
    private static final double SPEED_AXIS_MAX = 10; // speeds are clamped to 2..9 in Creature
    private static final Font STATS_FONT = new Font("SansSerif", Font.BOLD, 14);
    private static final Color PREY_SPEED_COLOR = Color.CYAN;
    private static final Color PRED_SPEED_COLOR = Color.ORANGE;

    private final List<Creature> sim;
    // {prey, predators, preyAvgSpeed, predatorAvgSpeed}; speed is NaN when the species is extinct
    private final Deque<double[]> samples = new ArrayDeque<>();
    private Set<Creature> previous = new HashSet<>();

    private int currentPrey;
    private int currentPredators;
    private int peakPrey;
    private int peakPredators;
    private int peakTotal;
    private int preyDeaths;
    private int predatorDeaths;
    private SpeciesStats preyStats = SpeciesStats.empty();
    private SpeciesStats predatorStats = SpeciesStats.empty();

    public PopulationGraph(List<Creature> sim) {
        this.sim = sim;
        setBackground(Color.BLACK);
    }

    public void sample() {
        List<Creature> snapshot = new ArrayList<>(sim);
        Set<Creature> current = new HashSet<>(snapshot);

        // Anyone who was alive at the last sample but isn't now has died
        for (Creature c : previous) {
            if (!current.contains(c)) {
                if (c instanceof Prey) {
                    preyDeaths++;
                } else {
                    predatorDeaths++;
                }
            }
        }
        previous = current;

        preyStats = snapshot.parallelStream()
                .filter(c -> c instanceof Prey)
                .collect(SpeciesStats.collector());
        predatorStats = snapshot.parallelStream()
                .filter(c -> c instanceof Predator)
                .collect(SpeciesStats.collector());

        currentPrey = preyStats.count();
        currentPredators = predatorStats.count();

        peakPrey = Math.max(peakPrey, currentPrey);
        peakPredators = Math.max(peakPredators, currentPredators);
        peakTotal = Math.max(peakTotal, currentPrey + currentPredators);

        samples.addLast(new double[] {
                currentPrey,
                currentPredators,
                currentPrey == 0 ? Double.NaN : preyStats.averageSpeed(),
                currentPredators == 0 ? Double.NaN : predatorStats.averageSpeed() });
        if (samples.size() > MAX_SAMPLES) {
            samples.removeFirst();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        double max = Math.max(1, samples.stream().mapToDouble(s -> Math.max(s[0], s[1])).max().orElse(1));
        double step = getWidth() / (double) MAX_SAMPLES;
        int usableHeight = getHeight() - 4;

        double[] last = null;
        int i = 0;
        for (double[] s : samples) {
            if (last != null) {
                int x1 = (int) ((i - 1) * step);
                int x2 = (int) (i * step);

                g.setColor(Color.BLUE);
                g.drawLine(x1, getHeight() - (int) (last[0] / max * usableHeight),
                        x2, getHeight() - (int) (s[0] / max * usableHeight));
                g.setColor(Color.RED);
                g.drawLine(x1, getHeight() - (int) (last[1] / max * usableHeight),
                        x2, getHeight() - (int) (s[1] / max * usableHeight));

                drawSpeedSegment(g, PREY_SPEED_COLOR, x1, x2, last[2], s[2], usableHeight);
                drawSpeedSegment(g, PRED_SPEED_COLOR, x1, x2, last[3], s[3], usableHeight);
            }
            last = s;
            i++;
        }

        g.setFont(STATS_FONT);
        g.setColor(Color.WHITE);
        g.drawString("Peak - total: " + peakTotal + "  prey: " + peakPrey
                + "  predators: " + peakPredators, 5, 18);
        g.drawString("Now - prey: " + currentPrey + "  predators: " + currentPredators, 5, 38);
        g.drawString("Deaths - prey: " + preyDeaths + "  predators: " + predatorDeaths, 5, 58);

        // Average speed line, with each species in its own line colour
        FontMetrics fm = g.getFontMetrics();
        int x = 5;
        int y = 78;
        g.setColor(Color.WHITE);
        String head = "Avg speed - ";
        g.drawString(head, x, y);
        x += fm.stringWidth(head);

        g.setColor(PREY_SPEED_COLOR);
        String preyText = "prey: " + format(preyStats) + "   ";
        g.drawString(preyText, x, y);
        x += fm.stringWidth(preyText);

        g.setColor(PRED_SPEED_COLOR);
        g.drawString("predators: " + format(predatorStats), x, y);
    }

    private void drawSpeedSegment(Graphics g, Color color, int x1, int x2,
            double from, double to, int usableHeight) {
        if (Double.isNaN(from) || Double.isNaN(to)) {
            return; // species extinct at one end, so leave a gap
        }
        g.setColor(color);
        g.drawLine(x1, getHeight() - (int) (from / SPEED_AXIS_MAX * usableHeight),
                x2, getHeight() - (int) (to / SPEED_AXIS_MAX * usableHeight));
    }

    private static String format(SpeciesStats stats) {
        if (stats.count() == 0) {
            return "-";
        }
        return String.format("%.2f (fastest %d)", stats.averageSpeed(), stats.fastest());
    }
}