package controller;
import listener.GameListener;

import model.*;
import view.*;
import view.ChessComponent.*;

import javax.swing.*;
import java.io.*;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static model.Chessboard.*;
import static model.Constant.CHESSBOARD_COL_SIZE;
import static model.Constant.CHESSBOARD_ROW_SIZE;
public class AIcontroller implements GameListener {
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

        public AIcontroller(ChessboardComponent view, Chessboard model, ChessGameFrame frame) {
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

        public void AIplay(){
            //AI只能后手
            int x,y;
            Random random1 = new Random();
            Random random2= new Random();
            ChessboardPoint choosen_point = null;
            while (true) {
                x = random1.nextInt(9);
                y = random2.nextInt(7);
                choosen_point = ChessboardPoint.getChessboardPoint(x,y);
                if(getChessPieceAt(choosen_point) != null && getChessPieceAt(choosen_point).getOwner() == currentPlayer){
                    break;
                }
            }
            Random random3 = new Random();
            int z = random3.nextInt(model.getValidMovesList(choosen_point).size());
            ChessboardPoint dest = model.getValidMovesList(choosen_point).get(z);

            if(getChessPieceAt(dest) == null) {
                stack_point_before.push(choosen_point);
                stack_point_after.push(dest);
                ChessPiece temp_eat = model.getChessPieceAt(stack_point_before.peek());
                ChessPiece temp_eaten = model.getChessPieceAt(stack_point_after.peek()); //push进去null
                eat.push(temp_eat);
                eaten.push(temp_eaten);

                stepList.add(model.recordStep(choosen_point, dest, currentPlayer, turnCount));
                step = new Step(choosen_point, dest, getChessPieceAt(choosen_point), null, currentPlayer, turnCount);
                stepList_str.add(step.toString());

                model.solveTrap(choosen_point, dest);
                model.moveChessPiece(choosen_point, dest);
                view.setChessComponentAtGrid(dest, view.removeChessComponentAtGrid(choosen_point));
            }
            else{
                stack_point_before.push(choosen_point);
                stack_point_after.push(dest);

                AnimalChessComponent temp_animal_eat = view.getAnimalChessComponent(stack_point_before.peek());
                AnimalChessComponent temp_animal_eaten = view.getAnimalChessComponent(stack_point_after.peek());
                eat_animal.push(temp_animal_eat);
                eaten_animal.push(temp_animal_eaten);
                ChessPiece temp_eat = model.getChessPieceAt(stack_point_before.peek());
                ChessPiece temp_eaten = model.getChessPieceAt(stack_point_after.peek());
                eat.push(temp_eat);
                eaten.push(temp_eaten);

                stepList.add(model.recordStep(choosen_point, dest, currentPlayer, turnCount));
                step = new Step(selectedPoint, dest, model.getChessPieceAt(stack_point_before.peek()), model.getChessPieceAt(stack_point_after.peek()), currentPlayer, turnCount);
                stepList_str.add(step.toString());

                hideValidMoves();

                model.captureChessPiece(choosen_point, dest);
                view.removeChessComponentAtGrid(dest);
                view.setChessComponentAtGrid(dest, view.removeChessComponentAtGrid(choosen_point));
            }
            view.repaint();

            checkWin();
            turnCount++;
            frame.getRoundNumLabel().setText(String.format("Round Number: %d", turnCount));
            swapColor();
            choosen_point = null;
        }

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
                        AIplay();
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
                            AIplay();
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











/*
        private static final int INFINITY = Integer.MAX_VALUE; // 定义无穷大（表示胜利或失败）
        private static final int DEPTH_LIMIT = 2; // 搜索深度
        public int[] AlphaBeta(int[][] board) {
            int alpha = -INFINITY;
            int beta = INFINITY;
            int[] result = new int[4];

            // 遍历当前玩家所有棋子的位置
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 9; j++) {
                    if (isCurrentPlayerPiece(board[i][j])) { // 判断是否为当前玩家的棋子
                        int[][] newBoard = cloneBoard(board); // 复制一个新的棋盘
                        int piece = newBoard[i][j];
                        newBoard[i][j] = 0; // 从原位置移动一个空格
                        // 遍历该棋子可以移动到的位置
                        for (int x = 0; x < 7; x++) {
                            for (int y = 0; y < 9; y++) {
                                if (isValidMove(newBoard, piece, i, j, x, y)) { // 判断该位置是否可移动
                                    int value = AlphaBetaHelper(newBoard, x, y, DEPTH_LIMIT-1, alpha, beta, false); // 对新棋盘进行alpha-beta搜索
                                    if (value > alpha) { // 更新最佳移动的值和坐标
                                        alpha = value;
                                        result[0] = i;
                                        result[1] = j;
                                        result[2] = x;
                                        result[3] = y;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return result; // 返回最佳移动的坐标
        }

        private int AlphaBetaHelper(int[][] board, int fx, int fy, int depth, int alpha, int beta, boolean maximizingPlayer) {
            if (depth == 0) { // 达到搜索深度或游戏结束
                return evaluateBoard(board); // 对棋盘进行估值
            }

            // 遍历下一步所有可能的移动
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 9; j++) {
                    if ((maximizingPlayer && isCurrentPlayerPiece(board[i][j])) || (!maximizingPlayer && isOpponentPiece(board[i][j]))) { // 确定当前玩家的棋子或对手的棋子
                        int piece = board[i][j];
                        int[][] newBoard = cloneBoard(board);
                        newBoard[i][j] = 0; // 原位置移动一个空格
                        // 遍历该棋子可以移动到的位置
                        for (int x = 0; x < 7; x++) {
                            for (int y = 0; y < 9; y++) {
                                if (isValidMove(newBoard, piece, i, j, x, y)) { // 判断该位置是否可移动
                                    int value;
                                    if (maximizingPlayer) { // 最大化搜索
                                        value = AlphaBetaHelper(newBoard, fx, fy, depth-1, alpha, beta, false);
                                        alpha = Math.max(alpha, value);
                                        if (beta <= alpha) { // beta剪枝
                                            return alpha;
                                        }
                                    } else { // 最小化搜索
                                        value = AlphaBetaHelper(newBoard, fx, fy, depth-1, alpha, beta, true);
                                        beta = Math.min(beta, value);
                                        if (beta <= alpha) { // alpha剪枝
                                            return beta;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return maximizingPlayer ? alpha : beta; // 返回最大或最小值
        }

        // 判断是否为当前玩家的棋子
        private boolean isCurrentPlayerPiece(int piece) {
            // 假设1到8表示己方棋子，-1到-8表示对手棋子，0表示无子
            return piece > 0;
        }

        // 判断是否为对手的棋子
        private boolean isOpponentPiece(int piece) {
            return piece < 0;
        }

        // 克隆棋盘
        private int[][] cloneBoard(int[][] board) {
            int[][] newBoard = new int[7][9];
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 9; j++) {
                    newBoard[i][j] = board[i][j];
                }
            }
            return newBoard;
        }

        // 判断是否为有效移动
        private boolean isValidMove(int[][] board, int piece, int i, int j, int x, int y) {
            // 判断是否越界
            if (x < 0 || x > 6 || y < 0 || y > 8 || (i == x &&



 */




/*
    private static final int MAX_DEPTH = 4; // 最大搜索深度
    private static final int INF = 1000000; // 极大值（赢局状态）
    private static final int N_INF = -INF; // 极小值（输局状态）
    public static int[] run(int[][] board) {
        int bestScore = N_INF;
        int[] bestMove = new int[4];
        int depth = 0;
        for (int i = 0; i < 7; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (board[i][j] != null && board[i][j].getOwner() == Piece.COLOR_BLACK) {
                    int[] actions = board[i][j].getValidMoves();
                    for (int k = 0; k < actions.length; ++k) {
                        int[] move = {i, j, actions[k] / 10, actions[k] % 10};
                        int score = alphaBetaSearch(board, move, MAX_DEPTH, depth, N_INF, INF, true);
                        if (score > bestScore) {
                            bestScore = score;
                            System.arraycopy(move, 0, bestMove, 0, 4);
                        }
                    }
                }
            }
        }
        return bestMove;
    }

    private static int alphaBetaSearch(int[][] board, int[] move, int maxDepth, int depth, int alpha, int beta, boolean isMax) {
        int side = isMax ? Piece.COLOR_BLACK : Piece.COLOR_RED; // 当前玩家
        if (depth >= maxDepth || isGameOver(board)) {
            return evaluateBoard(board, side);
        }
        int score;
        int bestValue = isMax ? N_INF : INF;
        Piece fromPiece = makeMove(board, move);
        int[] actions = fromPiece.getValidMoves();
        for (int i = 0; i < actions.length; ++i) {
            int[] newMove = {move[2], move[3], actions[i] / 10, actions[i] % 10};
            score = alphaBetaSearch(board, newMove, maxDepth, depth + 1, alpha, beta, !isMax);
            if ((isMax && score > bestValue) || (!isMax && score < bestValue)) {
                bestValue = score;
            }
            if (isMax) {
                alpha = Math.max(alpha, bestValue);
            } else {
                beta = Math.min(beta, bestValue);
            }
            undoMove(board, fromPiece);
            if (beta <= alpha) {
                break;
            }
        }
        return bestValue;
    }

    private static int evaluateBoard(int[][] board, int side) {
        int pieceScore = 0;
        int positionScore = 0;
        for (int i = 0; i < 7; ++i) {
            for (int j = 0; j < 9; ++j) {
                Piece p = board[i][j];
                if (p == null) {
                    continue;
                }
                if (p.getColor() == side) {
                    pieceScore += p.getRank().getValue();
                } else {
                    pieceScore -= p.getRank().getValue();
                }
                int x = Math.abs(p.getX() - 3);
                int y = p.getY();
                if (y < 3 || y > 5) {
                    positionScore -= (x + 1) * (x + 1);
                } else {
                    positionScore += (x + 1) * (x + 1);
                }
            }
        }
        return pieceScore + positionScore;
    }

    private static Piece makeMove(int[][] board, int[] move) {
        int fromX = move[0];
        int fromY = move[1];
        int toX = move[2];
        int toY = move[3];
        Piece fromPiece = board[fromX][fromY];
        Piece toPiece = board[toX][toY];
        if (toPiece != null && toPiece.getRank() == Rank.RAT) {
            toPiece.setDead(true);
        }
        board[fromX][fromY] = null;
        board[toX][toY] = fromPiece;
        fromPiece.setX(toX);
        fromPiece.setY(toY);
        return fromPiece;
    }

 */



