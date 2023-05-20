package view;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class MusicPlayer {
    private boolean flag = false;
    private Clip clip;
    //类比 ChessGameFrame中getGameController的方法：先声明要能get上一个类的实例
    public MusicPlayer() {
        try {
            File musicPath = new File("src/music/background.wav");
            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void musicPlay(boolean flag) {
        try {
            if (flag == true) {
                clip.start();
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                System.out.println("Click music playing");
            } else if (!flag) {
                clip.stop();
                System.out.println("Click music stopping");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void setFlag(boolean flag) {
        this.flag = flag;
    }
    public boolean getFlag(){
        return flag;
    }
}

