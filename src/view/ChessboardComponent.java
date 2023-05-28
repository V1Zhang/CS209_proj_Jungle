package view;
import controller.AI;
import controller.AIcontroller;
import controller.AlphaBetaController;
import controller.GameController;
import model.*;
import view.ChessComponent.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import static model.Chessboard.getChessPieceAt;
import static model.Constant.CHESSBOARD_COL_SIZE;
import static model.Constant.CHESSBOARD_ROW_SIZE;

/**
 * This class represents the checkerboard component object on the panel
 */
public class ChessboardComponent extends JComponent {
    private static final CellComponent[][] gridComponents = new CellComponent[CHESSBOARD_ROW_SIZE.getNum()][CHESSBOARD_COL_SIZE.getNum()];
    private final int CHESS_SIZE;
    private final Set<ChessboardPoint> riverCell = new HashSet<>();
    private final Set<ChessboardPoint> trapCell = new HashSet<>();
    private final Set<ChessboardPoint> denCell = new HashSet<>();
    public static final Color light_green = new Color(160, 222, 153);
    public static final Color LIGHT_GREEN = light_green;
    public static final Color brown = new Color(61,6,3);
    public static final Color BROWN = brown;
    public static final Color light_blue = new Color(147,209,240);
    public static final Color Light_BLUE = light_blue;

    /**
     * The color dark gray.  In the default sRGB space.
     * @since 1.4
     */
    public GameController gameController;
    public AIcontroller aIcontroller;
    public AlphaBetaController abcontroller;
    public ChessboardComponent(int chessSize) {
        CHESS_SIZE = chessSize;
        int width = CHESS_SIZE * 7;
        int height = CHESS_SIZE * 9;
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);// Allow mouse events to occur
        setLayout(null); // Use absolute layout.
        setSize(width, height);
        System.out.printf("chessboard width, height = [%d : %d], chess size = %d\n", width, height, CHESS_SIZE);

