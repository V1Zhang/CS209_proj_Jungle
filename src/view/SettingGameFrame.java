package view;

import controller.GameController;

import javax.swing.*;
import java.awt.*;
public class SettingGameFrame extends JFrame{
    private final int width;
    private final int height;
    private ChessGameFrame chessGameFrame;
    //类比 ChessGameFrame中getGameController的方法：先声明要能get上一个类的实例

    public SettingGameFrame(int width,int height,JFrame mainFrame){
        setTitle("Setting");
        this.width=width;
        this.height=height;

        setSize(width,height);
        setLocationRelativeTo(null); //设置该控件相对其他控件为null，也就是不相对其他控件显示，居中显示在屏幕上
        setResizable(false);//设置大小不可改变
        setLayout(null);
        setLayout(new GridLayout(5, 1, 10, 10));
        addTitleLable();


    }

    //声音音量调整  棋子风格
    private void addTitleLable(){
        JLabel titleLabel = new JLabel("Setting");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setVerticalAlignment(SwingConstants.CENTER);
        titleLabel.setSize(80, 30);
        titleLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(titleLabel);
    }
    private void addSettingButton(){
        JButton button = new JButton("Setting");
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
        button.addActionListener((e) -> {
            System.out.println("Click setting");
            SettingGameFrame settingGameFrame = new SettingGameFrame(500,700,this);
            settingGameFrame.setVisible(true);
        });
    }




}
