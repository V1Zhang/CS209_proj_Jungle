package model;

import view.GridType;

import java.io.Serializable;
/**
 * This class describe the slot for Chess in Chessboard
 * */
public class Cell implements Serializable {
    // the position for chess
    private ChessPiece piece;

    private GridType type;
    private PlayerColor owner;
    public Cell(GridType type) {
        this.type = type;
        this.piece = null;
    }

    public ChessPiece getPiece() {
        return piece;
    }

    public void setPiece(ChessPiece piece) {
        this.piece = piece;
    }

    public GridType getType(){
        return type;
    }
    public void setType(GridType type){
        this.type=type;
    }
    public void removePiece() {
        this.piece = null;
    }

    public PlayerColor getOwner() {
        return owner;
    }
    public void setOwner(PlayerColor owner) {
        this.owner = owner;
    }
}