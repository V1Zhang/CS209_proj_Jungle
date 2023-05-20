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
            //GameController gameController = new GameController(mainFrame.getChessboardComponent(), new Chessboard());
            mainFrame.setVisible(true);
        });
    }
}
