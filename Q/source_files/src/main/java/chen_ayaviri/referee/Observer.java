package chen_ayaviri.referee;

import chen_ayaviri.common.GameState;
import chen_ayaviri.visuals.GameStateRenderer;

import com.google.gson.JsonObject;
import java.awt.image.BufferedImage;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.Writer;
import java.io.IOException;

/**
 * Saves and displays states received. Display next/previous states with right arrow and left arrow.
 * Down arrow to open save file dialogue. Saves files to a hard-coded filepath. Saving states and selected states
 * fails if Tmp already exists as a file.
 */
public class Observer implements IObserver {
    private final List<GameState> states; // list of received states in the order they were sent
    // "Tmp/{states.size() + 1}.png" is the site of saving a new state
    private int currentStateIndex; // invariant - in range [0, states.size())

    public Observer() {
        this.states = new ArrayList<>();
        this.currentStateIndex = -1; //TODO find better current state representation
    }

    @Override
    public void receive(GameState state) {
        this.states.add(state);

        if (this.states.size() == 1) {
            this.currentStateIndex = 0;
            this.createTempDirectory();
            this.startGUI();
        }

        this.saveReceivedStateAsImage();
    }
    @Override
    public void gameOver() {
        // do nothing. Don't want to close GUI after game is over if we want to calmly view received states
        // for developers
    }


    protected void startGUI() {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        ImageIcon image = new ImageIcon();
        renderCurrent(image); // sets the image to the first and only state at this point
        JLabel label = new JLabel(image);
        panel.add(label);
        JScrollPane pane = new JScrollPane(panel);
        frame.add(pane);

        frame.setSize(1000, 1000);
        frame.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {}

            public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_LEFT:
                        renderLeft(image);
                        break;
                    case KeyEvent.VK_RIGHT:
                        renderRight(image);
                        break;
                    case KeyEvent.VK_DOWN:
                        saveCurrentStateToFileAsJson();
                        break;
                }
                panel.revalidate();
                panel.repaint();
            }

            public void keyPressed(KeyEvent e) {}
        });
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(true);
    }

    // Renders the previous state onto the given panel if
    // any, does nothing otherwise
    protected void renderLeft(ImageIcon image) {
        this.setToPreviousState();
        this.renderCurrent(image);
    }

    // Renders the next state onto the given panel if
    // any, does nothing otherwise
    protected void renderRight(ImageIcon image) {
        this.setToNextState();
        this.renderCurrent(image);
    }

    // Renders the current state onto the given panel
    protected void renderCurrent(ImageIcon image) {
        image.setImage(GameStateRenderer.getAsBufferedImage(states.get(currentStateIndex).visualize()));
    }


    protected void createTempDirectory() {
        //TODO figure out filepath of executable runtime and temp directory hard coded path is declared twice. refactor into a field
        File file = new File("../../8/Tmp");
        file.mkdir();
    }

    protected void setToPreviousState() {
        if (this.currentStateIndex > 0) {
            this.currentStateIndex -= 1;
        }
    }

    protected void setToNextState() {
        if (this.currentStateIndex < this.states.size() - 1) {
            this.currentStateIndex += 1;
        }
    }

    private void saveReceivedStateAsImage() {
        //TODO figure out filepath of executable runtime and temp directory hard coded path is declared twice. refactor into a field
        try {
            int mostRecentStateIndex = this.states.size() - 1;
            BufferedImage bufferedImage = GameStateRenderer.getAsBufferedImage(states.get(mostRecentStateIndex).visualize());
            String filename = String.format("../../8/Tmp/%s.png", mostRecentStateIndex);
            File file = new File(filename);
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException e) {
            //TODO print error OR implement GUI message for failed IO
        }
    }

    protected void saveCurrentStateToFileAsJson() {
        GameState currentGameState = this.states.get(currentStateIndex);
        JsonObject jState = currentGameState.toJson();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                Writer writer = new BufferedWriter(new FileWriter(selectedFile));
                writer.write(jState.toString());
                writer.flush();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                // do nothing. possibly add a feature to let the user know that their save failed.
            }
        }
    }
}
