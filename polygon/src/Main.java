import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class PolygonPanel extends JPanel {
    ArrayList<Rectangle> handles;
    boolean fill = false;

    Rectangle currentHandle = null;

    PolygonPanel() {
        super();

        handles = new ArrayList<>();

        setBackground(Color.black);

        setPreferredSize(new Dimension(256, 256));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                final int x = e.getX();
                final int y = e.getY();

                for (Rectangle rectangle : handles) {
                    System.out.println(x + " " + y);
                    if (rectangle.contains(x, y)) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            currentHandle = rectangle;
                        }
                        else if (SwingUtilities.isRightMouseButton(e)) {
                            handles.remove(rectangle);
                            repaint();
                        }

                        return;
                    }
                }

                if (SwingUtilities.isLeftMouseButton(e)) {
                    handles.add(new Rectangle(x, y, 12, 12));
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                currentHandle = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentHandle == null)
                    return;

                currentHandle.x = e.getX() - 6;
                currentHandle.y = e.getY() - 6;

                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (handles.size() >= 3) {
            int[] x = new int[handles.size()];
            int[] y = new int[handles.size()];

            for (int i = 0; i < handles.size(); i++) {
                Rectangle rectangle = handles.get(i);

                x[i] = rectangle.x + 6;
                y[i] = rectangle.y + 6;
            }

            g.setColor(Color.red);
            if (!fill)
                g.drawPolygon(x, y, handles.size());
            else
                g.fillPolygon(x, y, handles.size());
        }

        // We want handles to appear over the polygon
        g.setColor(Color.cyan);
        for (Rectangle rectangle : handles) {
            g.drawRect(rectangle.x, rectangle.y, 12, 12);
        }
    }
}

class Polygon extends JFrame {
    Polygon() {
        super("Polygon");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.BOTH;

        constraints.ipadx = 64;
        constraints.weighty = 0.0f;
        constraints.weightx = 0.0f;

        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        JCheckBox fillCheckBox = new JCheckBox("Fill");
        controls.add(fillCheckBox);

        JButton resetButton = new JButton("Reset");
        controls.add(resetButton);

        panel.add(controls, constraints);

        constraints.weighty = 1.0f;
        constraints.weightx = 1.0f;

        PolygonPanel polygonPanel = new PolygonPanel();
        panel.add(polygonPanel, constraints);

        getContentPane().add(panel);

        fillCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                polygonPanel.fill = !polygonPanel.fill;
                polygonPanel.repaint();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                polygonPanel.handles.clear();
                polygonPanel.repaint();
            }
        });

        polygonPanel.repaint();

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
