package model;


import view.ChessboardComponent;
import view.GridType;

import java.util.*;


import static view.ChessboardComponent.removeChessComponentAtGrid;
import static view.GridType.RIVER;

/**
 * This class store the real chess information.
 * The Chessboard has 9*7 cells, and each cell has a position for chess
 */
public class Chessboard {
    private static Cell[][] grid;
    private final Set<ChessboardPoint> riverCell = new HashSet<>();
    private final Set<ChessboardPoint> trapCell = new HashSet<>();
    private final Set<ChessboardPoint> denCell = new HashSet<>();

    public Chessboard() {
        this.grid =
                new Cell[Constant.CHESSBOARD_ROW_SIZE.getNum()][Constant.CHESSBOARD_COL_SIZE.getNum()];//19X19
        initSet();
        initGrid();
        initPieces();
    }
    private void initSet() {//定义三种类型cell位置
        riverCell.add(new ChessboardPoint(3,1));
        riverCell.add(new ChessboardPoint(3,2));
        riverCell.add(new ChessboardPoint(4,1));
        riverCell.add(new ChessboardPoint(4,2));
        riverCell.add(new ChessboardPoint(5,1));
        riverCell.add(new ChessboardPoint(5,2));

        riverCell.add(new ChessboardPoint(3,4));
        riverCell.add(new ChessboardPoint(3,5));
        riverCell.add(new ChessboardPoint(4,4));
        riverCell.add(new ChessboardPoint(4,5));
        riverCell.add(new ChessboardPoint(5,4));
        riverCell.add(new ChessboardPoint(5,5));

        trapCell.add(new ChessboardPoint(0,2));
        trapCell.add(new ChessboardPoint(0,4));
        trapCell.add(new ChessboardPoint(1,3));
        trapCell.add(new ChessboardPoint(7,3));
        trapCell.add(new ChessboardPoint(8,2));
        trapCell.add(new ChessboardPoint(8,4));

        denCell.add(new ChessboardPoint(0,3));
        denCell.add(new ChessboardPoint(8,3));
    }

    private void initGrid() {//利用遍历对确定类型的位置放Cell
        for (int i = 0; i < Constant.CHESSBOARD_ROW_SIZE.getNum(); i++) {
            for (int j = 0; j < Constant.CHESSBOARD_COL_SIZE.getNum(); j++) {
                if (riverCell.contains(new ChessboardPoint(i, j))) {
                    grid[i][j] = new Cell(RIVER);
                } else if (trapCell.contains(new ChessboardPoint(i, j))) {
                    grid[i][j] = new Cell(GridType.TRAP);
                    if (i < 2) {
                        grid[i][j].setOwner(PlayerColor.RED);
                    } else {
                        grid[i][j].setOwner(PlayerColor.BLUE);
                    }
                } else if (denCell.contains(new ChessboardPoint(i, j))) {
                    grid[i][j] = new Cell(GridType.DENS);
                } else {
                    grid[i][j] = new Cell(GridType.LAND);
                }
            }
        }
    }

    public void initPieces() {
        grid[2][6].setPiece(new ChessPiece(PlayerColor.BLUE, "Elephant",8));
        grid[6][0].setPiece(new ChessPiece(PlayerColor.RED, "Elephant",8));
        grid[0][0].setPiece(new ChessPiece(PlayerColor.BLUE, "Lion",7));
        grid[8][6].setPiece(new ChessPiece(PlayerColor.RED, "Lion",7));
        grid[0][6].setPiece(new ChessPiece(PlayerColor.BLUE, "Tiger",6));
        grid[8][0].setPiece(new ChessPiece(PlayerColor.RED, "Tiger",6));
        grid[2][2].setPiece(new ChessPiece(PlayerColor.BLUE, "Leopard",5));
        grid[6][4].setPiece(new ChessPiece(PlayerColor.RED, "Leopard",5));
        grid[2][4].setPiece(new ChessPiece(PlayerColor.BLUE, "Wolf",4));
        grid[6][2].setPiece(new ChessPiece(PlayerColor.RED, "Wolf",4));
        grid[1][1].setPiece(new ChessPiece(PlayerColor.BLUE, "Dog",3));
        grid[7][5].setPiece(new ChessPiece(PlayerColor.RED, "Dog",3));
        grid[1][5].setPiece(new ChessPiece(PlayerColor.BLUE, "Cat",2));
        grid[7][1].setPiece(new ChessPiece(PlayerColor.RED, "Cat",2));
        grid[2][0].setPiece(new ChessPiece(PlayerColor.BLUE, "Rat",1));
        grid[6][6].setPiece(new ChessPiece(PlayerColor.RED, "Rat",1));
    }


