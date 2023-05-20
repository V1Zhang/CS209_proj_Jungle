package controller;

import listener.GameListener;

import model.*;
import view.*;
import view.ChessComponent.AnimalChessComponent;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static model.Chessboard.*;
import static view.ChessboardComponent.getGridComponentAt;

/**
 * Controller is the connection between model and view,
 * when a Controller receive a request from a view, the Controller
 * analyzes and then hands over to the model for processing
 * [in this demo the request methods are onPlayerClickCell() and onPlayerClickChessPiece()]
 *
 */
public class GameController implements GameListener {
    private ChessGameFrame frame;
    private Chessboard model;
    private ChessboardComponent view;
    private PlayerColor currentPlayer = PlayerColor.BLUE;
    // Record whether there is a selected piece before
    private ChessboardPoint selectedPoint;
    private int turnCount = 1;
    private PlayerColor winner;
    private List<ChessboardPoint> validMoves = new ArrayList<>();
    private Stack<ChessboardPoint> stack_point_before;
    private Stack<ChessboardPoint> stack_point_after;
    private Stack<ChessPiece> eaten;
    private AnimalChessComponent temp_eat = null;
    private AnimalChessComponent temp_eaten = null;
    private ChessPiece temp_model_eat = null;
    private ChessPiece temp_model_eaten = null;
    ChessPiece temp = null;
    ChessboardPoint chessPoint_before = null;
    ChessboardPoint chessPoint_after = null;
    public GameController(ChessboardComponent view, Chessboard model,ChessGameFrame frame) {
        this.view = view;
        this.model = model;
        this.frame = frame;
        view.registerController(this);
        view.initiateChessComponent(model);
        view.repaint();
        this.stack_point_before = new Stack<ChessboardPoint>();
        this.stack_point_after = new Stack<ChessboardPoint>();
        this.eaten = new Stack<>();
    }

    // after a valid move swap the player
    private void swapColor() {
        currentPlayer = currentPlayer == PlayerColor.BLUE ? PlayerColor.RED : PlayerColor.BLUE;
        frame.getCurrentPlayerLabel().setText(String.format("%s's Turn", currentPlayer.toString()));;
    }

    private void checkWin(){//吃光对方所有棋子或进入对方兽穴-取胜
        if(model.checkOpponentNone(currentPlayer)){
            winner = currentPlayer;
            System.out.println("The winner is:"+winner);
            JOptionPane.showMessageDialog(null, "The winner is:"+currentPlayer,"Winner:",1);
        } else if (model.checkDensWin()== PlayerColor.RED) {
            winner = PlayerColor.RED;
            System.out.println("The winner is:"+winner);
            JOptionPane.showMessageDialog(null, "The winner is:RED","Winner:",1);
        } else if (model.checkDensWin() ==PlayerColor.BLUE) {
            winner = PlayerColor.BLUE;
            System.out.println("The winner is:"+winner);
            JOptionPane.showMessageDialog(null, "The winner is:BLUE","Winner:",1);
        }
    }

    public void showValidMoves(ChessboardPoint point) {
        validMoves = model.getValidMoves(point);
        view.showValidMoves(validMoves);
    }
    //showValidMoves() 方法首先调用了 model.getValidMoves(point) 方法，
    // 获取指定位置棋子的所有可移动位置，将结果保存在成员变量 validMoves 中。
    // 接下来，它调用视图层的 showValidMoves(validMoves) 方法，将可移动位置传递给视图层，
    // 让视图层根据这些位置来更新棋盘的显示。


