import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

class Polygon extends JFrame {
    Timer timer;
    TimerTask timerTask;

    Polygon() {
        super("Polygon");

        timer = new Timer();

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.BOTH;

        constraints.insets.left = 8;
        constraints.insets.top = 4;
        constraints.ipadx = 64;
        constraints.weighty = 0.0f;
        constraints.weightx = 0.0f;

        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        JCheckBox fillCheckBox = new JCheckBox("Fill Polygon");
        controls.add(fillCheckBox);

        JButton colorButton = new JButton("Select color");
        controls.add(colorButton);

        JCheckBox polygonCheckbox = new JCheckBox("Draw Polygon");
        controls.add(polygonCheckbox);

        JCheckBox splineCheckbox = new JCheckBox("Draw B-Spline");
        controls.add(splineCheckbox);

        JButton resetButton = new JButton("Reset");
        controls.add(resetButton);

        JLabel animationLabel = new JLabel("Animation");
        controls.add(animationLabel);

        JButton startButton = new JButton("Start");
        controls.add(startButton);

        JButton stopButton = new JButton("Stop");
        controls.add(stopButton);

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

        colorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color selectedColor = JColorChooser.showDialog(null, "Polygon Color", Color.red);
                polygonPanel.setCurrentColor(selectedColor);
            }
        });

        polygonCheckbox.setSelected(polygonPanel.showPolygon);
        polygonCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                polygonPanel.showPolygon = polygonCheckbox.isSelected();
                polygonPanel.repaint();
            }
        });

        splineCheckbox.setSelected(polygonPanel.showSpline);
        splineCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                polygonPanel.showSpline = splineCheckbox.isSelected();
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

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timerTask != null)
                    return;

                polygonPanel.animating = true;
                polygonPanel.rotation = 0.0;
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        polygonPanel.rotation += 2.0 * Math.PI / 360.0;

                        if (polygonPanel.rotation >= 2.0 * Math.PI) {
                            polygonPanel.animating = false;
                            timerTask.cancel();
                            timerTask = null;
                        }
                        polygonPanel.repaint();
                    }
                };
                // Schedule for 120 frames a second
                timer.schedule(timerTask, 0, 1000 / 120);
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timerTask == null)
                    return;

                timerTask.cancel();
                timerTask = null;

                polygonPanel.rotation = 0.0;
                polygonPanel.animating = false;
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