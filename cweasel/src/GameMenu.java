import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

// Game menu helper class that sets up our menu bar that interacts with our game
public class GameMenu {
    static void setupGameMenu(CalderaWeasel game) {
        // Create our menu bar and the "Game" menu
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");

        // Set up our reset button that simply calls our game's reset method
        JMenuItem resetItem = new JMenuItem("Reset");
        resetItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.reset();
            }
        });
        resetItem.setMnemonic(KeyEvent.VK_R);

        // Create our difficulty submenu
        JMenu difficultySubmenu = new JMenu("Difficulty");
        difficultySubmenu.setMnemonic(KeyEvent.VK_D);

        // Create our beginner, intermediate, expert and custom difficulty buttons
        // All except the custom option simply set the game difficulty to the corresponding difficulty object
        JMenuItem beginnerItem = new JMenuItem(new AbstractAction("Beginner") {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.difficulty = GameDifficulty.Beginner;
            }
        });
        JMenuItem intermediateItem = new JMenuItem(new AbstractAction("Intermediate") {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.difficulty = GameDifficulty.Intermediate;
            }
        });
        JMenuItem expertItem = new JMenuItem(new AbstractAction("Expert") {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.difficulty = GameDifficulty.Expert;
            }
        });
        JMenuItem customItem = new JMenuItem(new AbstractAction("Custom") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create an instance of our difficulty panel and present it to the player
                DifficultyPanel difficultyPanel = new DifficultyPanel();
                JOptionPane.showInternalMessageDialog(null, difficultyPanel, "Difficulty", JOptionPane.PLAIN_MESSAGE);

                // Once they've clicked OK, take the difficulty object from the panel
                game.difficulty = difficultyPanel.getDifficulty();
            }
        });

        // Set up our cheat mode check box, which when toggled sets the cheat mode and updates all buttons on the board
        JCheckBoxMenuItem cheatMenuItem = new JCheckBoxMenuItem("Cheat Mode");
        cheatMenuItem.setMnemonic(KeyEvent.VK_C);
        cheatMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameButton.cheatMode = cheatMenuItem.getState();
                for (GameButton button : game.gameButtons) {
                    button.checkCheats();
                }
            }
        });
        // Set up our exit button, simply calls System.exit
        JMenuItem exitButton = new JMenuItem("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        exitButton.setMnemonic(KeyEvent.VK_E);

        // Create our top-level help menu
        JMenu helpMenu = new JMenu("Help");

        // Set up our instructions button, creates an instance of our instructions panel and presents it to the player
        JMenuItem howToItem = new JMenuItem(new AbstractAction("How to play") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showInternalMessageDialog(null, new InstructionsPanel(), "How to Play", JOptionPane.PLAIN_MESSAGE);
            }
        });
        howToItem.setMnemonic(KeyEvent.VK_H);
        // Set up our about button, simply presents info about the software in a dialog box
        JMenuItem aboutItem = new JMenuItem(new AbstractAction("About") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] aboutDialog = {
                        "Caldera Weasel project for CS335",
                        "Created by William Yates"
                };
                JOptionPane.showInternalMessageDialog(null, aboutDialog, "About", JOptionPane.PLAIN_MESSAGE);
            }
        });
        aboutItem.setMnemonic(KeyEvent.VK_A);

        // Add all of our difficulty submenu items
        difficultySubmenu.add(beginnerItem);
        difficultySubmenu.add(intermediateItem);
        difficultySubmenu.add(expertItem);
        difficultySubmenu.add(customItem);

        // Add all of our game menu items
        gameMenu.add(resetItem);
        gameMenu.add(difficultySubmenu);
        gameMenu.add(cheatMenuItem);
        gameMenu.addSeparator();
        gameMenu.add(exitButton);

        // Add all of our help menu items
        helpMenu.add(howToItem);
        helpMenu.add(aboutItem);

        // Add our game and help menus to the top level menu bar
        menuBar.add(gameMenu);
        menuBar.add(helpMenu);

        // Set the menu bar for the game's JFrame
        game.setJMenuBar(menuBar);
    }
}
