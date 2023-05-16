package model;

/**
 * This class represents positions on the checkerboard, such as (0, 0), (0, 7), and so on
 * Where, the upper left corner is (0, 0), the lower left corner is (7, 0), the upper right corner is (0, 7), and the lower right corner is (7, 7).
 */
public class ChessboardPoint {
    private final int row;
    private final int col;

    public ChessboardPoint(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    //  此代码段是一个根据传入的参数 k 获取棋盘（Chessboard）中相邻棋子位置的方法。
    //  其中，k 的取值为整数0、1、2、3，分别代表当前点的上、右、下、左四个方向。
    //  该方法的返回值类型是 ChessboardPoint，即棋盘上的位置点。
    //  具体实现中，该方法首先使用 switch-case 分支结构对 k 取不同的值时的情况进行判断处理。
    //  当 k 为 0 时，即当前点的上方，方法返回一个新的 ChessboardPoint 对象，行数（row）减 1，列数（col）不变，表示向当前点的上方移动一格；
    //  当 k 为 1 时，即当前点的右方，方法返回一个新的 ChessboardPoint 对象，行数不变，列数加 1，表示向当前点的右方移动一格；
    //  当 k 为 2 时，即当前点的下方，方法返回一个新的 ChessboardPoint 对象，行数加 1，列数不变，表示向当前点的下方移动一格；
    //  当 k 为 3 时，即当前点的左方，方法返回一个新的 ChessboardPoint 对象，行数不变，列数减 1，表示向当前点的左方移动一格。
    //  如果传入的参数 k 不符合上述四种情况，该方法将返回 null 值。
    public ChessboardPoint getNeighbor(int k){
        switch (k) {
            case 0://向上移动一格
                return new ChessboardPoint(row - 1, col);
            case 1://向右移动一格
                return new ChessboardPoint(row, col + 1);
            case 2://向下移动一格
                return new ChessboardPoint(row + 1, col);
            case 3://向左移动一格
                return new ChessboardPoint(row, col - 1);
            default:
                return null;
        }
    }

    @Override
    public int hashCode() {
        return row + col;
    }

    @Override
    @SuppressWarnings("ALL")
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        ChessboardPoint temp = (ChessboardPoint) obj;
        return (temp.getRow() == this.row) && (temp.getCol() == this.col);
    }
    @Override
    public String toString() {
        return "("+row + ","+col+") " + "on the chessboard is clicked!";
    }

}