import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Simulation {

    public static void main(String[] args) throws Exception {
        List<Creature> sim = new ArrayList<>();
        List<Grass> grassList = new ArrayList<>();

        Simulation mainSim = new Simulation();
        mainSim.frameInitialise();

    }

        
    

    public JFrame simStart;
    public JPanel panel;
    public JButton begin;
    public JFrame runningSim;

    public Simulation(){
        frameInitialise();
    }

    public void frameInitialise(){
        
        simStart = new JFrame("Predator, Prey Simulation");
        simStart.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        simStart.setSize(800, 700);
        simStart.setLocationRelativeTo(null);
        simStart.setResizable(false);

        panel = new JPanel();
        begin = new JButton("Start");
        
        begin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                simStart.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                runningSim = new JFrame("Predator, Prey Simulation. RUNNING");

                simStart.dispose();
                simStart.setVisible(false);

                runningSim.setSize(800, 700);

                runningSim.setVisible(true);
                System.out.println("Button"); 
            }
        });

        panel.add(begin);
        simStart.add(panel);
        simStart.setVisible(true);
    }

    public void repaintSim(){
        private static final int RENDER_INTERVAL_MS = 33;
        private static final int TICKS_PER_LOGIC_UPDATE = 9;

        Timer renderTimer = new Timer(RENDER_INTERVAL_MS, e-> {
            frameCounter++;

            for(Creature c : sim) {
                c.movement();
            }

            if(frameCounter >= TICKS_PER_LOGIC_UPDATE){
                runSimulationTick();
                frameCounter = 0;
            }

            repaint();
        })
    }


}
