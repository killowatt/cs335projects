import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

class GaussianBlur extends JFrame {
    GaussianBlur() {
        super("Gaussian Blur");

        setDefaultCloseOperation(EXIT_ON_CLOSE);


        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        JMenuItem openFileButton = new JMenuItem("Open");
        JSeparator fileSeparator = new JSeparator();
        JMenuItem quitButton = new JMenuItem("Quit");

        fileMenu.add(openFileButton);
        fileMenu.add(fileSeparator);
        fileMenu.add(quitButton);

        menuBar.add(fileMenu);

        setJMenuBar(menuBar);



        JPanel controlPanel = new JPanel();

        JSlider blurSlider = new JSlider();

        JButton resetButton = new JButton("Reset");

        controlPanel.add(blurSlider);
        controlPanel.add(resetButton);

        blurSlider.setMajorTickSpacing(25);
        blurSlider.setPaintTicks(true);
        blurSlider.setSnapToTicks(true);

        getContentPane().add(controlPanel);

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

public class Main {
    public static void main(String[] args) {
        GaussianBlur gaussianBlur = new GaussianBlur();
    }
}
