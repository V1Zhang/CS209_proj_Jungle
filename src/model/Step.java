package model;

import java.io.Serializable;
//表示一步完整的棋子移动，包括移动前后的位置、移动前后的棋子、当前玩家颜色和这是第几个回合
public class Step {

    private ChessboardPoint from;
    private ChessboardPoint to;
    private ChessPiece fromChessPiece;
    private ChessPiece toChessPiece;
    private PlayerColor currentPlayer;
    private int turnCount;

    public Step(ChessboardPoint from, ChessboardPoint to, ChessPiece fromChessPiece, ChessPiece toChessPiece, PlayerColor currentPlayer, int turnCount) {
        this.from = from;
        this.to = to;
        this.fromChessPiece = fromChessPiece;
        this.toChessPiece = toChessPiece;
        this.currentPlayer = currentPlayer;
        this.turnCount = turnCount;
    }
    public ChessboardPoint getFrom() {
        return from;
    }

    public ChessboardPoint getTo() {
        return to;
    }

    public void setFrom(ChessboardPoint from) {
        this.from = from;
    }

    public void setTo(ChessboardPoint to) {
        this.to = to;
    }

    public ChessPiece getFromChessPiece() {
        return fromChessPiece;
    }

    public ChessPiece getToChessPiece() {
        return toChessPiece;
    }

    public void setFromChessPiece(ChessPiece fromChessPiece) {
        this.fromChessPiece = fromChessPiece;
    }

    public void setToChessPiece(ChessPiece toChessPiece) {
        this.toChessPiece = toChessPiece;
    }

    public PlayerColor getCurrentPlayer() {
        return currentPlayer;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public void setCurrentPlayer(PlayerColor currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setTurnCount(int turnCount) {
        this.turnCount = turnCount;
    }

    @Override
    public String toString() {
        return "Step{" +
                "from=" + from +
                ", to=" + to +
                ", fromChessPiece=" + fromChessPiece +
                ", toChessPiece=" + toChessPiece +
                "current player is "+ currentPlayer+
                "turn count is "+turnCount+
                "}";
    }
}