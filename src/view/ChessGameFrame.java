package view;
import model.PlayerColor;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import static view.SwingUtil.createAutoAdjustIcon;

/**
 * 这个类表示游戏过程中的整个游戏界面，是一切的载体
 */
public class ChessGameFrame extends JFrame {
    //    public final Dimension FRAME_SIZE ;
    private final int WIDTH;
    private final int HEIGTH;
    private final int ONE_CHESS_SIZE;
    JLayeredPane layeredPane = new JLayeredPane();

    private ChessboardComponent chessboardComponent;
    public ChessGameFrame(int width, int height) {
        setTitle("2023 DouShouQi"); //设置标题
        this.WIDTH = width;
        this.HEIGTH = height;
        this.ONE_CHESS_SIZE = (HEIGTH* 4 / 5) / 9;

        setSize(WIDTH, HEIGTH);
        setLocationRelativeTo(null); //设置该控件相对其他控件为null，也就是不相对其他控件显示，居中显示在屏幕上
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //设置程序关闭按键，如果点击右上方的叉就游戏全部关闭了
        setLayout(null);

        add(layeredPane);

        //layeredPane.add(creatPanel(),JLayeredPane.MODAL_LAYER);
        //layeredPane.add(addBG(),Integer.MAX_VALUE);
        addBG();
        addChessboard();
        addLabel();
        addStartButton();
        addUndoButton();
        addSettingButton();
        addHelpButton();
        addLoadButton();
        addSaveButton();

        //addCurrentPlayerLable();
       }
    private void addBG() {
        //ImageIcon img = new ImageIcon("src/images/bg1.jpg");
        ImageIcon img = createAutoAdjustIcon("src/images/bg1.jpg", true);
        JLabel background = new JLabel(img);
        background.setSize(WIDTH, HEIGTH);
        this.getLayeredPane().add(background, JLayeredPane.DEFAULT_LAYER);
        //将背景标签添加到jfram的LayeredPane面板里，且置于底层
    }
    public ChessboardComponent getChessboardComponent() {
        return chessboardComponent;
    }

    public void setChessboardComponent(ChessboardComponent chessboardComponent) {
        this.chessboardComponent = chessboardComponent;
    }