        initiateGridComponents();
    }


    /**
     * This method represents how to initiate ChessComponent
     * according to Chessboard information
     */
    public void initiateChessComponent(Chessboard chessboard) { //初始棋子
        Cell[][] grid = chessboard.getGrid();
        for (int i = 0; i < CHESSBOARD_ROW_SIZE.getNum(); i++) {
            for (int j = 0; j < CHESSBOARD_COL_SIZE.getNum(); j++) {
                if (grid[i][j].getPiece() != null) {
                    ChessPiece chessPiece = grid[i][j].getPiece();
                    System.out.println(chessPiece.getOwner());
                    if (chessPiece.getName().equals("Elephant")) {
                        gridComponents[i][j].add(new ElephantChessComponent(chessPiece.getOwner(), CHESS_SIZE));
                    }
                    if (chessPiece.getName().equals("Lion")) {
                        gridComponents[i][j].add(new LionChessComponent(chessPiece.getOwner(), CHESS_SIZE));
                    }
                    if (chessPiece.getName().equals("Tiger")) {
                        gridComponents[i][j].add(new TigerChessComponent(chessPiece.getOwner(), CHESS_SIZE));
                    }
                    if (chessPiece.getName().equals("Leopard")) {
                        gridComponents[i][j].add(new LeopardChessComponent(chessPiece.getOwner(), CHESS_SIZE));
                    }
                    if (chessPiece.getName().equals("Wolf")) {
                        gridComponents[i][j].add(new WolfChessComponent(chessPiece.getOwner(), CHESS_SIZE));
                    }
                    if (chessPiece.getName().equals("Dog")) {
                        gridComponents[i][j].add(new DogChessComponent(chessPiece.getOwner(), CHESS_SIZE));
                    }
                    if (chessPiece.getName().equals("Cat")) {
                        gridComponents[i][j].add(new CatChessComponent(chessPiece.getOwner(), CHESS_SIZE));
                    }
                    if (chessPiece.getName().equals("Rat")) {
                        gridComponents[i][j].add(new RatChessComponent(chessPiece.getOwner(), CHESS_SIZE));
                    }
                }
            }
        }

    }

    public void initiateGridComponents() { //初始棋盘格
        //左
        riverCell.add(new ChessboardPoint(3,1));
        riverCell.add(new ChessboardPoint(3,2));
        riverCell.add(new ChessboardPoint(4,1));
        riverCell.add(new ChessboardPoint(4,2));
        riverCell.add(new ChessboardPoint(5,1));
        riverCell.add(new ChessboardPoint(5,2));
        //右
        riverCell.add(new ChessboardPoint(3,4));
        riverCell.add(new ChessboardPoint(3,5));
        riverCell.add(new ChessboardPoint(4,4));
        riverCell.add(new ChessboardPoint(4,5));
        riverCell.add(new ChessboardPoint(5,4));
        riverCell.add(new ChessboardPoint(5,5));
        //上
        trapCell.add(new ChessboardPoint(0,2));
        trapCell.add(new ChessboardPoint(0,4));
        trapCell.add(new ChessboardPoint(1,3));
        //下
        trapCell.add(new ChessboardPoint(8,2));
        trapCell.add(new ChessboardPoint(8,4));
        trapCell.add(new ChessboardPoint(7,3));

        denCell.add(new ChessboardPoint(0,3));
        denCell.add(new ChessboardPoint(8,3));

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                ChessboardPoint temp = new ChessboardPoint(i, j);
                CellComponent cell;
                if (riverCell.contains(temp)) {
                    cell = new CellComponent(Light_BLUE, calculatePoint(i, j), CHESS_SIZE);
                    this.add(cell);
                } else if (trapCell.contains(temp)) {
                    cell = new CellComponent(LIGHT_GREEN,calculatePoint(i,j),CHESS_SIZE);
                    this.add(cell);
                }else if (denCell.contains((temp))) {
                    cell = new CellComponent(BROWN,calculatePoint(i,j),CHESS_SIZE);
                    this.add(cell);
                }  else {
                    cell = new CellComponent(Color.LIGHT_GRAY, calculatePoint(i, j), CHESS_SIZE);
                    this.add(cell);
                }
                gridComponents[i][j] = cell;
            }
        }
    }

    public void registerController(GameController gameController) {
        this.gameController = gameController;
    }
    public void registerController(AIcontroller controller) {
        this.aIcontroller = controller;
    }
    public void registerController(AlphaBetaController controller) {
        this.abcontroller = controller;
    }

    public void setChessComponentAtGrid(ChessboardPoint point, AnimalChessComponent chess) {
        getGridComponentAt(point).add(chess);
    }

    public static AnimalChessComponent removeChessComponentAtGrid(ChessboardPoint point) throws ArrayIndexOutOfBoundsException{
        // Note re-validation is required after remove / removeAll.
        try {
            AnimalChessComponent chess = (AnimalChessComponent) getGridComponentAt(point).getComponents()[0];
            getGridComponentAt(point).removeAll();
            getGridComponentAt(point).revalidate();
            chess.setSelected(false);
            return chess;
        }catch (ArrayIndexOutOfBoundsException e){
            return null;
        }
    }
    public static AnimalChessComponent getAnimalChessComponent(ChessboardPoint point) throws ArrayIndexOutOfBoundsException{
        try {
            AnimalChessComponent chess = (AnimalChessComponent) getGridComponentAt(point).getComponents()[0];
            return chess;
        }catch(ArrayIndexOutOfBoundsException e){
            return null;
        }
    }

    public static CellComponent getGridComponentAt(ChessboardPoint point) {
        return gridComponents[point.getRow()][point.getCol()];
    }

    public ChessboardPoint getChessboardPoint(Point point) {
        System.out.println("[" + point.y/CHESS_SIZE +  ", " +point.x/CHESS_SIZE + "] Clicked");
        return new ChessboardPoint(point.y/CHESS_SIZE, point.x/CHESS_SIZE);
    }
    private Point calculatePoint(int row, int col) {
        return new Point(col * CHESS_SIZE, row * CHESS_SIZE);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            //getAnimalChessComponent(getChessboardPoint(e.getPoint())).setSelected(true);
            JComponent clickedComponent = (JComponent) getComponentAt(e.getX(), e.getY());
            if (AI.mode == false) {
                if (clickedComponent.getComponentCount() == 0) {
                    System.out.print("None chess here and ");
                    gameController.onPlayerClickCell(getChessboardPoint(e.getPoint()), (CellComponent) clickedComponent);
                } else {
                    System.out.print("One chess here and ");
                    gameController.onPlayerClickChessPiece(getChessboardPoint(e.getPoint()), (AnimalChessComponent) clickedComponent.getComponents()[0]);
                }
            } else {
                if (AI.difficulty == 1) {
                    if (clickedComponent.getComponentCount() == 0) {
                        System.out.print("None chess here and ");
                        aIcontroller.onPlayerClickCell(getChessboardPoint(e.getPoint()), (CellComponent) clickedComponent);
                    } else {
                        System.out.print("One chess here and ");
                        aIcontroller.onPlayerClickChessPiece(getChessboardPoint(e.getPoint()), (AnimalChessComponent) clickedComponent.getComponents()[0]);
                    }
                } else if (AI.difficulty == 2) {
                    if (clickedComponent.getComponentCount() == 0) {
                        System.out.print("None chess here and ");
                        abcontroller.onPlayerClickCell(getChessboardPoint(e.getPoint()), (CellComponent) clickedComponent);
                    } else {
                        System.out.print("One chess here and ");
                        abcontroller.onPlayerClickChessPiece(getChessboardPoint(e.getPoint()), (AnimalChessComponent) clickedComponent.getComponents()[0]);
                    }
                }
            }
        }
    }


    public void showValidMoves(List<ChessboardPoint> validMoves) {
        for (ChessboardPoint validMove : validMoves) {
            Graphics2D g2d = (Graphics2D) getGridComponentAt(validMove).getGraphics();
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(5.0f));
            int radius = 30; // 设置圆形半径
            int centerX = getGridComponentAt(validMove).getWidth() / 2; //计算中心点x坐标
            int centerY = getGridComponentAt(validMove).getHeight() / 2; //计算中心点y坐标
            g2d.translate(centerX, centerY);
            g2d.drawOval(-radius / 2, -radius / 2, radius, radius);
        }
    }
