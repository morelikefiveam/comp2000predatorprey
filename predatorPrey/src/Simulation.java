import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Simulation{

    private static final int RENDER_INTERVAL_MS = 33;
    private static final int TICKS_PER_LOGIC_UPDATE = 9;
    private static final int STARTING_GRASS = 150;
    private static final int GRASS_GROWTH_TIME = 100; // logic ticks for eaten grass to regrow
    private  int frameCounter = 0;
    private final Random random = new Random();

    List<Creature> sim;
    List<Grass> grassList;

    //Necessary JFrame stuff to make things visiable
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

    public static void main(String[] args) throws Exception {
        List<Creature> sim = new ArrayList<>();

        Simulation mainSim = new Simulation(sim);
        mainSim.frameInitialise();
    }



    public Simulation(List<Creature> sim){ //Our JFrame simulation constructor
        this.sim = sim;
        this.grassList = new ArrayList<>(); 
    }

    public void frameInitialise(){ // How we setup the JFramse and make it work
        
        simStart = new JFrame("Predator, Prey Simulation");
        simStart.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        simStart.setSize(800, 700);
        simStart.setLocationRelativeTo(null);
        simStart.setResizable(false);

        panel = new JPanel();
        warning = new JLabel("DO NOT SAVE NUMBERS MULTIPLE TIMES");
        begin = new JButton("Start");
        pred = new JLabel("Starting predator amount");
        prey = new JLabel("Starting prey amount");
        predAmount = new JTextField(10);
        preyAmount = new JTextField(10);
        savePred = new JButton("Save");
        savePrey = new JButton("Save");
        
        begin.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e){ // Close start frame open active/running frame
                spawnGrass();

                simStart.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                runningSim = new JFrame("Predator, Prey Simulation. RUNNING");

                simStart.dispose();
                simStart.setVisible(false);

                
                simPanel = new SimPanel();
                simPanel.setPreferredSize(new Dimension(Creature.WORLD_WIDTH, Creature.WORLD_HEIGHT));
                simPanel.setBackground(new java.awt.Color(18, 84, 13));
                runningSim.add(simPanel, BorderLayout.CENTER);

                runningSim.setResizable(false);
                runningSim.pack();
                runningSim.setLocationRelativeTo(null);
                runningSim.setVisible(true);

                System.out.println("Button"); 

                startGameLoop();
            }
        });
        savePred.addActionListener(new ActionListener() { // Attempts to get a button to save a value related to amout of start pred
            public void actionPerformed(ActionEvent e){
                String strInput = predAmount.getText();
                int input;
                try {
                    input = Integer.parseInt(strInput);
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid predator amount: " + strInput);
                    return;
                }
                for(int i = 0; i < input; i++){
                    int x = random.nextInt(Creature.WORLD_WIDTH - 20);
                    int y = random.nextInt(Creature.WORLD_HEIGHT - 20);
                    sim.add(new Predator(5, 0, x, y, sim) { });
                    System.out.println("predator added: " + i + " times"); //Doesn't work nor my brain
                }
            }
        });
        savePrey.addActionListener(new ActionListener() { // Attempts to get a button to save a value related to amout of start prey
            public void actionPerformed(ActionEvent e){
                String strInput = preyAmount.getText();
                int input;
                try {
                    input = Integer.parseInt(strInput);
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid prey amount: " + strInput);
                    return;
                }
                for(int i = 0; i < input; i++){
                    int x = random.nextInt(Creature.WORLD_WIDTH - 20);
                    int y = random.nextInt(Creature.WORLD_HEIGHT - 20);
                    sim.add(new Prey(5, 10, x, y, sim, grassList));
                    System.out.println("Prey added: " + i + " Times");
                }
            }
        });


        //Building the first JFrame
        panel.add(begin);
        panel.add(warning);
        panel.add(pred);
        panel.add(prey);
        panel.add(predAmount);
        panel.add(preyAmount);
        panel.add(savePred);
        panel.add(savePrey);
        simStart.add(panel, BorderLayout.CENTER);
        simStart.add(panel);
        simStart.setVisible(true);
    }

    private void spawnGrass(){
        for (int i = 0; i < STARTING_GRASS; i++){
            int x = random.nextInt(Creature.WORLD_WIDTH - 10);
            int y = random.nextInt(Creature.WORLD_HEIGHT - 10);
            grassList.add(new Grass(x, y, GRASS_GROWTH_TIME));
        }
    }


    private void startGameLoop(){
        gameTimer = new Timer(RENDER_INTERVAL_MS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                List<Creature> snapshot = new ArrayList<>(sim);
                for (Creature c : snapshot) {
                    c.movement();
                    c.eat();
                }

                frameCounter++;
                if (frameCounter >= TICKS_PER_LOGIC_UPDATE) {
                    runSimulationTick();
                    frameCounter = 0;
                }

                repaint();
            }
        });
        gameTimer.start();
    }

    public void runSimulationTick(){ // starvation + reproduction + grass regrowth
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
    }

    public void repaint(){
        if (simPanel != null) {
            simPanel.repaint();
        }
    }

    // Minimal rendering: green dots for grass, blue circles for prey, red for predators.
    private class SimPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(new Color(80, 200, 80));
            for (Grass grass : grassList) {
                if (grass.isEdible()) {
                    g.fillOval(grass.getX(), grass.getY(), 8, 8);
                }
            }

            for (Creature c : new ArrayList<>(sim)) {
                g.setColor(c instanceof Predator ? Color.RED : Color.BLUE);
                g.fillOval(c.getX(), c.getY(), 20, 20);
            }
        }
    }
}