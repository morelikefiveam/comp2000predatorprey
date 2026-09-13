import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Simulation {
    public static void main(String[] args) throws Exception {
        List<Creature> sim = new ArrayList<>();
        List<Grass> grassList = new ArrayList<>();

        JFrame simStart = new JFrame("Predator, Prey Simulation");
        simStart.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        simStart.setSize(800, 700);
        simStart.setLocationRelativeTo(null);
        simStart.setResizable(false);

        JPanel panel = new JPanel();
        JButton begin;
        begin = new JButton("Start");
        
        panel.add(begin);
        simStart.add(panel);
        simStart.setVisible(true);
    }
}
