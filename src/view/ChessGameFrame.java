package view;
import controller.GameController;
import model.*;
import view.ChessComponent.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;



import static view.SwingUtil.createAutoAdjustIcon;

/**
 * 这个类表示游戏过程中的整个游戏界面，是一切的载体
 */
public class ChessGameFrame extends JFrame {
    //    public final Dimension FRAME_SIZE ;
    private final int WIDTH;
    private final int HEIGTH;
    private final int ONE_CHESS_SIZE;
    private PlayerColor currentPlayer;
    private int turnCount=1;
    private JLabel RoundsNumLabel = new JLabel();
    private JLabel CurrentPlayerLabel = new JLabel();
    private JButton UndoButton = new JButton();
    JLayeredPane layeredPane = new JLayeredPane();

    private MusicPlayer musicPlayer;
    private ChessboardComponent chessboardComponent;
    public ChessGameFrame(int width, int height, MusicPlayer musicPlayer) {
        //添加一个参数MusicPlayer 从而实现每一个new的frame都能调用同一个MusicPlayer
        setTitle("2023 DouShouQi"); //设置标题
        this.WIDTH = width;
        this.HEIGTH = height;
        this.ONE_CHESS_SIZE = (HEIGTH* 4 / 5) / 9;
        this.musicPlayer = musicPlayer;

        setSize(WIDTH, HEIGTH);
        setLocationRelativeTo(null); //设置该控件相对其他控件为null，也就是不相对其他控件显示，居中显示在屏幕上
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //设置程序关闭按键，如果点击右上方的叉就游戏全部关闭了
        setLayout(null);

        addBG();
        add(layeredPane);

        //layeredPane.add(creatPanel(),JLayeredPane.MODAL_LAYER);
        //layeredPane.add(addBG(),Integer.MAX_VALUE);

        addChessboard();
        addLabel();
        addStartButton();
        add(UndoButton);
        setUndoButton();
        addSettingButton();
        addHelpButton();
        addLoadButton();
        addSaveButton();

        add(CurrentPlayerLabel);
        add(RoundsNumLabel);
        setCurrentPlayerLabel();
        setRoundsNumLabel();
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
        GameController controller = new GameController(chessboardComponent,new Chessboard(),this);
        chessboardComponent.setGameController(controller);
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
                ChessGameFrame mainFrame = new ChessGameFrame(1100, 810, musicPlayer);
                mainFrame.setVisible(true);
                setVisible(false);
                //chessboardComponent.getGameController().restart();
            }
        });

    }
    //悔棋功能：尚未设置首次移动前不能使用
    private void setUndoButton(){
        ImageIcon img = createAutoAdjustIcon("src/images/undo.jpg", true);
        UndoButton = new JButton(img);
        UndoButton.setLocation(HEIGTH, HEIGTH / 10 + 200);
        UndoButton.setSize(170, 60);
        UndoButton.setFont(new Font("Rockwell", Font.BOLD, 20));
        this.getLayeredPane().add(UndoButton, JLayeredPane.MODAL_LAYER);
        UndoButton.addActionListener((e) -> {
            System.out.println("Click Undo");
            int result=JOptionPane.showConfirmDialog(this, "Are you sure to undo your last step?", "Undo Option",
                    JOptionPane.YES_NO_OPTION);
            if(result==0) {
                chessboardComponent.getGameController().undo();
            }
        });
    }
    private void addSettingButton(){
        ImageIcon img = createAutoAdjustIcon("src/images/setting.jpg", true);
        JButton button = new JButton(img);
        button.setLocation(HEIGTH, HEIGTH / 10 + 280);
        button.setSize(170, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        this.getLayeredPane().add(button, JLayeredPane.MODAL_LAYER);
        button.addActionListener((e) -> {
            System.out.println("Click setting");
            SettingGameFrame settingGameFrame = new SettingGameFrame(300,600,this, musicPlayer);
            settingGameFrame.setVisible(true);
        });
    }
    private void addHelpButton(){
        ImageIcon img = createAutoAdjustIcon("src/images/help.jpg", true);
        JButton button = new JButton(img);
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
                    "如果一方进入了对方的兽穴，或吃光对方的棋子，或使对方只剩一个棋子且没有可走位置，便胜出。\n" ;
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
            JFileChooser chooser = new JFileChooser();
            //FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF & DOC & TXT Images", "pdf", "doc", "txt");
            //chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(this);
            //Component参数:父参数决定了两件事:打开对话框所依赖的框架和放置对话框时应考虑其外观位置的组件。
            // 如果父对象是一个框架对象(例如JFrame)，那么该对话框取决于框架和外观，对话框相对于框架的位置(例如，以框架为中心)。
            // 如果父元素是一个组件，那么对话框取决于包含该组件的框架，并且相对于该组件定位(例如，以组件为中心)。如果父窗口为空，则对话框依赖于没有可见窗口，并且它被放置在外观和感觉相关的位置，例如屏幕的中心。
            // parent参数：null———当前电脑显示器屏幕的中央。this———当前你编写的程序屏幕中央.如果是你其他的按钮名称就是以这个按钮为中心，弹出的文件选择器。
            if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String path = file.getPath();
            String name = file.getName();
            String extension = name.substring(file.getName().lastIndexOf(".")+1).toLowerCase();
            if (extension.equals("txt")) {
                System.out.println("You chose to open this file: " + name);
                int line = 0;
                String content = "";
                boolean readable = true;
                boolean chessError = false;
                try {
                    //ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))
                    // InputStream是表示字节输入流的所有类的超类,提供的是字节流的读取,而非文本读取；
                    // Reader是用于读取字符流的抽象类，即用Reader读取出来的是char数组或者String ，使用InputStream读取出来的是byte数组。
                    FileReader fileReader = new FileReader(file);
                    BufferedReader reader = new BufferedReader(fileReader);
                    while ((content = reader.readLine()) != null) {
                        String[] strings = content.split(",");
                        //根据不同的行数处理不同的异常
                        if (line == 0) {
                            if (content.equals("B") && content.equals("R")) { //未提供下一步行棋方 错误编码104
                                JOptionPane.showMessageDialog(this, "Error 104: No Current Player Message", "Error", 1);
                                readable = false;
                                break;
                            }
                        }
                        if (line >= 2 && line < 11) { // 2-10行为棋盘
                            if (strings.length != 7) { //列数不为7 错误编码102
                                JOptionPane.showMessageDialog(this, "Error 102: Not 9*7 Chessboard-COL","Error", 1);
                                readable = false;
                                break;
                            }
                        }
                        if (line == 12) {
                            if (strings.length == 7) { //行数不为9 错误编码102
                                JOptionPane.showMessageDialog(this, "Error 102: Not 9*7 Chessboard-ROW", "Error", 1);
                                readable = false;
                                break;
                            }
                        }
                        if(line >= 2 && line < 11) {
                            String[] component_str = content.split(",");
                            for (String str : component_str) {
                                String[] standardstr = {"R1", "R2", "R3", "R4", "R5", "R5", "R6", "R7", "R8", "B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "0"};
                                    for(int j=0;j < standardstr.length;j++) {
                                        if (str.equals(standardstr[j])) {
                                            chessError=true;
                                        }
                                    }
                            }
                            if (chessError==false) { //棋子错误 错误编码101
                                JOptionPane.showMessageDialog(this, "Error 101: Wrong Chess Component", "Error", 1);
                                readable = false;
                                break;
                            }
                        }
                        line++;
                    }
                    if (readable == true) {
                        FileReader fileReader_again = new FileReader(file);
                        BufferedReader reader_again = new BufferedReader(fileReader_again);
                        chessboardComponent.getGameController().load(reader_again);
                    }
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            } else { //文件格式错误处理 错误编码101
                JOptionPane.showMessageDialog(this, "Error 101: File Extension Error", "Error", 1);
            }





            }
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
    public void setCurrentPlayerLabel() {
        CurrentPlayerLabel = new JLabel( "BLUE 's turn");
        //currentPlayer = chessboardComponent.getGameController().getCurrentPlayer();
        //JLabel CurrentPlayerLabel = new JLabel(currentPlayer.toString() + " 's turn");
        //通过controller更改label内容 否则在这里直接调用getCurrentPlayer首次会空指针
        CurrentPlayerLabel.setLocation(820, HEIGTH - 200);
        CurrentPlayerLabel.setSize(200, 80);
        CurrentPlayerLabel.setFont(new Font("Bernard MT Condensed", Font.BOLD, 25));
        this.getLayeredPane().add(CurrentPlayerLabel, JLayeredPane.MODAL_LAYER);

    }
    public void setRoundsNumLabel() {
        RoundsNumLabel = new JLabel("Round Number: 1");
        RoundsNumLabel.setLocation(820, HEIGTH - 150);
        RoundsNumLabel.setSize(300, 80);
        RoundsNumLabel.setFont(new Font("Bernard MT Condensed", Font.BOLD, 22));
        this.getLayeredPane().add(RoundsNumLabel, JLayeredPane.MODAL_LAYER);
    }
    public JLabel getCurrentPlayerLabel(){
        return CurrentPlayerLabel;
    }
    public JLabel getRoundNumLabel(){
        return RoundsNumLabel;
    }

}