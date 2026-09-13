import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.EventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Simulation {

    public static void main(String[] args) throws Exception {
        List<Creature> sim = new ArrayList<>();
        List<Grass> grassList = new ArrayList<>();
    }

    public JFrame simStart;
    public JPanel panel;
    public JButton begin;

    public Simulation(){
        frameInitialise();
    }

    private void frameInitialise(){
        
        simStart = new JFrame("Predator, Prey Simulation");
        simStart.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        simStart.setSize(800, 700);
        simStart.setLocationRelativeTo(null);
        simStart.setResizable(false);

        panel = new JPanel();
        begin = new JButton("Start");
        
        begin.addActionListener(new ActionListener() {
            @Override
            public void startClicked(ActionEvent e){
                System.out.println("Button"); 
            }
        });

        panel.add(begin);
        simStart.add(panel);
        simStart.setVisible(true);
    }
}
