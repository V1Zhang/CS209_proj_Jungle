package controller;

import listener.GameListener;
import model.Constant;
import model.PlayerColor;
import model.Chessboard;
import model.ChessboardPoint;
import view.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller is the connection between model and view,
 * when a Controller receive a request from a view, the Controller
 * analyzes and then hands over to the model for processing
 * [in this demo the request methods are onPlayerClickCell() and onPlayerClickChessPiece()]
 *
*/
public class GameController implements GameListener {
    private Chessboard model;
    private ChessboardComponent view;
    private PlayerColor currentPlayer;
    // Record whether there is a selected piece before
    private ChessboardPoint selectedPoint;
    private int turnCount = 1;
    private PlayerColor winner;
    private List<ChessboardPoint> validMoves;
    public GameController(ChessboardComponent view, Chessboard model) {
        this.view = view;
        this.model = model;
        this.currentPlayer = PlayerColor.BLUE;
        validMoves = new ArrayList<>();
        view.registerController(this);
        initialize();
        view.initiateChessComponent(model);
        view.repaint();
    }

    private void initialize() {
        for (int i = 0; i < Constant.CHESSBOARD_ROW_SIZE.getNum(); i++) {
            for (int j = 0; j < Constant.CHESSBOARD_COL_SIZE.getNum(); j++) {
            }
        }
    }

    // after a valid move swap the player
    private void swapColor() {
        currentPlayer = currentPlayer == PlayerColor.BLUE ? PlayerColor.RED : PlayerColor.BLUE;
    }

    private boolean win() {
        // TODO: Check the board if there is a winner
        return false;
    }



    // click an empty cell
    @Override
    public void onPlayerClickCell(ChessboardPoint point, CellComponent component) {
        if (selectedPoint != null && model.isValidMove(selectedPoint, point)) {


            model.solveTrap(selectedPoint,point);
            model.moveChessPiece(selectedPoint, point);
            view.setChessComponentAtGrid(point, view.removeChessComponentAtGrid(selectedPoint));

            if (model.solveDens(point)){

            }
            selectedPoint = null;
            swapColor();
            view.repaint();
            // TODO: if the chess enter Dens or Traps and so on
        }
    }

    // click a cell with a chess
    //这段代码是一个 ChessComponent 的事件处理方法，处理某个玩家点击棋盘上的棋子时的行为。
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
                model.captureChessPiece(selectedPoint, point);
                view.removeChessComponentAtGrid(point);
                view.setChessComponentAtGrid(point, view.removeChessComponentAtGrid(selectedPoint));
                selectedPoint = null;
                view.repaint();
            }else{
                System.out.println("Illegal chess capture!");
                return;
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





}