    /**
     * 在游戏面板中添加棋盘
     */
    private void addChessboard() {
        chessboardComponent = new ChessboardComponent(ONE_CHESS_SIZE);
        chessboardComponent.setLocation(HEIGTH / 5, HEIGTH / 15);
        this.getLayeredPane().add(chessboardComponent, JLayeredPane.MODAL_LAYER);
    }
    private void addLabel() {
        JLabel statusLabel = new JLabel("-Jungle-");
        statusLabel.setLocation(800, HEIGTH / 10);
        statusLabel.setSize(200, 80);
        statusLabel.setFont(new Font("Blackadder ITC", Font.BOLD, 60));
        //设置字体格式Font mf = new Font(String 字体，int 风格，int 字号);
        //风格包括PLAIN：普通样式常量 BOLD :粗体样式常量 ITALIC: 斜体样式常量
        this.getLayeredPane().add(statusLabel, JLayeredPane.MODAL_LAYER);
    }
    private void addStartButton() {
        ImageIcon img = createAutoAdjustIcon("src/images/restart.jpg", true);
        JButton button = new JButton(img);
        //button.setContentAreaFilled(false);
        button.setBorder(null);
        button.setLocation(HEIGTH, HEIGTH / 10 + 120);
        button.setSize(170, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        this.getLayeredPane().add(button, JLayeredPane.MODAL_LAYER);
        button.addActionListener((e) -> {
            System.out.println("Click Restart");
            int result=JOptionPane.showConfirmDialog(this, "Are you sure to restart?", "Restart Option",
                    JOptionPane.YES_NO_OPTION);
            //() ->{}是Java8的Lambda表达式，e是函数式【接口中必须只有一个抽象方法】接口中抽象方法的形式参数，{}中是抽象方法的实现
            if(result==0) {//点击 是 返回值为0，点击 否 返回值为1，点击 x 返回值为-1
                chessboardComponent.getGameController().restart();
            }
        });
    }
    //悔棋功能：尚未设置首次移动前不能使用
    private void addUndoButton(){
        ImageIcon img = createAutoAdjustIcon("src/images/undo.jpg", true);
        JButton button = new JButton(img);
        button.setBorder(null);
        button.setLocation(HEIGTH, HEIGTH / 10 + 200);
        button.setSize(170, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        this.getLayeredPane().add(button, JLayeredPane.MODAL_LAYER);
        button.addActionListener((e) -> {
            System.out.println("Click Undo");
            int result=JOptionPane.showConfirmDialog(this, "Are you sure to undo your last step?", "Undo Option",
                    JOptionPane.YES_NO_OPTION);
            if(result==1) {
                chessboardComponent.getGameController().undo();
            }
        });
    }
    private void addSettingButton(){
        ImageIcon img = createAutoAdjustIcon("src/images/setting.jpg", true);
        JButton button = new JButton(img);
        button.setBorder(null);
        button.setLocation(HEIGTH, HEIGTH / 10 + 280);
        button.setSize(170, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        this.getLayeredPane().add(button, JLayeredPane.MODAL_LAYER);
        button.addActionListener((e) -> {
            System.out.println("Click setting");
            SettingGameFrame settingGameFrame = new SettingGameFrame(300,600,this);
            settingGameFrame.setVisible(true);
        });
    }
    private void addHelpButton(){
        ImageIcon img = createAutoAdjustIcon("src/images/help.jpg", true);
        JButton button = new JButton(img);
        button.setBorder(null);
        button.setLocation(HEIGTH, HEIGTH / 10 + 360);
        button.setSize(170, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        this.getLayeredPane().add(button, JLayeredPane.MODAL_LAYER);
        button.addActionListener((e) -> {
            String gameRules = "棋盘：\n"+
                    "棋盘横7列，纵9行。双方底线上各3个陷阱（作品字排）和一个兽穴（于品字中间)。\n" +
                    "双方各有八只棋子，依大小顺序为象、狮、虎、豹、狼、狗、猫、鼠。较大的可吃较小的，同类可以互吃，而鼠则可吃象。\n\n" +
                    "玩法规则：\n"+
                    "动物走一格，前后左右都可以。\n"+
                    "若对方的兽类走进陷阱，己方任何一只兽都可以把它吃掉。\n"+
                    "中间有两条小河。狮、虎可以横直方向跳过河，而且可以直接把对岸的动物吃掉。\n" +
                    "只有鼠可以下水，在水中的鼠可以阻隔狮、虎跳河。两鼠在水内可以互吃。水中的鼠不能吃岸上的象。\n\n" +
                    "胜负判决：\n" +
                    "如果一方进入了对方的兽穴或吃光对方的棋子便胜出。\n" +
                    "双方总步数达到 500步，判和。\n";
            JOptionPane.showMessageDialog(this, gameRules,"Game Rules:",1);
        });
    }

    private void addLoadButton() {
        ImageIcon img = createAutoAdjustIcon("src/images/load.jpg", true);
        JButton button = new JButton(img);
        button.setLocation(HEIGTH, HEIGTH / 10 +440);
        button.setSize(85, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        this.getLayeredPane().add(button, JLayeredPane.MODAL_LAYER);
        button.addActionListener(e -> {
            System.out.println("Click load");
            chessboardComponent.getGameController().load();
        });
    }
    private void addSaveButton(){
        ImageIcon img = createAutoAdjustIcon("src/images/save.jpg", true);
        JButton button = new JButton(img);
        button.setLocation(HEIGTH+95, HEIGTH / 10 +440);
        button.setSize(85, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        this.getLayeredPane().add(button, JLayeredPane.MODAL_LAYER);
        button.addActionListener(e -> {
            System.out.println("Click save");
            chessboardComponent.getGameController().save();
        });
    }

    private void addCurrentPlayerLable(){
        PlayerColor currentPlayer = chessboardComponent.getGameController().getCurrentPlayer();
        JLabel statusLabel = new JLabel(currentPlayer.toString()  + "'s turn");
        statusLabel.setLocation(800, HEIGTH / 500);
        statusLabel.setSize(200, 80);
        statusLabel.setFont(new Font("", Font.BOLD, 40));
        this.getLayeredPane().add(statusLabel, JLayeredPane.MODAL_LAYER);
        }
        private void addLable(){

        }

}