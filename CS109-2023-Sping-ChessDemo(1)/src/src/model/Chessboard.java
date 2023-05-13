package model;

import view.GridType;

import java.util.*;

import static view.GridType.RIVER;

/**
 * This class store the real chess information.
 * The Chessboard has 9*7 cells, and each cell has a position for chess
 */
public class Chessboard {
    private Cell[][] grid;
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

    private void initSet() {
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

    private void initGrid() {
        for (int i = 0; i < Constant.CHESSBOARD_ROW_SIZE.getNum(); i++) {
            for (int j = 0; j < Constant.CHESSBOARD_COL_SIZE.getNum(); j++) {
                if(riverCell.contains(new ChessboardPoint(i,j))) {
                    grid[i][j] = new Cell(RIVER);
                } else if (trapCell.contains(new ChessboardPoint(i,j))) {
                    grid[i][j]=new Cell(GridType.TRAP);
                    if (i<2){
                        grid[i][j].setOwner(PlayerColor.RED);
                    }else{
                        grid[i][j].setOwner(PlayerColor.BLUE);
                    }
                }else if(denCell.contains(new ChessboardPoint(i,j))){
                    grid[i][j]=new Cell(GridType.DENS);
                }else {
                    grid[i][j]= new Cell(GridType.LAND);
                }
            }
        }
    }

    private void initPieces() {
        //清空棋盘
        for (int i = 0; i < Constant.CHESSBOARD_ROW_SIZE.getNum(); i++) {
            for (int j = 0; j < Constant.CHESSBOARD_COL_SIZE.getNum(); j++) {
                grid[i][j].removePiece();
            }
        }
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

    private ChessPiece getChessPieceAt(ChessboardPoint point) {
        return getGridAt(point).getPiece();
    }

    private Cell getGridAt(ChessboardPoint point) {
        return grid[point.getRow()][point.getCol()];
    }

    private int calculateDistance(ChessboardPoint src, ChessboardPoint dest) {
        return Math.abs(src.getRow() - dest.getRow()) + Math.abs(src.getCol() - dest.getCol());
    }

    private ChessPiece removeChessPiece(ChessboardPoint point) {
        ChessPiece chessPiece = getChessPieceAt(point);
        getGridAt(point).removePiece();
        return chessPiece;
    }

    private void setChessPiece(ChessboardPoint point, ChessPiece chessPiece) {
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
        if (calculateDistance(src,dest)>1&&(Objects.equals(getChessPieceAt(src).getName(), "Lion"))||(Objects.equals(getChessPieceAt(src).getName(), "Tiger"))){
           //不能窜行跳跃
            if (src.getRow()!= dest.getRow()&&src.getCol()!=dest.getCol()){
                return false;
            }
            //两点之间全是河

            // 检查两个格子之间是否全为RIVER，如果是，则可以移动，否则不可以移动

                //源位置和目标位置在棋盘上同一列的情况下，棋子在该列上是向左移动还是向右移动。如果源位置所在列小于目标位置所在列，那么棋子就向右移动，否则就向左移动。这里使用了一个三目运算符，如果条件成立则返回1，否则返回-1。最终得到的step值即为1或-1。
               //行一致
                if (src.getRow()== dest.getRow()) {
                    int step=0;
                    int length=0;
                    if (src.getCol() < dest.getCol()) {
                        step = 1;
                        length= dest.getCol()- src.getCol();
                    } else if (src.getCol() > dest.getCol()) {
                        step = -1;
                        length= -dest.getCol()+src.getCol();
                    }
                    int col= src.getCol();
                    for (int t=1;t<length;t++){
                        col= col +t*step;
                        if (getGridAt(new ChessboardPoint(src.getRow(),col)).getType()!=RIVER){
                            return false;
                        }
                        if (getChessPieceAt(new ChessboardPoint(src.getRow(),col))!=null){
                            return false;
                        }
                        break;
                    }
                    return true;
                }
            //列一致
            if (src.getCol()== dest.getCol()) {
                int step2=1;
                int length2=0;
                if (src.getRow() < dest.getRow()) {
                    length2= dest.getRow()- src.getRow();
                } else if (src.getRow() > dest.getRow()) {
                    step2 =-1;
                    length2= -dest.getRow()+src.getRow();
                }
                int row= src.getRow();
                for (int t=1;t<length2;t++){
                    row= row+t*step2;
                    if (getGridAt(new ChessboardPoint(row, src.getCol())).getType()!=RIVER)
                        return false;
                    if (getChessPieceAt(new ChessboardPoint(row,src.getCol()))!=null){
                        return false;
                    }
                    break;
                }
            }
            return true;
        }


        // 不能走到自己的巢穴里
        if (getGridAt(dest).getType() == GridType.DENS && getGridAt(dest).getOwner() == getChessPieceAt(src).getOwner()) {
            return false;
        }

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
                int step=0;
                int length=0;
                if (src.getCol() < dest.getCol()) {
                    step = 1;
                    length= dest.getCol()- src.getCol();
                } else if (src.getCol() > dest.getCol()) {
                    step = -1;
                    length= -dest.getCol()+src.getCol();
                }
                int col= src.getCol();
                for (int t=1;t<length;t++){
                    col= col+t*step;
                    if (getGridAt(new ChessboardPoint(src.getRow(),col)).getType()!=RIVER){
                        return false;
                    }
                    if (getChessPieceAt(new ChessboardPoint(src.getRow(),col))!=null){
                        return false;
                    }
                    break;
                }
                return srcPiece.canCapture(destPiece);
            }
            //列一致
            if (src.getCol()== dest.getCol()) {
                int step2=0;
                int length2=0;
                if (src.getRow() < dest.getRow()) {
                    step2 = 1;
                    length2= dest.getRow()- src.getRow();
                } else if (src.getRow() > dest.getRow()) {
                    step2 = -1;
                    length2= -dest.getRow()+src.getRow();
                }
                int row= src.getRow();
                for (int t=1;t<length2;t++){
                    row= row+t*step2;
                    if (getGridAt(new ChessboardPoint(row, src.getCol())).getType()!=RIVER){
                        return false;
                    }
                    if (getChessPieceAt(new ChessboardPoint(row,src.getCol()))!=null){
                        return false;
                    }
                    break;
                }
                return srcPiece.canCapture(destPiece);
            }
        }
        return calculateDistance(src, dest) == 1 && srcPiece.canCapture(destPiece);
    }


