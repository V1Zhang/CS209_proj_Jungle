package listener;
import model.ChessboardPoint;
import model.PlayerColor;
import view.*;
import view.ChessComponent.AnimalChessComponent;

import java.awt.*;

//Java接口，其中声明了两个方法，onPlayerClickCell和onPlayerClickChessPiece。这两个方法分别在玩家点击棋盘上的单元格和棋子时被调用。

public interface GameListener {

    void onPlayerClickCell(ChessboardPoint point, CellComponent component);

    void onPlayerClickChessPiece(ChessboardPoint point, AnimalChessComponent component);

    interface OnChangeListener {    // 创建interface类
        void onChange();    // 值改变
    }

    public interface Subject {
        public void addUpdateListener(OnChangeListener conchangeListener);

    }
}