    // click an empty cell
    @Override
    public void onPlayerClickCell(ChessboardPoint point, CellComponent component) {
        //如果当前是选中棋子的状态，那么此时需要调用 model.selectPiece(point) 方法来选中当前格子上的棋子；
        // 如果当前是移动棋子的状态，那么需要调用 model.movePiece(selectedPiece, point) 方法来移动选中的棋子。
        // 同时，无论何时，该方法都需要调用 showValidMoves(point) 方法来让视图层展示当前棋子的可移动位置。
        if (selectedPoint != null && model.isValidMove(selectedPoint, point)) {
            stack_point_before.push(selectedPoint);
            stack_point_after.push(point);
            temp_eat = view.getAnimalChessComponent(stack_point_before.pop());
            stack_point_before.push(selectedPoint);
            eaten.push(temp_model_eaten);

            model.moveChessPiece(selectedPoint, point);
            view.setChessComponentAtGrid(point, view.removeChessComponentAtGrid(selectedPoint));


            // TODO: if the chess enter Dens or Traps and so on
            model.solveTrap(selectedPoint,point);

            view.repaint();
            //无论是否进入den 都要checkWin() 故不特殊判断
            checkWin();
            turnCount++;
            frame.getRoundNumLabel().setText(String.format("Round Number: %d",turnCount));
            swapColor();
            selectedPoint = null;

        }

    }
    // click an objective cell with a chess
    @Override
    public void onPlayerClickChessPiece(ChessboardPoint point, AnimalChessComponent component) {
        if (selectedPoint == null) {
            //首先，如果之前没有选中任何棋子（即 selectedPoint 为 null），则判断当前点击的棋子是否属于当前玩家。
            // 如果是，则将该棋子设为选中状态，并调用 showValidMoves() 方法展示可行的落子位置。
            if (model.getChessPieceOwner(point).equals(currentPlayer)) {
                selectedPoint = point;
                component.setSelected(true);
                component.repaint();

            }
        } else if (selectedPoint.equals(point)) {
            //如果之前已经选中了一个棋子，且当前点击的棋子位置和该棋子位置相同，则将所有可落子位置隐藏，取消选中状态。
            selectedPoint = null;
            component.setSelected(false);
            component.repaint();
            //在这段代码中，使用了 model.getChessPieceOwner() 方法获取指定棋子的所有者，并将其与 currentPlayer 进行比较，以判断棋子是否属于当前玩家。
            //注释掉的 throw 语句表示如果判断为非法落子或移动，将会抛出一个 IllegalArgumentException 异常。
        } else{//    如果之前已经选中一个棋子，且当前点击的棋子位置和该棋子位置不同，
            // 则判断是否符合规则调用 model.isValidCapture() 方法进行判断。
            // 如果不符合规则则打印提示信息并返回，否则执行下面的逻辑。
            if (model.isValidCapture(selectedPoint,point)){

                stack_point_before.push(selectedPoint);
                stack_point_after.push(point);

                temp_model_eat = model.getChessPieceAt(selectedPoint);
                temp_model_eaten = model.getChessPieceAt(point);
                eaten.push(temp_model_eaten);

                temp_eat = view.getAnimalChessComponent(stack_point_before.pop());
                temp_eaten = view.getAnimalChessComponent(stack_point_after.pop());
                //得到两个位置对应的棋子 因为调用了pop所以再重新push一次
                stack_point_before.push(selectedPoint);
                stack_point_after.push(point);

                model.captureChessPiece(selectedPoint, point);
                view.removeChessComponentAtGrid(point);
                view.setChessComponentAtGrid(point, view.removeChessComponentAtGrid(selectedPoint));

                view.repaint();
                turnCount++;
                frame.getRoundNumLabel().setText(String.format("Round Number: %d",turnCount));
                swapColor();
                checkWin();
                selectedPoint = null;
                }
            else{
                System.out.println("Illegal chess capture!");
            }
        }
        // 如果指定的棋子落子或者棋子移动不符合规则，将会打印出 "Illegal chess capture!" 的消息，
        // 并且结束当前方法（通过 return 关键字）。
        // 其中，model 是一个 ChessBoardModel 类型的对象，
        // isValidCapture() 方法用于判断当前棋子落子或移动是否符合棋盘规则，
        // selectedPoint 表示当前选中的棋子的坐标，
        // point 表示将要落子或移动到的坐标。
        // 如果 isValidCapture() 返回 false，将会执行打印语句和 return 语句，否则直接执行下面的代码。
        // 同时，被注释掉的 throw 语句表示如果判断为非法落子或移动，将会抛出一个 IllegalArgumentException 异常。
        // TODO: Implement capture function
    }

    public void setCurrentPlayer(PlayerColor currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    public PlayerColor getCurrentPlayer() {
        return currentPlayer;
    }
    public int getTurnCount() {
        return turnCount;
    }

    public void swap(){
        chessPoint_before = stack_point_before.pop();
        chessPoint_after = stack_point_after.pop();
        //为了实现坐标交换，需要有一个中间值temp
        temp = temp_model_eat;
        setChessPiece(chessPoint_after,temp_model_eaten);
        setChessPiece(chessPoint_before,temp);
    }

       public void undo() {
           //利用栈实现后进先出-撤销操作
           if (stack_point_after.size() == 0) {
               System.out.println("Can't undo");
           } else {
               if (eaten.pop() != null) {
                   System.out.println();
                   swap();
                   view.setChessComponentAtGrid(chessPoint_after, temp_eaten);
                   view.setChessComponentAtGrid(chessPoint_before, temp_eat);
               }
               else {
                   ChessboardPoint chessPoint_before = stack_point_before.pop();
                   ChessboardPoint chessPoint_after = stack_point_after.pop();
                   System.out.printf("Undo from (%d,%d) to (%d,%d), chess name=%s\n",chessPoint_after.getRow(),chessPoint_after.getCol(),
                           chessPoint_before.getRow(),chessPoint_before.getCol(),getChessPieceAt(chessPoint_after).getName());
                   model.moveChessPiece(chessPoint_after, chessPoint_before);
                   view.setChessComponentAtGrid(chessPoint_before, view.removeChessComponentAtGrid(chessPoint_after));
                   }
                    turnCount--;
                    frame.getRoundNumLabel().setText(String.format("Round Number: %d",turnCount));
                    swapColor();
                    view.repaint();
               }
           }

        public void load() {
            JFileChooser chooser = new JFileChooser();
            //FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF & DOC & TXT Images", "pdf", "doc", "txt");
            //chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            //Component参数:父参数决定了两件事:打开对话框所依赖的框架和放置对话框时应考虑其外观位置的组件。
            // 如果父对象是一个框架对象(例如JFrame)，那么该对话框取决于框架和外观，对话框相对于框架的位置(例如，以框架为中心)。
            // 如果父元素是一个组件，那么对话框取决于包含该组件的框架，并且相对于该组件定位(例如，以组件为中心)。如果父窗口为空，则对话框依赖于没有可见窗口，并且它被放置在外观和感觉相关的位置，例如屏幕的中心。
            // parent参数：null———当前电脑显示器屏幕的中央。this———当前你编写的程序屏幕中央.如果是你其他的按钮名称就是以这个按钮为中心，弹出的文件选择器。
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getPath();
                System.out.println("You chose to open this file: " +  chooser.getSelectedFile().getName());
                File file = new File(path);
                try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))){
                    List<Step> stepList = (List<Step>) inputStream.readObject();
                    for(Step step : stepList){
                        //model.runStep(step);
                        //view.runStep(step);
                        view.repaint();
                        try{
                            Thread.sleep(259);
                            view.paintImmediately(0,0,view.getWidth(),view.getHeight());
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    //Desktop.getDesktop().open(file);
                }catch(IOException e){
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        public void save() {
            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showOpenDialog(null);
        }
}


