import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class PolygonPanel extends JPanel {
    PolygonPanel() {
        super();
    }

    boolean fill = false;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        setPreferredSize(new Dimension(128 + 65, 32 + 65));

        if (!fill)
            g.drawRect(128, 32, 64, 64);
        else
            g.fillRect(128, 32, 64, 64);
    }
}

class Polygon extends JFrame {
    Polygon() {
        super("Polygon");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();

        JCheckBox fillCheckBox = new JCheckBox("Fill");

        panel.add(fillCheckBox);

        PolygonPanel polygonPanel = new PolygonPanel();
        panel.add(polygonPanel);

        getContentPane().add(panel);

        fillCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                polygonPanel.fill = !polygonPanel.fill;
                polygonPanel.repaint();
                System.out.println("fart");
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

public class Main {
    public static void main(String[] args) {
        Polygon polygon = new Polygon();
    }
}
