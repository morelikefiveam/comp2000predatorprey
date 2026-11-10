import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;

public class PopulationGraph extends JPanel {
    private static final int MAX_SAMPLES = 150;
    private static final Font STATS_FONT = new Font("SansSerif", Font.BOLD, 14);

    private final List<Creature> sim;
    private final Deque<int[]> samples = new ArrayDeque<>(); // {prey, predators}
    private Set<Creature> previous = new HashSet<>();

    private int currentPrey;
    private int currentPredators;
    private int peakPrey;
    private int peakPredators;
    private int peakTotal;
    private int preyDeaths;
    private int predatorDeaths;

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

        currentPrey = (int) snapshot.stream().filter(c -> c instanceof Prey).count();
        currentPredators = (int) snapshot.stream().filter(c -> c instanceof Predator).count();

        peakPrey = Math.max(peakPrey, currentPrey);
        peakPredators = Math.max(peakPredators, currentPredators);
        peakTotal = Math.max(peakTotal, currentPrey + currentPredators);

        samples.addLast(new int[] { currentPrey, currentPredators });
        if (samples.size() > MAX_SAMPLES) {
            samples.removeFirst();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int max = Math.max(1, samples.stream().flatMapToInt(Arrays::stream).max().orElse(1));
        double step = getWidth() / (double) MAX_SAMPLES;
        int usableHeight = getHeight() - 4;

        int[] last = null;
        int i = 0;
        for (int[] s : samples) {
            if (last != null) {
                int x1 = (int) ((i - 1) * step);
                int x2 = (int) (i * step);

                g.setColor(Color.BLUE);
                g.drawLine(x1, getHeight() - (int) ((double) last[0] / max * usableHeight),
                        x2, getHeight() - (int) ((double) s[0] / max * usableHeight));
                g.setColor(Color.RED);
                g.drawLine(x1, getHeight() - (int) ((double) last[1] / max * usableHeight),
                        x2, getHeight() - (int) ((double) s[1] / max * usableHeight));
            }
            last = s;
            i++;
        }

        g.setColor(Color.WHITE);
        g.setFont(STATS_FONT);
        g.drawString("Peak - total: " + peakTotal + "  prey: " + peakPrey
                + "  predators: " + peakPredators, 5, 18);
        g.drawString("Now - prey: " + currentPrey + "  predators: " + currentPredators, 5, 38);
        g.drawString("Deaths - prey: " + preyDeaths + "  predators: " + predatorDeaths, 5, 58);
    }
}