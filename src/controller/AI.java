package controller;


public class AI {
    public static boolean mode;
    public static void startAIMode(){
        mode=true;
    }
    public static void quitAIMode(){
        mode=false;
    }
}
/*
public class AI {

        private static final int MAX_DEPTH = 4; // 最大搜索深度
        private static final int INF = 1000000; // 极大值（赢局状态）
        private static final int N_INF = -INF; // 极小值（输局状态）
        public static int[] run(int[][] board) {
            int bestScore = N_INF;
            int[] bestMove = new int[4];
            int depth = 0;
            for (int i = 0; i < 7; ++i) {
                for (int j = 0; j < 9; ++j) {
                    if (board[i][j] != null && board[i][j].getColor() == Piece.COLOR_BLACK) {
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

        private static void undoMove(int[][] board, Piece piece) {
            int x = piece.getX();
            int y = piece.getY();
            int fromX = piece.getFromX();
            int fromY = piece.getFromY();
            board[x][y] = null;
            board[fromX][fromY] = piece;
            piece.setX(fromX);
            piece.setY(fromY);
            if (piece.isDead()) {
                piece.setDead(false);
                Piece rat = PieceFactory.createPiece(Rank.RAT, piece.getColor(), fromX, fromY);
                board[fromX][fromY] = rat;
            }
        }
}

     */

