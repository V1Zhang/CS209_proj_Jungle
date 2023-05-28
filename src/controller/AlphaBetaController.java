package controller;

import listener.GameListener;
import model.*;
import view.CellComponent;
import view.ChessComponent.*;
import view.ChessGameFrame;
import view.ChessboardComponent;
import view.GridType;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static model.Chessboard.getChessPieceAt;
import static model.Chessboard.setChessPiece;
import static model.Constant.CHESSBOARD_COL_SIZE;
import static model.Constant.CHESSBOARD_ROW_SIZE;

public class AlphaBetaController implements GameListener {
    private ChessGameFrame frame;
    private static Chessboard model;
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
    int size = 0;
    ArrayList<Integer> scores = new ArrayList<>();

    int min_max_index = 0;

    public AlphaBetaController(ChessboardComponent view, Chessboard model, ChessGameFrame frame) {
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

    public void ABPlay() {
        int[][] max = new int[9][7];
        int m=0;
        ChessboardPoint choosen_point = null;
        ChessboardPoint dest = null;
        // 遍历当前玩家所有棋子的位置
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                choosen_point = ChessboardPoint.getChessboardPoint(i, j);
                if (getChessPieceAt(choosen_point) != null && getChessPieceAt(choosen_point).getOwner() == currentPlayer) {// 判断是否为当前玩家的棋子
                    size = model.getValidMovesList(choosen_point).size();
                    for (int a = 0; a < size; a++) {
                        dest = model.getValidMovesList(choosen_point).get(a);
                        int score = calculatePieceValue(choosen_point.getRow(),choosen_point.getRow(),getChessPieceAt(choosen_point).getName(), dest.getRow(), dest.getCol(), getChessPieceAt(dest));
                        scores.add(score);
                    }

                    //对一个棋子遍历出它的最小得分
                    if (scores.size() == 0) {
                        max[i][j] = -1;
                    } else {
                        int min = scores.get(0);
                        int in_max = scores.get(0);
                        while (m < scores.size()) {
                            if (scores.get(m) < min) {
                                min = scores.get(m);
                            }
                            if(scores.get(m) > in_max){
                                in_max = scores.get(m);
                                min_max_index = m;
                            }
                            m++;
                            max[i][j] = min;
                        }
                    }
                }
                scores.clear();
            }
        }
        //遍历所有棋子的最小得分，取出其中最大值 对应的行棋即为最优解
        int Max = max[0][0];
        int p = 0;
        int q = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                if(max[i][j]>Max){
                    Max=max[i][j];
                    p=i;
                    q=j;
                }
            }
        }
        ChessboardPoint final_choosen = ChessboardPoint.getChessboardPoint(p,q);
        ChessboardPoint final_dest = model.getValidMovesList(final_choosen).get(min_max_index);


        if(getChessPieceAt(final_dest) == null) {
            stack_point_before.push(final_choosen);
            stack_point_after.push(final_dest);
            ChessPiece temp_eat = model.getChessPieceAt(stack_point_before.peek());
            ChessPiece temp_eaten = model.getChessPieceAt(stack_point_after.peek()); //push进去null
            eat.push(temp_eat);
            eaten.push(temp_eaten);

            stepList.add(model.recordStep(final_choosen, final_dest, currentPlayer, turnCount));
            step = new Step(final_choosen, final_dest, getChessPieceAt(final_choosen), null, currentPlayer, turnCount);
            stepList_str.add(step.toString());

            model.solveTrap(final_choosen, final_dest);
            model.moveChessPiece(final_choosen, final_dest);
            view.setChessComponentAtGrid(final_dest, view.removeChessComponentAtGrid(final_choosen));
        }
        else{
            stack_point_before.push(final_choosen);
            stack_point_after.push(final_dest);

            AnimalChessComponent temp_animal_eat = view.getAnimalChessComponent(stack_point_before.peek());
            AnimalChessComponent temp_animal_eaten = view.getAnimalChessComponent(stack_point_after.peek());
            eat_animal.push(temp_animal_eat);
            eaten_animal.push(temp_animal_eaten);
            ChessPiece temp_eat = model.getChessPieceAt(stack_point_before.peek());
            ChessPiece temp_eaten = model.getChessPieceAt(stack_point_after.peek());
            eat.push(temp_eat);
            eaten.push(temp_eaten);

            stepList.add(model.recordStep(final_choosen, final_dest, currentPlayer, turnCount));
            step = new Step(selectedPoint, final_dest, model.getChessPieceAt(stack_point_before.peek()), model.getChessPieceAt(stack_point_after.peek()), currentPlayer, turnCount);
            stepList_str.add(step.toString());

            hideValidMoves();

            model.captureChessPiece(final_choosen, final_dest);
            view.removeChessComponentAtGrid(final_dest);
            view.setChessComponentAtGrid(final_dest, view.removeChessComponentAtGrid(final_choosen));
        }
        view.repaint();

        checkWin();
        turnCount++;
        frame.getRoundNumLabel().setText(String.format("Round Number: %d", turnCount));
        swapColor();
        final_choosen = null;
    }


    private static int calculatePieceValue(int C_row, int C_col, String name1,int D_row,int D_col,ChessPiece dest_piece) {
//        分值总和：如果棋子具有较高的分值，则评估函数返回较高的分数，反之则返回较低的分数。
//        棋子位置权值：如果己方的棋子位于对手陷阱周围的位置，则该棋子的价值更高。根据斗兽棋棋盘的布局，可以为每个位置分配不同的权值。
//        棋子控制范围：如果己方控制了更多的地形，则当前局面评分高；反之则评分较低。
//        左右河岸：如果某只老虎或狮子跨越河流，则在另一岸被吃掉的可能性就大大增加了，因此处在河岸上的老虎和狮子相应地会得到更高的权重。
//        棋子基本状态：游戏进程、濒死棋子等因素。
        int value = 0;
        //选中棋子等级权重 优先走等级高的棋子
        switch (name1) {
            case "Elephant":   // 象
                value += 16;
                break;
            case "Lion":   // 狮
                value += 14;
                if ((C_col == 2 || C_col == 4) && (C_row == 6 || C_row == 2)) {
                    value += 5;  // 处在河边上，增加价值权重
                }
                break;
            case "Tiger":   // 虎
                value = 12;
                if (C_col == 3 || C_col == 4) {
                    value += 5;   // 处在河边上，增加价值权重
                }
                break;
            case "Leopard":   // 豹
                value += 10;
                break;
            case "Wolf":   // 狼
                value += 8;
                break;
            case "Dog":   // 狗
                value += 6;
                break;
            case "Cat":   // 猫
                value += 4;
                break;
            case "Rat":   // 鼠
                value += 2;

                Cell[][] grid = model.getGrid();
                //鼠进入河，增加价值权重
                if (grid[D_row][D_col].getType() == GridType.RIVER) {
                    value += 10;
                    break;
                }
        }

                // 棋子位置权重
        switch (C_row) {
            case 0:
                value += 16;
                break;
            case 1:
                value += 14;
                break;
            case 2:
                value += 12;
                break;
            case 3:
                value += 10;
                break;
            case 4:
                value += 8;
                break;
            case 5:
                value += 6;
                break;
            case 6:
                value += 4;
            case 7:
                value += 2;
                break;
            case 8:
                value += 0;
                break;
        }
                    //目标位置越靠下权重更高
                if(D_row < C_row){
                    value+=30;
                }
                    //目标位置为兽穴，权重拉满
                if(D_row == 0 && D_col == 3){
                    value += 100;
                }
                    // 目标位置为对面的三个陷阱之一，增加价值权重
                if ((D_row == 0 && D_col == 2) || (D_row == 0 && D_col == 4) || (D_row == 1 && D_col == 3)) {
                    value += 50;
                }
                    //可以吃子，优先吃子
                if(dest_piece != null){
                    value += 80;
                }

        return value;
    }







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

                Thread t = new Thread(() -> {
                    try{
                        Thread.sleep(500); //milliseconds
                        ABPlay();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                });
                t.start();
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
                    Thread t = new Thread(() -> {
                        try {
                            Thread.sleep(500); //milliseconds
                            ABPlay();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    t.start();
                } else {
                    System.out.println("Illegal chess capture!");
                }
            }

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
            }
            view.repaint();
        }
    }