  //  定义了一个函数 getTrapped，它接受一个 ChessboardPoint 类型的参数 point。
  //  函数内部调用了一个 getGridAt 函数，这个函数应该是返回指定位置的棋子或者棋格。
  //  然后从这个棋格中获取棋子并将其等级设置为0。
  //  这段代码可能是实现一个围住对方棋子的功能，将对方棋子的等级降为0表示将其困住。

    public void solveTrap(ChessboardPoint selectedPoint, ChessboardPoint destPoint) {
        if (getGridAt(destPoint).getType() == GridType.TRAP && getGridAt(destPoint).getOwner() != getChessPieceAt(selectedPoint).getOwner()) {
            getTrapped(selectedPoint);
        } else if (getGridAt(selectedPoint).getType() == GridType.TRAP && getGridAt(selectedPoint).getOwner() != getChessPieceAt(selectedPoint).getOwner()) {
            exitTrap(selectedPoint);
        }
    }
    public boolean solveDens(ChessboardPoint destPoint) {
        if (getGridAt(destPoint).getType() == GridType.DENS) {
            return true;
        }
        return false;
    }
    public void getTrapped(ChessboardPoint point) {
        getGridAt(point).getPiece().setRank(0);
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
    //这个方法接受当前玩家的颜色作为参数，然后在棋盘上检查是否还有敌方棋子，如果有则返回 false，否则返回 true。
    // 为了做到这一点，方法使用嵌套的 for 循环来遍历棋盘中的每个点，调用 getChessPieceAt(point) 方法获取该点上的棋子，并检查它的所有者是否与当前玩家相同。
    // 如果找到了敌方棋子，则返回 false，从而表示还有棋子；
    // 如果所有点都被遍历且没有找到敌方棋子，则返回 true。



    //如果检测到可以走或可以吃，则将该位置保存到一个List集合availablePoints中，最后将所有可用的位置返回。
    public List<ChessboardPoint> getValidMoves(ChessboardPoint point) {
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

    public List<ChessboardPoint> getValidPoints(PlayerColor color){
        List<ChessboardPoint> availablePoints = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                ChessboardPoint point = new ChessboardPoint(i, j);
                if (getChessPieceAt(point) != null && getChessPieceAt(point).getOwner() == color) {
                    availablePoints.add(point);
                }
            }
        }
        return availablePoints;
    }

    public List<Step> getValidSteps(PlayerColor color){//获取当前玩家可用的所有合法走法，并将它们保存在一个List集合中
        List<Step> availableSteps = new ArrayList<>();
        List<ChessboardPoint> availablePoints = getValidPoints(color);
        //首先，这个方法调用了getValidPoints()方法获取当前玩家可以移动的所有棋子的位置，得到一个List集合availablePoints。
        for (ChessboardPoint point : availablePoints) {
            List<ChessboardPoint> validMoves = getValidMoves(point);
            //然后，对于每个可用位置，使用getValidMoves()方法获取该棋子可以移动到的所有合法位置，得到另外一个List集合validMoves。
            for (ChessboardPoint destPoint : validMoves) {
                availableSteps.add(recordStep(point, destPoint, color, 0));
                //接下来，对于每个起点位置和目标位置的组合，调用recordStep()方法记录这个移动到一个新的Step对象中，并将这个Step对象添加到availableSteps集合中。
            }
        }
        return availableSteps;//返回所有可用的步骤。
    }
    public Step recordStep(ChessboardPoint fromPoint, ChessboardPoint toPoint, PlayerColor currentPlayer, int turn){
        ChessPiece fromPiece = getChessPieceAt(fromPoint);
        ChessPiece toPiece = getChessPieceAt(toPoint);
        return new Step(fromPoint, toPoint, fromPiece, toPiece, currentPlayer, turn);
    }

    public void undoStep(Step step){
        ChessboardPoint fromPoint = step.getFrom();
        ChessboardPoint toPoint = step.getTo();
        ChessPiece fromPiece = step.getFromChessPiece();
        ChessPiece toPiece = step.getToChessPiece();
        setChessPiece(toPoint, toPiece);
        setChessPiece(fromPoint, fromPiece);
    }

    public void runStep(Step step){
        ChessboardPoint fromPoint = step.getFrom();
        ChessboardPoint toPoint = step.getTo();
        ChessPiece fromPiece = step.getFromChessPiece();
        setChessPiece(fromPoint, null);
        setChessPiece(toPoint, fromPiece);
    }

}

