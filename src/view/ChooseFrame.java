package view;
import controller.AI;
import controller.GameController;
import model.*;
import view.ChessComponent.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;
import static view.SwingUtil.createAutoAdjustIcon;
public class ChooseFrame extends JFrame{
    private final int w;
    private final int h;
    JLayeredPane layeredPane = new JLayeredPane();

    public ChooseFrame (int width, int height) {
        setTitle("Choose The Mode"); //设置标题
        this.w = width;
        this.h = height;

        setSize(w, h);
        setLocationRelativeTo(null); //设置该控件相对其他控件为null，也就是不相对其他控件显示，居中显示在屏幕上
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //设置程序关闭按键，如果点击右上方的叉就游戏全部关闭了
        setLayout(null);

        add(layeredPane);
        addButtonAI();
        addButtonTwo();
    }
    private void addButtonAI(){
        ImageIcon img = createAutoAdjustIcon("src/images/button1.jpg", true);
        JButton button = new JButton(img);
        button.setLocation(0, 0);
        button.setSize(280, 85);
        this.getLayeredPane().add(button, JLayeredPane.MODAL_LAYER);
        button.addActionListener((e) -> {
            System.out.println("AI Mode!");
            AI.startAIMode();
            MusicPlayer musicPlayer = new MusicPlayer();
            ChessGameFrame mainFrame = new ChessGameFrame(1100, 810, musicPlayer);
            mainFrame.setVisible(true);
            this.setVisible(false);
        });
    }
    private void addButtonTwo(){
        ImageIcon img = createAutoAdjustIcon("src/images/button2.jpg", true);
        JButton button = new JButton(img);
        button.setLocation(0, 87);
        button.setSize(280, 95);
        this.getLayeredPane().add(button, JLayeredPane.MODAL_LAYER);
        button.addActionListener((e) -> {
            System.out.println("Two Players");
            AI.quitAIMode();
            MusicPlayer musicPlayer = new MusicPlayer();
            ChessGameFrame mainFrame = new ChessGameFrame(1100, 810, musicPlayer);
            mainFrame.setVisible(true);
            this.setVisible(false);
        });
    }


}
