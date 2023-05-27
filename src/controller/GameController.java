package controller;

import listener.GameListener;

import model.*;
import view.*;
import view.ChessComponent.*;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import static model.Chessboard.*;
import static model.Constant.CHESSBOARD_COL_SIZE;
import static model.Constant.CHESSBOARD_ROW_SIZE;
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
    private Stack<ChessboardPoint> stack_point_before;// record every previous step's chessboardPoint
    private Stack<ChessboardPoint> stack_point_after;// record every next step's chessboardPoint
    private Stack<ChessPiece> eat;// record every previous step's chessboardPiece
    private Stack<ChessPiece> eaten;// record every next step's chessboardPoint
    private Stack<AnimalChessComponent> eaten_animal;// record every eaten animal
    private Stack<AnimalChessComponent> eat_animal;// record every eat animal
    ChessboardPoint chessPoint_before = null;
    ChessboardPoint chessPoint_after = null;
    private List<Step> stepList = new ArrayList<>();
    private List<String> stepList_str = new ArrayList<>();
    private Step step = new Step(null, null, null, null, currentPlayer, turnCount);

    public GameController(ChessboardComponent view, Chessboard model, ChessGameFrame frame) {
        this.view = view;
        this.model = model;
        this.frame = frame;
        view.registerController(this);
        view.initiateChessComponent(model);
        view.repaint();
        this.stack_point_before = new Stack<ChessboardPoint>();
        this.stack_point_after = new Stack<ChessboardPoint>();
        this.eaten = new Stack<>();
        this.eat = new Stack<>();
        this.eat_animal = new Stack<>();
        this.eaten_animal = new Stack<>();
    }

        // after a valid move swap the player
    private void swapColor() {
        currentPlayer = currentPlayer == PlayerColor.BLUE ? PlayerColor.RED : PlayerColor.BLUE;
        frame.getCurrentPlayerLabel().setText(String.format("%s's Turn", currentPlayer.toString()));
    }

    private void checkWin() {//吃光对方所有棋子或进入对方兽穴-取胜
        if (model.checkOpponentNone(currentPlayer)) {
            winner = currentPlayer;
            System.out.println("The winner is:" + winner);
            JOptionPane.showMessageDialog(null, "The winner is:" + currentPlayer, "Winner:", 1);
        } else if (model.checkDensWin() == PlayerColor.RED) {
            winner = PlayerColor.RED;
            System.out.println("The winner is:" + winner);
            JOptionPane.showMessageDialog(null, "The winner is:RED", "Winner:", 1);
        } else if (model.checkDensWin() == PlayerColor.BLUE) {
            winner = PlayerColor.BLUE;
            System.out.println("The winner is:" + winner);
            JOptionPane.showMessageDialog(null, "The winner is:BLUE", "Winner:", 1);
        }
    }

    public void viewValidMoves(ChessboardPoint point) {
        for (int i = 0; i < model.getValidMovesList(point).size(); i++) {
            validMoves.add(model.getValidMovesList(point).get(i));
        }
        view.showValidMoves(validMoves);
    }



    public void hideValidMoves() {
        view.hideValidMoves(validMoves);
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
            hideValidMoves();

            // record the process of changing chessboardPoint and chessPiece
            stack_point_before.push(selectedPoint);
            stack_point_after.push(point);
            ChessPiece temp_eat = model.getChessPieceAt(stack_point_before.peek());
            ChessPiece temp_eaten = model.getChessPieceAt(stack_point_after.peek()); //push进去null
            eat.push(temp_eat);
            eaten.push(temp_eaten);

            stepList.add(model.recordStep(selectedPoint, point, currentPlayer, turnCount));
            step = new Step(selectedPoint, point, getChessPieceAt(selectedPoint), null, currentPlayer, turnCount);
            stepList_str.add(step.toString());

            // TODO: if the chess enter Dens or Traps and so on
            model.solveTrap(selectedPoint, point);
            model.moveChessPiece(selectedPoint, point);
            view.setChessComponentAtGrid(point, view.removeChessComponentAtGrid(selectedPoint));

            view.repaint();
            //无论是否进入den 都要checkWin() 故不特殊判断
            checkWin();
            turnCount++;
            frame.getRoundNumLabel().setText(String.format("Round Number: %d", turnCount));
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
                viewValidMoves(selectedPoint);
                component.setSelected(true);
                component.repaint();

            }
        } else if (selectedPoint.equals(point)) {
            //如果之前已经选中了一个棋子，且当前点击的棋子位置和该棋子位置相同，则将所有可落子位置隐藏，取消选中状态。
            selectedPoint = null;
            component.setSelected(false);
            hideValidMoves();
            component.repaint();
            view.repaint();

            //在这段代码中，使用了 model.getChessPieceOwner() 方法获取指定棋子的所有者，并将其与 currentPlayer 进行比较，以判断棋子是否属于当前玩家。
            //注释掉的 throw 语句表示如果判断为非法落子或移动，将会抛出一个 IllegalArgumentException 异常。
        } else {//    如果之前已经选中一个棋子，且当前点击的棋子位置和该棋子位置不同，
            // 则判断是否符合规则调用 model.isValidCapture() 方法进行判断。
            // 如果不符合规则则打印提示信息并返回，否则执行下面的逻辑。
            if (model.isValidCapture(selectedPoint, point)) {
                stack_point_before.push(selectedPoint);
                stack_point_after.push(point);

                AnimalChessComponent temp_animal_eat = view.getAnimalChessComponent(stack_point_before.peek());
                AnimalChessComponent temp_animal_eaten = view.getAnimalChessComponent(stack_point_after.peek());
                eat_animal.push(temp_animal_eat);
                eaten_animal.push(temp_animal_eaten);
                ChessPiece temp_eat = model.getChessPieceAt(stack_point_before.peek());
                ChessPiece temp_eaten = model.getChessPieceAt(stack_point_after.peek());
                eat.push(temp_eat);
                eaten.push(temp_eaten);

                stepList.add(model.recordStep(selectedPoint, point, currentPlayer, turnCount));
                step = new Step(selectedPoint, point, model.getChessPieceAt(stack_point_before.peek()), model.getChessPieceAt(stack_point_after.peek()), currentPlayer, turnCount);
                stepList_str.add(step.toString());

                hideValidMoves();

                //model.solveTrap(selectedPoint,point);
                model.captureChessPiece(selectedPoint, point);
                view.removeChessComponentAtGrid(point);
                view.setChessComponentAtGrid(point, view.removeChessComponentAtGrid(selectedPoint));

                view.repaint();
                turnCount++;
                frame.getRoundNumLabel().setText(String.format("Round Number: %d", turnCount));


                checkWin();
                swapColor();
                selectedPoint = null;
            } else {
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
    public void setTurnCount(int turnCount){
        this.turnCount = turnCount;
    }

    public PlayerColor getCurrentPlayer() {
        return currentPlayer;
    }

    public int getTurnCount() {
        return turnCount;
    }


    public void undo() {
        //利用栈实现后进先出-撤销操作
        if (stack_point_after.size() == 0) {
            System.out.println("Can't undo");
        } else {
            ChessPiece chessPiece_before = eat.pop();
            ChessPiece chessPiece_after = eaten.pop();
            chessPoint_before = stack_point_before.pop();
            chessPoint_after = stack_point_after.pop();
            if (chessPiece_after != null) {
                //if we need to change two animal grid then we should acquire animal component from eat_animal and eaten_animal stack
                AnimalChessComponent temp_animal_eaten = eaten_animal.pop();
                AnimalChessComponent temp_animal_eat = eat_animal.pop();
                setChessPiece(chessPoint_before,chessPiece_before);
                setChessPiece(chessPoint_after,chessPiece_after);
                view.setChessComponentAtGrid(chessPoint_after, temp_animal_eaten);
                view.setChessComponentAtGrid(chessPoint_before, temp_animal_eat);
                System.out.printf("Undo from %s eat %s\n", temp_animal_eat.getName(), temp_animal_eaten.getName());
            } else {
                //if we only need to change an animal grid and an empty grid then we implement undo through modifying the process of move
                System.out.printf("Undo from (%d,%d) to (%d,%d), chess name=%s\n", chessPoint_after.getRow(), chessPoint_after.getCol(),
                        chessPoint_before.getRow(), chessPoint_before.getCol(), getChessPieceAt(chessPoint_after).getName());
                model.moveChessPiece(chessPoint_after, chessPoint_before);
                view.setChessComponentAtGrid(chessPoint_before, view.removeChessComponentAtGrid(chessPoint_after));
            }
            turnCount--;
            frame.getRoundNumLabel().setText(String.format("Round Number: %d", turnCount));
            swapColor();
            view.repaint();
        }
    }


    public List<String> convertToList() {
        List<String> lines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        if (currentPlayer == PlayerColor.RED) {
            sb.append("R");
            lines.add(sb + "\n");
        } else if (currentPlayer == PlayerColor.BLUE) {
            sb.append("B");
            lines.add(sb + "\n");
        }

        sb.setLength(0);
        sb.append(turnCount);
        lines.add(sb + "\n");

        for (int i = 0; i < CHESSBOARD_ROW_SIZE.getNum(); i++) {
            sb.setLength(0);
            for (int j = 0; j < CHESSBOARD_COL_SIZE.getNum(); j++) {
                Cell[][] grid = model.getGrid();
                if (grid[i][j].getPiece() != null) {
                    ChessPiece s = grid[i][j].getPiece();
                    if (Objects.equals(s.getName(), "Elephant")) {
                        if (s.getOwner() == PlayerColor.BLUE) {
                            sb.append("B8").append(",");
                        } else {
                            sb.append("R8").append(",");
                        }
                    } else if (Objects.equals(s.getName(), "Lion")) {
                        if (s.getOwner() == PlayerColor.BLUE) {
                            sb.append("B7").append(",");
                        } else {
                            sb.append("R7").append(",");
                        }
                    } else if (Objects.equals(s.getName(), "Tiger")) {
                        if (s.getOwner() == PlayerColor.BLUE) {
                            sb.append("B6").append(",");
                        } else {
                            sb.append("R6").append(",");
                        }
                    } else if (Objects.equals(s.getName(), "Leopard")) {
                        if (s.getOwner() == PlayerColor.BLUE) {
                            sb.append("B5").append(",");
                        } else {
                            sb.append("R5").append(",");
                        }
                    } else if (Objects.equals(s.getName(), "Wolf")) {
                        if (s.getOwner() == PlayerColor.BLUE) {
                            sb.append("B4").append(",");
                        } else {
                            sb.append("R4").append(",");
                        }
                    } else if (Objects.equals(s.getName(), "Dog")) {
                        if (s.getOwner() == PlayerColor.BLUE) {
                            sb.append("B3").append(",");
                        } else {
                            sb.append("R3").append(",");
                        }
                    } else if (Objects.equals(s.getName(), "Cat")) {
                        if (s.getOwner() == PlayerColor.BLUE) {
                            sb.append("B2").append(",");
                        } else {
                            sb.append("R2").append(",");
                        }
                    } else if (Objects.equals(s.getName(), "Rat")) {
                        if (s.getOwner() == PlayerColor.BLUE) {
                            sb.append("B1").append(",");
                        } else {
                            sb.append("R1").append(",");
                        }
                    }
                } else {
                    sb.append("0").append(",");
                }
            }
            sb.setLength(sb.length() - 1);
            lines.add(sb + "\n");
        }

        sb.setLength(0);
        for (int a = 0; a < stepList_str.size(); a++) {
            sb.append(stepList_str.get(a).toString()).append(",");
            lines.add(sb + "\n");
        }
        return lines;
    }

    public void save() {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                FileWriter fileWriter = new FileWriter(chooser.getSelectedFile());
                BufferedWriter writer = new BufferedWriter(fileWriter);
                List<String> lines = this.convertToList();
                for (String line : lines) {
                    writer.write(line);
                }
                writer.close();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void load(BufferedReader reader_again) throws IOException {
        int line = 0;
        String content = "";
        while ((content = reader_again.readLine()) != null) {
            if (line == 0) {
                if (content.equals("B")) {
                    frame.getCurrentPlayerLabel().setText(String.format("BLUE's Turn"));
                    setCurrentPlayer(PlayerColor.BLUE);
                } else {
                    frame.getCurrentPlayerLabel().setText(String.format("RED's Turn"));
                    setCurrentPlayer(PlayerColor.RED);
                }
            } else if (line == 1) {
                frame.getRoundNumLabel().setText(String.format("Round Number: %s", content));
                setTurnCount(Integer.parseInt(content));
            } else if (line >= 2 && line < 12) {
                String[] component_str = content.split(","); //把一行的分割开
                for (int s = 0; s < component_str.length; s++) {
                    String str = component_str[s];
                    if (str.equals("0")) {
                        model.getGridAt(new ChessboardPoint(line-2, s)).removePiece();
                        view.removeChessComponentAtGrid(new ChessboardPoint(line - 2, s));
                    } else if (str.equals("B8")) {
                        model.getGridAt(new ChessboardPoint(line-2, s)).removePiece();
                        view.removeChessComponentAtGrid(new ChessboardPoint(line - 2, s));
                        model.getGridAt(new ChessboardPoint(line-2, s)).setPiece(new ChessPiece(PlayerColor.BLUE, "Elephant", 8));
                        view.setChessComponentAtGrid(new ChessboardPoint(line-2,s), new ElephantChessComponent(PlayerColor.BLUE, 72));
                    } else if (str.equals("B7")) {
                        model.getGridAt(new ChessboardPoint(line-2, s)).removePiece();
                        view.removeChessComponentAtGrid(new ChessboardPoint(line - 2, s));
                        model.getGridAt(new ChessboardPoint(line-2, s)).setPiece(new ChessPiece(PlayerColor.BLUE, "Lion", 7));
                        view.setChessComponentAtGrid(new ChessboardPoint(line-2,s), new LionChessComponent(PlayerColor.BLUE, 72));
                    } else if (str.equals("B6")) {
                        model.getGridAt(new ChessboardPoint(line-2, s)).removePiece();
                        view.removeChessComponentAtGrid(new ChessboardPoint(line - 2, s));
                        model.getGridAt(new ChessboardPoint(line-2, s)).setPiece(new ChessPiece(PlayerColor.BLUE, "Tiger", 6));
                        view.setChessComponentAtGrid(new ChessboardPoint(line-2,s), new TigerChessComponent(PlayerColor.BLUE, 72));
                    } else if (str.equals("B5")) {
                        model.getGridAt(new ChessboardPoint(line-2, s)).removePiece();
                        view.removeChessComponentAtGrid(new ChessboardPoint(line - 2, s));
                        model.getGridAt(new ChessboardPoint(line-2, s)).setPiece(new ChessPiece(PlayerColor.BLUE, "Leopard", 5));
                        view.setChessComponentAtGrid(new ChessboardPoint(line-2,s), new LeopardChessComponent(PlayerColor.BLUE, 72));
                    } else if (str.equals("B4")) {
                        model.getGridAt(new ChessboardPoint(line-2, s)).removePiece();
                        view.removeChessComponentAtGrid(new ChessboardPoint(line - 2, s));
                        model.getGridAt(new ChessboardPoint(line-2, s)).setPiece(new ChessPiece(PlayerColor.BLUE, "Wolf", 4));
                        view.setChessComponentAtGrid(new ChessboardPoint(line-2,s), new WolfChessComponent(PlayerColor.BLUE, 72));
                    } else if (str.equals("B3")) {
                        model.getGridAt(new ChessboardPoint(line-2, s)).removePiece();
                        view.removeChessComponentAtGrid(new ChessboardPoint(line - 2, s));
                        model.getGridAt(new ChessboardPoint(line-2, s)).setPiece(new ChessPiece(PlayerColor.BLUE, "Dog", 3));
                        view.setChessComponentAtGrid(new ChessboardPoint(line-2,s), new DogChessComponent(PlayerColor.BLUE, 72));
                    } else if (str.equals("B2")) {
                        model.getGridAt(new ChessboardPoint(line-2, s)).removePiece();
                        view.removeChessComponentAtGrid(new ChessboardPoint(line - 2, s));
                        model.getGridAt(new ChessboardPoint(line-2, s)).setPiece(new ChessPiece(PlayerColor.BLUE, "Cat", 2));
                        view.setChessComponentAtGrid(new ChessboardPoint(line-2,s), new CatChessComponent(PlayerColor.BLUE, 72));
                    } else if (str.equals("B1")) {
                        model.getGridAt(new ChessboardPoint(line-2, s)).removePiece();
                        view.removeChessComponentAtGrid(new ChessboardPoint(line - 2, s));
                        model.getGridAt(new ChessboardPoint(line-2, s)).setPiece(new ChessPiece(PlayerColor.BLUE, "Rat", 1));
                        view.setChessComponentAtGrid(new ChessboardPoint(line-2,s), new RatChessComponent(PlayerColor.BLUE, 72));
                    } else if (str.equals("R8")) {
                        model.getGridAt(new ChessboardPoint(line-2, s)).removePiece();
                        view.removeChessComponentAtGrid(new ChessboardPoint(line - 2, s));
                        model.getGridAt(new ChessboardPoint(line-2, s)).setPiece(new ChessPiece(PlayerColor.RED, "Elephant", 8));
                        view.setChessComponentAtGrid(new ChessboardPoint(line-2,s), new ElephantChessComponent(PlayerColor.RED, 72));
                    } else if (str.equals("R7")) {
                        model.getGridAt(new ChessboardPoint(line-2, s)).removePiece();
                        view.removeChessComponentAtGrid(new ChessboardPoint(line - 2, s));
                        model.getGridAt(new ChessboardPoint(line-2, s)).setPiece(new ChessPiece(PlayerColor.RED, "Lion", 7));
                        view.setChessComponentAtGrid(new ChessboardPoint(line-2,s), new LionChessComponent(PlayerColor.RED, 72));
                    } else if (str.equals("R6")) {
                        model.getGridAt(new ChessboardPoint(line-2, s)).removePiece();
                        view.removeChessComponentAtGrid(new ChessboardPoint(line - 2, s));
                        model.getGridAt(new ChessboardPoint(line-2, s)).setPiece(new ChessPiece(PlayerColor.RED, "Tiger", 6));
                        view.setChessComponentAtGrid(new ChessboardPoint(line-2,s), new TigerChessComponent(PlayerColor.RED, 72));
                    } else if (str.equals("R5")) {
                        model.getGridAt(new ChessboardPoint(line-2, s)).removePiece();
                        view.removeChessComponentAtGrid(new ChessboardPoint(line - 2, s));
                        model.getGridAt(new ChessboardPoint(line-2, s)).setPiece(new ChessPiece(PlayerColor.RED, "Leopard", 5));
                        view.setChessComponentAtGrid(new ChessboardPoint(line-2,s), new LeopardChessComponent(PlayerColor.RED, 72));
                    } else if (str.equals("R4")) {
                        model.getGridAt(new ChessboardPoint(line-2, s)).removePiece();
                        view.removeChessComponentAtGrid(new ChessboardPoint(line - 2, s));
                        model.getGridAt(new ChessboardPoint(line-2, s)).setPiece(new ChessPiece(PlayerColor.RED, "Wolf", 4));
                        view.setChessComponentAtGrid(new ChessboardPoint(line-2,s), new WolfChessComponent(PlayerColor.RED, 72));
                    } else if (str.equals("R3")) {
                        model.getGridAt(new ChessboardPoint(line-2, s)).removePiece();
                        view.removeChessComponentAtGrid(new ChessboardPoint(line - 2, s));
                        model.getGridAt(new ChessboardPoint(line-2, s)).setPiece(new ChessPiece(PlayerColor.RED, "Dog", 3));
                        view.setChessComponentAtGrid(new ChessboardPoint(line-2,s), new DogChessComponent(PlayerColor.RED, 72));
                    } else if (str.equals("R2")) {
                        model.getGridAt(new ChessboardPoint(line-2, s)).removePiece();
                        view.removeChessComponentAtGrid(new ChessboardPoint(line - 2, s));
                        model.getGridAt(new ChessboardPoint(line-2, s)).setPiece(new ChessPiece(PlayerColor.RED, "Cat", 2));
                        view.setChessComponentAtGrid(new ChessboardPoint(line-2,s), new CatChessComponent(PlayerColor.RED, 72));
                    } else if (str.equals("R1")) {
                        model.getGridAt(new ChessboardPoint(line-2, s)).removePiece();
                        view.removeChessComponentAtGrid(new ChessboardPoint(line - 2, s));
                        model.getGridAt(new ChessboardPoint(line-2, s)).setPiece(new ChessPiece(PlayerColor.RED, "Rat", 1));
                        view.setChessComponentAtGrid(new ChessboardPoint(line-2,s), new RatChessComponent(PlayerColor.RED, 72));
                    }
                }
            }
            line++;


            //view.initiateChessComponent(model_new);


            //chessboardComponent.setChessComponentAtGrid(ChessboardPoint point, AnimalChessComponent chess)
            //chessboardComponent.gridComponents[line][s].add(new ElephantChessComponent(PlayerColor.BLUE, ONE_CHESS_SIZE));
            //grid[row][col].setPiece(new ChessPiece(PlayerColor.BLUE, "Elephant",8));

        /*for (Step step : stepList) {
            //model.runStep(step);
            //view.runStep(step);
            view.repaint();
            try {
                Thread.sleep(259);
                view.paintImmediately(0, 0, view.getWidth(), view.getHeight());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

         */
        }
        view.repaint();

        //Desktop.getDesktop().open(file);

    }
}


