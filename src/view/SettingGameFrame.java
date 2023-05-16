package view;

import controller.GameController;

import javax.swing.*;
import java.awt.*;

import java.io.File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;


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
    static void playMusic() {// 背景音乐播放
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File("D:/mysoft/eclipse/贪吃蛇/music.wav"));    //绝对路径
            AudioFormat aif = ais.getFormat();
            final SourceDataLine sdl;
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, aif);
            sdl = (SourceDataLine) AudioSystem.getLine(info);
            sdl.open(aif);
            sdl.start();
            FloatControl fc = (FloatControl) sdl.getControl(FloatControl.Type.MASTER_GAIN);
            // value可以用来设置音量，从0-2.0
            double value = 2;
            float dB = (float) (Math.log(value == 0.0 ? 0.0001 : value) / Math.log(10.0) * 20.0);
            fc.setValue(dB);
            int nByte = 0;
            final int SIZE = 1024 * 64;
            byte[] buffer = new byte[SIZE];
            while (nByte != -1) {
                nByte = ais.read(buffer, 0, SIZE);
                sdl.write(buffer, 0, nByte);
            }
            sdl.stop();

        } catch (Exception e) {
            e.printStackTrace();
        }
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
    private void addMusicButton(){
        JButton button = new JButton("Music");

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
