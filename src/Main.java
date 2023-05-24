import controller.GameController;
import model.Chessboard;
import view.ChessGameFrame;
import view.MusicPlayer;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MusicPlayer musicPlayer = new MusicPlayer();
            ChessGameFrame mainFrame = new ChessGameFrame(1100, 810, musicPlayer);
            mainFrame.setVisible(true);
        });
    }
}
