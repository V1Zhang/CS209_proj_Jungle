package view;

import controller.GameController;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;

import java.io.File;
import java.util.ArrayList;


public class SettingGameFrame extends JFrame{
    private final int width;
    private final int height;
    /*private Clip clip;
    //类比 ChessGameFrame中getGameController的方法：先声明要能get上一个类的实例
    SwingWorker<Void,Void> worker;
    ArrayList<SwingWorker<Void,Void> > workers;

     */
    private MusicPlayer musicPlayer;
    public SettingGameFrame(int width,int height,JFrame mainFrame, MusicPlayer musicPlayer){
        setTitle("Setting");
        this.width=width;
        this.height=height;
        this.musicPlayer = musicPlayer;

        setSize(width,height);
        setLocationRelativeTo(null); //设置该控件相对其他控件为null，也就是不相对其他控件显示，居中显示在屏幕上
        setResizable(false);//设置大小不可改变
        setLayout(null);
        addTitleLable();
        JPanel panel = new JPanel();
        add(panel); //在panel中才能setSize、setLocation

       addMusicLable();
        MusicButtonOn();
        MusicButtonOff();
        AIButton();
    }
    //声音音量调整  棋子风格
    private void addTitleLable(){
        JLabel titleLabel = new JLabel("Setting");
        titleLabel.setSize(150,30);
        titleLabel.setLocation(100,40);
        titleLabel.setFont(new Font("Rockwell", Font.BOLD, 30));
        add(titleLabel);
    }
    private void addMusicLable(){
        JLabel musicLabel = new JLabel("Music:");
        musicLabel.setSize(70,50);
        musicLabel.setLocation(30,80);
        musicLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(musicLabel);
    }

    /*public void musicPlay(boolean flag){
        try{
            if (flag==true) {
                clip.start();
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                System.out.println("Click music playing");
            }
            else if(!flag) {
                clip.stop();
                System.out.println("Click music stopping");
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

    }

     */


    private void MusicButtonOn() {
        String text = "Turn on";
        JButton button_on = new JButton(text);
        button_on.setBorder(null);
        button_on.setSize(70, 50);
        button_on.setLocation(110, 80);
        button_on.setFont(new Font("Rockwell", Font.BOLD, 15));
        add(button_on);
        button_on.addActionListener((e) -> {
            musicPlayer.setFlag(true);
            musicPlayer.musicPlay(musicPlayer.getFlag());
        });
    }
    private void MusicButtonOff() {
        String text = "Turn off";
        JButton button_off = new JButton(text);
        button_off.setBorder(null);
        button_off.setSize(70, 50);
        button_off.setLocation(190, 80);
        button_off.setFont(new Font("Rockwell", Font.BOLD, 15));
        add(button_off);
        button_off.addActionListener((e) -> {
            musicPlayer.setFlag(false);
            musicPlayer.musicPlay(musicPlayer.getFlag());
        });
    }
    private void AIButton(){
        JButton button = new JButton("Play with AI");
        button.setBorder(null);
        button.setSize(200, 40);
        button.setLocation(50,150);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }

}
