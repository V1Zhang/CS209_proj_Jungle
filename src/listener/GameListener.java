package listener;
import model.ChessboardPoint;
import view.*;
import view.ChessComponent.AnimalChessComponent;

//Java接口，其中声明了两个方法，onPlayerClickCell和onPlayerClickChessPiece。这两个方法分别在玩家点击棋盘上的单元格和棋子时被调用。

public interface GameListener {

    void onPlayerClickCell(ChessboardPoint point, CellComponent component);


    void onPlayerClickChessPiece(ChessboardPoint point, AnimalChessComponent component);





}