    public static ChessPiece getChessPieceAt(ChessboardPoint point) {
        return getGridAt(point).getPiece();
    }

    public static Cell getGridAt(ChessboardPoint point) {
        return grid[point.getRow()][point.getCol()];
    }

    private int calculateDistance(ChessboardPoint src, ChessboardPoint dest) {
        return Math.abs(src.getRow() - dest.getRow()) + Math.abs(src.getCol() - dest.getCol());
    }



    private ChessPiece removeChessPiece(ChessboardPoint point) { //移除棋子
        ChessPiece chessPiece = getChessPieceAt(point);
        getGridAt(point).removePiece();
        return chessPiece;
    }

    public static void setChessPiece(ChessboardPoint point, ChessPiece chessPiece) { //放置棋子
        getGridAt(point).setPiece(chessPiece);
    }

    public void moveChessPiece(ChessboardPoint src, ChessboardPoint dest) {
        if (!isValidMove(src, dest)) {
            throw new IllegalArgumentException("Illegal chess move!");
        }
        setChessPiece(dest, removeChessPiece(src));
    }

    public void captureChessPiece(ChessboardPoint src, ChessboardPoint dest) {
        removeChessPiece(dest);
        setChessPiece(dest,removeChessPiece(src));
    }



    public Cell[][] getGrid() {
        return grid;
    }
    public PlayerColor getChessPieceOwner(ChessboardPoint point) {
        return getGridAt(point).getPiece().getOwner();
    }

    public boolean isValidMove(ChessboardPoint src, ChessboardPoint dest) {
        if (getChessPieceAt(src) == null || getChessPieceAt(dest) != null) {
            return false;
        }
        //老鼠可以游泳
        if (getGridAt(dest).getType()== RIVER){
            if (!Objects.equals(getChessPieceAt(src).getName(), "Rat")){
                return false;
            }else{
                return calculateDistance(src,dest)==1;
            }
        }
        //狮子和老虎可以跳过河
        if (calculateDistance(src,dest)>1&&(Objects.equals(getChessPieceAt(src).getName(), "Lion"))||(Objects.equals(getChessPieceAt(src).getName(), "Tiger"))) {
            //不能窜行跳跃
            if (src.getRow() != dest.getRow() && src.getCol() != dest.getCol()) {
                return false;
            }
            //两点之间全是河

            // 检查两个格子之间是否全为RIVER，如果是，则可以移动，否则不可以移动

            //源位置和目标位置在棋盘上同一列的情况下，棋子在该列上是向左移动还是向右移动。如果源位置所在列小于目标位置所在列，那么棋子就向右移动，否则就向左移动。这里使用了一个三目运算符，如果条件成立则返回1，否则返回-1。最终得到的step值即为1或-1。
            //行一致
            if (src.getRow() == dest.getRow()) {
                int moveCol = 1;
                if (src.getCol() < dest.getCol()) {
                    ;
                } else if (src.getCol() > dest.getCol()) {
                    moveCol = -1;
                }

                int col = src.getCol() + moveCol;
                while (col != dest.getCol()) {
                    if (getGridAt(new ChessboardPoint(src.getRow(), col)).getType() != RIVER) {
                        return false;
                    }
                    if (getChessPieceAt(new ChessboardPoint(src.getRow(), col)) != null) {
                        return false;
                    }
                    col = col + moveCol;
                }
                return true;
            }
            //列一致
            if (src.getCol() == dest.getCol()) {
                int moveRow = 1;
                if (src.getRow() < dest.getRow()) {
                    ;
                } else if (src.getRow() > dest.getRow()) {
                    moveRow = -1;
                }
                int row = src.getRow() + moveRow;
                while (row != dest.getRow()) {
                    if (getGridAt(new ChessboardPoint(row, src.getCol())).getType() != RIVER)
                        return false;
                    if (getChessPieceAt(new ChessboardPoint(row, src.getCol())) != null) {
                        return false;
                    }
                    row = row + moveRow;
                }
                return true;
            }
        }
        // 不能走到自己的巢穴里
        if (getGridAt(dest).getType() == GridType.DENS && getChessPieceAt(src).getOwner()==PlayerColor.RED&&dest.getRow()==8&&dest.getCol()==3) {
            //System.out.println("Attention! You cannot enter your own den!");
            return false;
        }
        if (getGridAt(dest).getType() == GridType.DENS && getChessPieceAt(src).getOwner()==PlayerColor.BLUE&&dest.getRow()==0&&dest.getCol()==3) {
            //System.out.println("Attention! You cannot enter your own den!");
            return false;
        }

        solveTrap(src, dest);
        return calculateDistance(src, dest) == 1;
    }

