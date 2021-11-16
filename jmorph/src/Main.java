import javax.swing.*;
import java.awt.*;

class JMorph extends JFrame {
    JMorph() {
        super("JMorph");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        JPanel imagesPanel = new JPanel();
        ImageMesh a = new ImageMesh();
        ImageMesh b = new ImageMesh();

        imagesPanel.add(a);
        imagesPanel.add(b);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridy = 0;

        JPanel controls = new JPanel();
        controls.add(new JButton("Render"));
        controls.add(new JSlider());

        panel.add(imagesPanel, constraints);

        constraints.gridy = 1;
        panel.add(controls, constraints);

        getContentPane().add(panel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

public class Main {
    public static void main(String[] args) {
        JMorph jMorph = new JMorph();
    }
}
