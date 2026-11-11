import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class Simulation {

    private static final int RENDER_INTERVAL_MS = 33;
    private static final int TICKS_PER_LOGIC_UPDATE = 9;
    private static final int GRASS_SIZE = 20; // Also affects grass amount
    private static final int GRASS_GROWTH_TIME = 1000; // Logic ticks for eaten grass to regrow
    private static final int MAX_STARTING_POPULATION = 500;
    private int frameCounter = 0;
    private final Random random = new Random();

    List<Creature> sim;
    List<Grass> grassList;

    // Necessary JFrame stuff to make things visiable
    private JFrame simStart;
    private JPanel panel;
    private JButton begin;
    private JButton savePred;
    private JButton savePrey;
    private JFrame runningSim;
    private SimPanel simPanel;
    private Timer gameTimer;
    private JLabel pred;
    private JLabel prey;
    private JTextField predAmount;
    private JTextField preyAmount;
    private int entPredAmount;
    private int entPreyAmount;
    private JLabel warning;
    private JPanel row1;
    private JPanel row2;
    private JPanel row3;
    private JPanel row4;
    private Dimension rowSize;
    private PopulationGraph graph;

    public static void main(String[] args) throws Exception {
        List<Creature> sim = new ArrayList<>();

        Simulation mainSim = new Simulation(sim);
        mainSim.frameInitialise();
    }

    public Simulation(List<Creature> sim) { // Our JFrame simulation constructor
        this.sim = sim;
        this.grassList = new ArrayList<>();
    }

    public void frameInitialise() { // How we setup the JFramse and make it work

        simStart = new JFrame("Predator, Prey Simulation");
        simStart.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        simStart.setSize(800, 700);
        simStart.setLocationRelativeTo(null);
        simStart.setResizable(false);

        panel = new JPanel();
        warning = new JLabel("Enter each starting amount, then press Save");
        begin = new JButton("Start");
        pred = new JLabel("Starting predator amount");
        prey = new JLabel("Starting prey amount");
        predAmount = new JTextField(10);
        preyAmount = new JTextField(10);
        savePred = new JButton("Save");
        savePrey = new JButton("Save");

        row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        row3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        row4 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        rowSize = new Dimension(800, 80);

        begin.addActionListener(e -> { // Close start frame open active/running frame
            spawnGrass();

            simStart.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            runningSim = new JFrame("Predator, Prey Simulation. RUNNING");
            runningSim.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            simStart.dispose();
            simStart.setVisible(false);

            simPanel = new SimPanel();
            simPanel.setPreferredSize(new Dimension(Creature.WORLD_WIDTH, Creature.WORLD_HEIGHT));
            simPanel.setBackground(new java.awt.Color(18, 84, 13));
            runningSim.add(simPanel, BorderLayout.CENTER);

            graph = new PopulationGraph(sim);
            graph.setPreferredSize(new Dimension(Creature.WORLD_WIDTH, 120));
            runningSim.add(graph, BorderLayout.SOUTH);

            runningSim.setResizable(false);
            runningSim.pack();
            runningSim.setLocationRelativeTo(null);
            runningSim.setVisible(true);

            startGameLoop();
        });

        savePred.addActionListener(e -> { // Saves the starting predator amount
            try {
                int input = parseAmount(predAmount.getText(), "predator");
                for (int i = 0; i < input; i++) {
                    int x = random.nextInt(Creature.WORLD_WIDTH - 20);
                    int y = random.nextInt(Creature.WORLD_HEIGHT - 20);
                    sim.add(new Predator(5, 0, x, y, sim));
                }
                warning.setText("Saved " + input + " predators");
                savePred.setEnabled(false);
                predAmount.setEditable(false);
            } catch (InvalidPopulationException ex) {
                warning.setText(ex.getMessage());
            }
        });

        savePrey.addActionListener(e -> { // Saves the starting prey amount
            try {
                int input = parseAmount(preyAmount.getText(), "prey");
                for (int i = 0; i < input; i++) {
                    int x = random.nextInt(Creature.WORLD_WIDTH - 20);
                    int y = random.nextInt(Creature.WORLD_HEIGHT - 20);
                    sim.add(new Prey(5, 10, x, y, sim, grassList));
                }
                warning.setText("Saved " + input + " prey");
                savePrey.setEnabled(false);
                preyAmount.setEditable(false);
            } catch (InvalidPopulationException ex) {
                warning.setText(ex.getMessage());
            }
        });

        // Building the first JFrame

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(80, 20, 20, 20));
        row1.add(begin);
        row1.setMaximumSize(rowSize);

        row2.add(warning);
        row2.setMaximumSize(rowSize);

        row3.add(pred);
        row3.add(predAmount);
        row3.add(savePred);
        row3.setMaximumSize(rowSize);

        row4.add(prey);
        row4.add(preyAmount);
        row4.add(savePrey);
        row4.setMaximumSize(rowSize);

        panel.add(row1);
        panel.add(row2);
        panel.add(row3);
        panel.add(row4);

        simStart.add(panel, BorderLayout.CENTER);
        simStart.add(panel);
        simStart.setVisible(true);
    }

    private int parseAmount(String text, String label) throws InvalidPopulationException {
        int amount;
        try {
            amount = Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidPopulationException("Starting " + label + " amount must be a whole number");
        }
        if (amount < 0 || amount > MAX_STARTING_POPULATION) {
            throw new InvalidPopulationException(
                    "Starting " + label + " amount must be between 0 and " + MAX_STARTING_POPULATION);
        }
        return amount;
    }

    private void spawnGrass() {
        for (int i = 0; i < Creature.WORLD_WIDTH; i += GRASS_SIZE) {
            for (int j = 0; j < Creature.WORLD_HEIGHT; j += GRASS_SIZE) {
                int randomGrowth = random.nextInt(GRASS_GROWTH_TIME);
                int randomWeight = random.nextInt(20);
                boolean isEaten = true;
                if (randomWeight < 1) {
                    isEaten = false; // Randomly decide if the grass is eaten or not
                }
                grassList.add(new Grass(i, j, randomGrowth, isEaten));
            }
        }
    }

    private void startGameLoop() {
        gameTimer = new Timer(RENDER_INTERVAL_MS, e -> {
            List<Creature> snapshot = new ArrayList<>(sim);
            snapshot.parallelStream().forEach(Creature::chooseStrategy);

            for (Creature c : snapshot) {
                if (!c.isAlive()){
                    continue; 
                }
                c.applyStrategy();
                c.eat();
            }

            frameCounter++;
            if (frameCounter >= TICKS_PER_LOGIC_UPDATE) {
                runSimulationTick();
                frameCounter = 0;
            }

            repaint();
        });
        gameTimer.start();
    }

    public void runSimulationTick() { // starvation + reproduction + grass regrowth
        List<Creature> snapshot = new ArrayList<>(sim);
        List<Creature> toRemove = new ArrayList<>();

        for (Creature c : snapshot) {
            if (c.starve()) {
                toRemove.add(c);
            }
        }
        sim.removeAll(toRemove);

        List<Creature> reproSnapshot = new ArrayList<>(sim);
        for (Creature c : reproSnapshot) {
            c.reproduce();
        }

        for (Grass g : grassList) {
            g.tickGrowth();
        }

        if (graph != null) {
            graph.sample();
        }
    }

    public void repaint() {
        if (simPanel != null) {
            simPanel.repaint();
        }
        if (graph != null) {
            graph.repaint();
        }
    }

    // Minimal rendering: green dots for grass, blue circles for prey, red for
    // predators.
    private class SimPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(new Color(80, 200, 80));
            for (Grass grass : grassList) {
                if (grass.isEdible()) {
                    g.fillRect(grass.getX(), grass.getY(), GRASS_SIZE, GRASS_SIZE);
                }
            }

            for (Creature c : new ArrayList<>(sim)) {
                g.setColor(c instanceof Predator ? Color.RED : Color.BLUE);
                g.fillOval(c.getX(), c.getY(), 20, 20);
            }
        }
    }
}