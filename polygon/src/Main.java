import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Polygon JFrame, simply controls and contains our polygon panel
class Polygon extends JFrame {
    Polygon() {
        // Set the name of this JFrame to "Polygon" using parent constructor
        super("Polygon");

        // Close the application whenever we exit this JFrame
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Set up our JPanel for our polygon controls
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        // Create and add our color selector
        JButton colorButton = new JButton("Select color");
        controls.add(colorButton);

        // Create and add our show polygon checkbox
        JCheckBox polygonCheckBox = new JCheckBox("Show Polygon");
        controls.add(polygonCheckBox);

        // Create and add our fill polygon checkbox
        JCheckBox fillCheckBox = new JCheckBox("Fill Polygon");
        controls.add(fillCheckBox);

        // Create and add our show spline checkbox
        JCheckBox splineCheckBox = new JCheckBox("Show B-Spline");
        controls.add(splineCheckBox);

        // Create and add our show handles checkbox
        JCheckBox handlesCheckBox = new JCheckBox("Show Handles");
        controls.add(handlesCheckBox);

        // Create and add our show centroid checkbox
        JCheckBox centroidCheckBox = new JCheckBox("Show Centroid");
        controls.add(centroidCheckBox);

        // Create and add our reset polygon button
        JButton resetButton = new JButton("Reset");
        controls.add(resetButton);

        // Create and add our animation section label
        JLabel animationLabel = new JLabel("Animation");
        controls.add(animationLabel);

        // Create and add our animation start button
        JButton startButton = new JButton("Start");
        controls.add(startButton);

        // Create and add our animation stop button
        JButton stopButton = new JButton("Stop");
        controls.add(stopButton);

        // Polygon Panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        // Set up our grid bag constraints so our controls fit on the left side of the window
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets.left = 8;
        constraints.insets.top = 4;
        constraints.ipadx = 12;
        constraints.weighty = 0.0f;
        constraints.weightx = 0.0f;

        // Add our controls to the JFrame with our desired constraints
        panel.add(controls, constraints);

        // Now set our constraints so that our polygon panel will take up the remainder of the window
        constraints.weighty = 1.0f;
        constraints.weightx = 1.0f;

        // Create our polygon panel and add it to the panel
        PolygonPanel polygonPanel = new PolygonPanel();
        panel.add(polygonPanel, constraints);

        // Now add our main panel to the JFrame
        getContentPane().add(panel);

        // Set up our color button to set the current polygon/b-spline color using a color picker
        colorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color selectedColor = JColorChooser.showDialog(null, "Polygon Color", Color.red);
                polygonPanel.setCurrentColor(selectedColor);
            }
        });

        // Set up our polygon checkbox to set the corresponding polygon panel setting
        polygonCheckBox.setSelected(polygonPanel.isShowingPolygon());
        polygonCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                polygonPanel.setShowPolygon(polygonCheckBox.isSelected());
            }
        });

        // Set up our fill checkbox to set the corresponding polygon panel setting
        fillCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                polygonPanel.setPolygonFill(!polygonPanel.isPolygonFilled());
            }
        });

        // Set up our spline checkbox to set the corresponding polygon panel setting
        splineCheckBox.setSelected(polygonPanel.isShowingSpline());
        splineCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                polygonPanel.setShowSpline(splineCheckBox.isSelected());
            }
        });

        // Set up our handles checkbox to set the corresponding polygon panel setting
        handlesCheckBox.setSelected(polygonPanel.isShowingHandles());
        handlesCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                polygonPanel.setShowHandles(handlesCheckBox.isSelected());
            }
        });

        // Set up our centroid checkbox to set the corresponding polygon panel setting
        centroidCheckBox.setSelected(polygonPanel.isShowingCentroid());
        centroidCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                polygonPanel.setShowCentroid(centroidCheckBox.isSelected());
            }
        });

        // Set up our reset button to call the corresponding polygon panel method
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                polygonPanel.clearPolygon();
            }
        });

        // Set up our start animation button to call the corresponding polygon panel method
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                polygonPanel.startAnimation();
            }
        });

        // Set up our stop animation button to call the corresponding polygon panel method
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                polygonPanel.stopAnimation();
            }
        });

        // Finally, pack the JFrame layout, center it on screen and make it visible
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

public class Main {
    public static void main(String[] args) {
        // Create our Polygon JFrame
        Polygon polygon = new Polygon();
    }
}