    public boolean isValidCapture(ChessboardPoint src, ChessboardPoint dest) {
        ChessPiece srcPiece = getChessPieceAt(src);
        ChessPiece destPiece = getChessPieceAt(dest);
        //压根没有棋子的位置无法操作
        if (srcPiece == null || destPiece == null) {
            return false;
        }
        //不能自己吃自己
        if (srcPiece.getOwner() == destPiece.getOwner()) {
            return false;
        }
        //河里不能吃，也不能在河里被吃
        if (getGridAt(dest).getType() == GridType.RIVER) {
            return false;
        }
        if (getGridAt(src).getType() == GridType.RIVER) {
            return false;
        }
        // 狮子和虎可以跳过河来捕获
        if (calculateDistance(src, dest) > 1 && (Objects.equals(srcPiece.getName(), "Lion") || Objects.equals(srcPiece.getName(), "Tiger"))) {
            // 检查两个格子是否在同一行或者同一列
            if (src.getRow() != dest.getRow() && src.getCol() != dest.getCol()) {
                return false;
            }
            // 检查两个格子之间是否全为RIVER，如果是，则可以移动，否则不可以移动
            if (src.getRow() == dest.getRow()) {
                int moveCol = 1;
                if (src.getCol() < dest.getCol()) {
                    ;
                } else if (src.getCol() > dest.getCol()) {
                    moveCol = -1;
                }

                int col = src.getCol() + moveCol;
                while (col != dest.getCol()) {
                    if (getGridAt(new ChessboardPoint(src.getRow(), col)).getType() != RIVER) {
                        return false;
                    }
                    if (getChessPieceAt(new ChessboardPoint(src.getRow(), col)) != null) {
                        return false;
                    }
                    col = col + moveCol;
                }
                if (getChessPieceAt(dest).getRank()>getChessPieceAt(src).getRank()){
                    return false;
                }
                return true;
            }
            //列一致
            if (src.getCol() == dest.getCol()) {
                int moveRow = 1;
                if (src.getRow() < dest.getRow()) {
                    ;
                } else if (src.getRow() > dest.getRow()) {
                    moveRow = -1;
                }
                int row = src.getRow() + moveRow;
                while (row != dest.getRow()) {
                    if (getGridAt(new ChessboardPoint(row, src.getCol())).getType() != RIVER)
                        return false;
                    if (getChessPieceAt(new ChessboardPoint(row, src.getCol())) != null) {
                        return false;
                    }
                    row = row + moveRow;
                }
                if (getChessPieceAt(dest).getRank()>getChessPieceAt(src).getRank()){
                    return false;
                }
                return true;
            }
        }
        solveTrap(src, dest);
        return calculateDistance(src, dest) == 1 && srcPiece.canCapture(destPiece);
    }


