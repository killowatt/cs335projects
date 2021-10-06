import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class GameMenu {
    static void setupGameMenu(CalderaWeasel game) {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");

        JMenuItem resetItem = new JMenuItem("Reset");
        resetItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.reset();
            }
        });
        resetItem.setMnemonic(KeyEvent.VK_R);

        JMenu difficultySubmenu = new JMenu("Difficulty");
        difficultySubmenu.setMnemonic(KeyEvent.VK_D);

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
                DifficultyDialog difficultyDialog = new DifficultyDialog();
                JOptionPane.showInternalMessageDialog(null, difficultyDialog, "Difficulty", JOptionPane.PLAIN_MESSAGE);
                game.difficulty = difficultyDialog.getDifficulty();
            }
        });

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
        JMenuItem exitButton = new JMenuItem("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        exitButton.setMnemonic(KeyEvent.VK_E);

        JMenu helpMenu = new JMenu("Help");

        JMenuItem howToItem = new JMenuItem(new AbstractAction("How to play") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showInternalMessageDialog(null, new InstructionsPanel(), "How to Play", JOptionPane.PLAIN_MESSAGE);
            }
        });
        howToItem.setMnemonic(KeyEvent.VK_H);
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

        difficultySubmenu.add(beginnerItem);
        difficultySubmenu.add(intermediateItem);
        difficultySubmenu.add(expertItem);
        difficultySubmenu.add(customItem);

        gameMenu.add(resetItem);
        gameMenu.add(difficultySubmenu);
        gameMenu.add(cheatMenuItem);
        gameMenu.addSeparator();
        gameMenu.add(exitButton);

        helpMenu.add(howToItem);
        helpMenu.add(aboutItem);

        menuBar.add(gameMenu);
        menuBar.add(helpMenu);

        game.setJMenuBar(menuBar);
    }
}