/*
    public void showValidMoves(List<ChessboardPoint> validMoves) {
        for (ChessboardPoint validMove : validMoves) {
            CellComponent cellComponent = new CellComponent(Light_PINK, calculatePoint(validMove.getRow(), validMove.getCol()), CHESS_SIZE);
            this.add(cellComponent);
            gridComponents[validMove.getRow()][validMove.getCol()] = cellComponent;
            //CellComponent cellComponent = getGridComponentAt(validMove);
            cellComponent.setValidMove(true);
            paintImmediately(this.getBounds());
            repaint();
        }
    }

 */
    public void hideValidMoves(List<ChessboardPoint> validMoves) throws ConcurrentModificationException {
        //多线程并发修改异常
        try {
            for (ChessboardPoint validMove : validMoves) {
                CellComponent cellComponent = getGridComponentAt(validMove);
                cellComponent.setValidMove(false);
                validMoves.clear();
//            System.out.println("hide valid move" + validMove);
            }
        }catch (ConcurrentModificationException e){
        }

    }



    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
    public void setAIController(AIcontroller aIController) {
        this.aIcontroller = aIController;
    }
    public void setAlphaBetaController(AlphaBetaController abController) {
        this.abcontroller = abController;
    }
    public GameController getGameController(){
        return gameController;
    }

    public AIcontroller aIcontroller() {return aIcontroller;}

    public AlphaBetaController abcontroller() {return abcontroller;}
}
