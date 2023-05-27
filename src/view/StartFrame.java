package view;

import controller.GameController;
import model.*;
import view.ChessComponent.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;



import static view.SwingUtil.createAutoAdjustIcon;
public class StartFrame extends JFrame{
    private final int W;
    private final int H;
    JLayeredPane layeredPane = new JLayeredPane();
    public StartFrame(int width, int height) {
        //添加一个参数MusicPlayer 从而实现每一个new的frame都能调用同一个MusicPlayer
        setTitle("2023 DouShouQi"); //设置标题
        this.W = width;
        this.H = height;

        setSize(W, H);
        setLocationRelativeTo(null); //设置该控件相对其他控件为null，也就是不相对其他控件显示，居中显示在屏幕上
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //设置程序关闭按键，如果点击右上方的叉就游戏全部关闭了
        setLayout(null);

        addBG();
        add(layeredPane);
        addLabel1();
        addLabel2();
        addStartButton();
    }
    private void addBG() {
        //ImageIcon img = new ImageIcon("src/images/bg1.jpg");
        ImageIcon img = createAutoAdjustIcon("src/images/StartFrameBG.jpg", true);
        JLabel background = new JLabel(img);
        background.setSize(W, H);
        this.getLayeredPane().add(background, JLayeredPane.DEFAULT_LAYER);
        //将背景标签添加到jfram的LayeredPane面板里，且置于底层
    }
    private void addLabel1() {
        JLabel statusLabel = new JLabel("-Welcome to Jungle-");
        statusLabel.setLocation(100, H / 8);
        statusLabel.setSize(400, 80);
        statusLabel.setFont(new Font("Blackadder ITC", Font.BOLD, 40));
        //设置字体格式Font mf = new Font(String 字体，int 风格，int 字号);
        //风格包括PLAIN：普通样式常量 BOLD :粗体样式常量 ITALIC: 斜体样式常量
        this.getLayeredPane().add(statusLabel, JLayeredPane.MODAL_LAYER);
    }
    private void addLabel2() {
        JLabel statusLabel = new JLabel("-Weiyi Zhang & Yulin Liu-");
        statusLabel.setLocation(125, H / 5);
        statusLabel.setSize(400, 80);
        statusLabel.setFont(new Font("Bahnschrift SemiBold Condensed", Font.BOLD, 20));
        //设置字体格式Font mf = new Font(String 字体，int 风格，int 字号);
        //风格包括PLAIN：普通样式常量 BOLD :粗体样式常量 ITALIC: 斜体样式常量
        this.getLayeredPane().add(statusLabel, JLayeredPane.MODAL_LAYER);
    }
    private void addStartButton(){
        ImageIcon img = createAutoAdjustIcon("src/images/start.jpg", true);
        JButton button = new JButton(img);
        button.setLocation(230, 410);
        button.setSize(130, 60);
        this.getLayeredPane().add(button, JLayeredPane.MODAL_LAYER);
        button.addActionListener((e) -> {
            System.out.println("Choose Mode");
            ChooseFrame chooseFrame = new ChooseFrame(280,200);
            chooseFrame.setVisible(true);
            this.setVisible(false);
        });
    }
}
