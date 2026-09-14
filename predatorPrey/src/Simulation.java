import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Simulation{

    public static void main(String[] args) throws Exception {
        List<Creature> sim = new ArrayList<>();
        List<Grass> grassList = new ArrayList<>();

        Simulation mainSim = new Simulation();
        mainSim.frameInitialise();

        

    }

    private static final int RENDER_INTERVAL_MS = 33;
    private static final int TICKS_PER_LOGIC_UPDATE = 9;
    private  int frameCounter = 0;

    List<Creature> sim;
    List<Grass> grassList;

    //Necessary JFrame stuff to make things visiable
    private JFrame simStart;
    private JPanel panel;
    private JButton begin;
    private JButton savePred;
    private JButton savePrey;
    private JFrame runningSim;
    private JLabel pred;
    private JLabel prey;
    private JTextField predAmount;
    private JTextField preyAmount;
    private int entPredAmount;
    private int entPreyAmount;
    private JLabel warning;

    public Simulation(){ //Our JFrame simulation constructor
        frameInitialise();
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
                simStart.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                runningSim = new JFrame("Predator, Prey Simulation. RUNNING");

                simStart.dispose();
                simStart.setVisible(false);

                runningSim.setSize(800, 700);

                runningSim.setVisible(true);

                runningSim.getContentPane().setBackground(new java.awt.Color(18, 84, 13));
                System.out.println("Button"); 
            }
        });
        savePred.addActionListener(new ActionListener() { // Attempts to get a button to save a value related to amout of start pred
            @Override 
            public void actionPerformed(ActionEvent e){
                int input = Integer.parseInt(predAmount.getText());
                for(int i = 0; i < input; i++){
                    sim.add(new Predator(10, 10, 10, 10, sim) { });
                    System.out.println("predator added: " + i + " times"); //Doesn't work nor my brain
                }
            }
        });
        savePrey.addActionListener(new ActionListener() { // Attempts to get a button to save a value related to amout of start prey
            @Override
            public void actionPerformed(ActionEvent e){
                int input = Integer.parseInt(preyAmount.getText());
                for(int i = 0; i < input; i++){
                    sim.add(new Prey(10, 10, 10, 10, sim, grassList));
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

    public void runSimulationTick(){ // implement later

    }

// ***COME BACK TO LATER*** ps: currently a mess

   /*  public void timeSetup(){

        Timer renderTimer = new Timer();
        TimerTask repainting = new TimerTask(){
            @Override
            public void run(){
                for(Creature c : sim) {
                    c.movement();
                }

                if(frameCounter >= TICKS_PER_LOGIC_UPDATE){
                    runSimulationTick();
                    frameCounter = 0;
                }
            }
            //frameCounter++; **Come back to later**

        if(frameCounter >= TICKS_PER_LOGIC_UPDATE){
            runSimulationTick();
            frameCounter = 0;
        }

        repaint();  
        };

        frameCounter++;
        


        for(Creature c : sim) {
            c.movement();
        }

        if(frameCounter >= TICKS_PER_LOGIC_UPDATE){
            runSimulationTick();
            frameCounter = 0;
        }

        repaint();
    }

*/
}