    //  定义了一个函数 getTrapped，它接受一个 ChessboardPoint 类型的参数 point。
    //  函数内部调用了一个 getGridAt 函数，这个函数返回指定位置的棋子或者棋格。
    //  然后从这个棋格中获取棋子并将其等级设置为0。
    //  这段代码可能是实现一个围住对方棋子的功能，将对方棋子的等级降为0表示将其困住。

    public void solveTrap(ChessboardPoint selectedPoint, ChessboardPoint destPoint) {
        if (getGridAt(destPoint).getType() == GridType.TRAP) {
            if (getGridAt(destPoint) == grid[0][2] || getGridAt(destPoint) == grid[0][4] || getGridAt(destPoint) == grid[1][3]) {
                if (getChessPieceAt(selectedPoint).getOwner() == PlayerColor.RED) {
                    getTrapped(selectedPoint);
                }
            } else if (getGridAt(destPoint) == grid[8][2] || getGridAt(destPoint) == grid[8][4] || getGridAt(destPoint) == grid[7][3]) {
                if (getChessPieceAt(selectedPoint).getOwner() == PlayerColor.BLUE) {
                    getTrapped(selectedPoint);
                }
            }
            //进入陷阱
        } else if (getGridAt(selectedPoint).getType() == GridType.TRAP) {
            if (getGridAt(selectedPoint) == grid[0][2] || getGridAt(selectedPoint) == grid[0][4] || getGridAt(selectedPoint) == grid[1][3]
                    || getGridAt(selectedPoint) == grid[8][2] || getGridAt(selectedPoint) == grid[8][4] || getGridAt(selectedPoint) == grid[7][3]) {
                exitTrap(selectedPoint);  //逃出陷阱
            }
        }
    }

    public void getTrapped(ChessboardPoint point) {
        getChessPieceAt(point).setRank(0);
    }

