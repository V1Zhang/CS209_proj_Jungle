package listener;

import model.ChessboardPoint;
import view.CellComponent;
import view.ElephantChessComponent;
import view.LionChessComponent;
import view.TigerChessComponent;
import view.LeopardChessComponent;
import view.WolfChessComponent;
import view.DogChessComponent;
import view.CatChessComponent;
import view.RatChessComponent;

public interface GameListener {

    void onPlayerClickCell(ChessboardPoint point, CellComponent component);


    void onPlayerClickChessPiece(ChessboardPoint point, ElephantChessComponent component);
    void onPlayerClickChessPiece(ChessboardPoint point, LionChessComponent component);
    void onPlayerClickChessPiece(ChessboardPoint point, TigerChessComponent component);
    void onPlayerClickChessPiece(ChessboardPoint point, LeopardChessComponent component);
    void onPlayerClickChessPiece(ChessboardPoint point, WolfChessComponent component);
    void onPlayerClickChessPiece(ChessboardPoint point, DogChessComponent component);
    void onPlayerClickChessPiece(ChessboardPoint point, CatChessComponent component);
    void onPlayerClickChessPiece(ChessboardPoint point, RatChessComponent component);




}
