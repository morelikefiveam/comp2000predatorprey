import java.awt.BorderLayout;
import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Predator-Prey Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        SimulationPanel simPanel = new SimulationPanel();
        Simulation sim = new Simulation(simPanel);
        try {
            sim.addEntity(new Predator(3, 100, 50, 50));
            sim.addEntity(new Prey(3, 100, false, 200, 200));
            sim.addEntity(new Predator(3, 100, 350, 150));
            sim.addEntity(new Prey(3, 100, false, 250, 250));
            sim.addEntity(new Grass(100, 100, 100));
            sim.addEntity(new Grass(300, 400, 100));
            sim.addEntity(new Grass(500, 150, 100));
        } catch (InvalidCreatureStateException e) {
            System.err.println("Failed to create initial entities: " + e.getMessage());
            JOptionPane.showMessageDialog(frame, "Simulation could not start: "+ e.getMessage(),
        "Initialization Error", JOptionPane.ERROR_MESSAGE);
        } finally{
            System.err.println("Entity setup attempt complete. Current entity count: " + sim.getEntities().size());
        }
       
        simPanel.setEntities(sim.getEntities());
        Timer timer = new Timer(33, e -> {
            sim.tick();
            simPanel.repaint();
        });


        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> timer.start());
        JButton pauseButton = new JButton("Pause");
        pauseButton.addActionListener(e -> timer.stop());
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            timer.stop();
            sim.reset();
            simPanel.repaint();
        });


        JPanel controlPanel = new JPanel();
        controlPanel.add(startButton);
        controlPanel.add(pauseButton);
        controlPanel.add(resetButton);


        frame.setLayout(new BorderLayout());
        frame.add(simPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);


        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}