    public void exitTrap(ChessboardPoint point) {
        switch (getGridAt(point).getPiece().getName()) {
            case "Rat":
                getGridAt(point).getPiece().setRank(1);
                break;
            case "Cat":
                getGridAt(point).getPiece().setRank(2);
                break;
            case "Dog":
                getGridAt(point).getPiece().setRank(3);
                break;
            case "Wolf":
                getGridAt(point).getPiece().setRank(4);
                break;
            case "Leopard":
                getGridAt(point).getPiece().setRank(5);
                break;
            case "Tiger":
                getGridAt(point).getPiece().setRank(6);
                break;
            case "Lion":
                getGridAt(point).getPiece().setRank(7);
                break;
            case "Elephant":
                getGridAt(point).getPiece().setRank(8);
                break;
        }
    }



/*
    public boolean checkAnnihilate(PlayerColor currentPlayer){
        // 检查是否还有敌方棋子
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                ChessboardPoint point = new ChessboardPoint(i, j);
                if (getChessPieceAt(point) != null && getChessPieceAt(point).getOwner() != currentPlayer) {
                    //这个条件是敌方有棋子
                    //现在锁定的point就是敌方的棋子

                    if (point.getRow()-1>=0) {//锁定的这个敌方棋子左边有棋子
                        ChessboardPoint a = new ChessboardPoint(point.getRow() - 1, point.getCol());//这个敌方棋子左边的棋子
                        if (getChessPieceAt(a)==null){
                            ;
                        }else if (getChessPieceAt(a).getOwner()==currentPlayer&&getChessPieceAt(a).getRank()>=getChessPieceAt(point).getRank()){//能吃敌方棋子
                            continue;
                        }
                    }
                    if (point.getRow()+1>=0) {//锁定的这个敌方棋子右边有棋子
                        ChessboardPoint a = new ChessboardPoint(point.getRow()+1, point.getCol());//这个敌方棋子右边的棋子
                        if (getChessPieceAt(a)==null){
                            ;
                        }else if (getChessPieceAt(a).getOwner()==currentPlayer&&getChessPieceAt(a).getRank()>=getChessPieceAt(point).getRank()){//能吃敌方棋子
                            continue;
                        }
                    }
                    if (point.getCol()-1>=0) {//锁定的这个敌方棋子左边有棋子
                        ChessboardPoint a = new ChessboardPoint(point.getRow(), point.getCol()-1);//这个敌方棋子上边的棋子
                        if (getChessPieceAt(a)==null){
                            ;
                        }else if (getChessPieceAt(a).getOwner()==currentPlayer&&getChessPieceAt(a).getRank()>=getChessPieceAt(point).getRank()){//能吃敌方棋子
                            continue;
                        }
                    }
                    if (point.getCol()+1>=0) {//锁定的这个敌方棋子左边有棋子
                        ChessboardPoint a = new ChessboardPoint(point.getRow(), point.getCol()+1);//这个敌方棋子左边的棋子
                        if (getChessPieceAt(a)==null){
                            ;
                        }else if (getChessPieceAt(a).getOwner()==currentPlayer&&getChessPieceAt(a).getRank()>=getChessPieceAt(point).getRank()){//能吃敌方棋子
                            continue;
                        }
                    }
                    return false;
                }
            }
        }
        //到这是：敌方没有棋子了！
        System.out.println("Annihilate!");
        return true;
    }

 */
public boolean checkOpponentNone(PlayerColor currentPlayer) {
    for (int i = 0; i < 9; i++) {
        for (int j = 0; j < 7; j++) {
            ChessboardPoint point = new ChessboardPoint(i, j);
            if (getChessPieceAt(point) != null && getChessPieceAt(point).getOwner() != currentPlayer) {
                return false;
            }
        }
    }
    return true;
}

    public  PlayerColor checkDensWin(){
        if (getChessPieceAt(new ChessboardPoint(0,3))!=null){
            return PlayerColor.RED;
        }
        if (getChessPieceAt(new ChessboardPoint(8,3))!=null){
            return PlayerColor.BLUE;
        }
        return null;
    }
    //这个方法接受当前玩家的颜色作为参数，然后在棋盘上检查是否还有敌方棋子，如果有则返回 false，否则返回 true。
    // 为了做到这一点，方法使用嵌套的 for 循环来遍历棋盘中的每个点，调用 getChessPieceAt(point) 方法获取该点上的棋子，并检查它的所有者是否与当前玩家相同。
    // 如果找到了敌方棋子，则返回 false，从而表示还有棋子；
    // 如果所有点都被遍历且没有找到敌方棋子，则返回 true。



    //如果检测到可以走或可以吃，则将该位置保存到一个List集合availablePoints中，最后将所有可用的位置返回。
    public List<ChessboardPoint> getValidMovesList(ChessboardPoint point) {
        List<ChessboardPoint> availablePoints = new ArrayList<>();
        // 检查整张棋盘，用isValidMove()方法检查每个格子是否可以移动到，同时也用isValidCapture()方法检查每个格子是否可以吃掉
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                ChessboardPoint destPoint = new ChessboardPoint(i, j);
                if (isValidMove(point, destPoint) || isValidCapture(point, destPoint)) {
                    availablePoints.add(destPoint);
                }
            }
        }
        return availablePoints;
    }


    public Step recordStep(ChessboardPoint fromPoint, ChessboardPoint toPoint, PlayerColor currentPlayer, int turn){
        ChessPiece fromPiece = getChessPieceAt(fromPoint);
        ChessPiece toPiece = getChessPieceAt(toPoint);
        Step step=new Step(fromPoint, toPoint, fromPiece, toPiece, currentPlayer, turn);
        return step;
    }






}
