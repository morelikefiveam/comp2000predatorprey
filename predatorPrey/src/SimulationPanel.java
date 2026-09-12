import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
public class SimulationPanel extends JPanel{
    private List<Entity> entities = new ArrayList<>();

    private static final int ICON_SIZE = 70;
    private static final int GRASS_SIZE = 30;

    private final Image predatorIcon = loadAnimatedIcon("predatorPrey/resources/tiger.gif");
    private final Image preyIcon = loadAnimatedIcon("predatorPrey/resources/deer.gif");
    private final Image grassIcon = loadStaticIcon("predatorPrey/resources/grass.png", GRASS_SIZE);

    private Image loadAnimatedIcon (String path){
        Image result = null;
        try {
            File file = new File(path);
            ImageIO.read(file);
            ImageIcon icon = new ImageIcon(path);
            result = icon.getImage();
        } catch (IOException e) {
            System.err.println("Failed to load animated icon ' " + path + "': " + e.getMessage());
        } finally{
            System.out.println("Finished icon load attempt for: " + path);
        }
        return result;
    }

    private Image loadStaticIcon(String path, int size){
        Image result = null;
        try {
            Image raw = ImageIO.read(new File(path));
            result = raw.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            System.err.println("Failed to load static icon '" + path + "': "+ e.getMessage());
        } finally{
            System.out.println("Finished icon load attempt for: "+ path);
        }
        return result;
    }

    public void setEntities(List<Entity> entities){
        this.entities = entities;
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        for(Entity entity: entities){
            Image icon;
            int size;
            Color fallbackColor;
            boolean facingRight = true;
            if(entity instanceof Predator p){
                icon = predatorIcon;
                size = ICON_SIZE;
                fallbackColor=Color.RED;
                facingRight = p.isFacingRight();
            } else if(entity instanceof Prey p){
                icon = preyIcon;
                size = ICON_SIZE;
                fallbackColor = Color.BLUE;
                facingRight = p.isFacingRight();
            } else if (entity instanceof Grass){
                icon = grassIcon;
                size = GRASS_SIZE;
                fallbackColor = Color.GREEN;
            } else{
                icon = null;
                size = ICON_SIZE;
                fallbackColor=Color.BLACK;
            }

            if(icon!=null){
                drawFacingImage(g2d, icon, entity.getX(), entity.getY(), size, facingRight); 
            } else{
                g2d.setColor(fallbackColor);
                g2d.fillOval(entity.getX(), entity.getY(), size, size);
            }
        }
    }

    private void drawFacingImage(Graphics2D g2d, Image icon, int x, int y, int size, boolean facingRight){
        if(facingRight){
            g2d.drawImage(icon, x, y, size, size, this);
        } else{
            g2d.drawImage(icon, x+size, y, -size, size, this);
        }
    }
}