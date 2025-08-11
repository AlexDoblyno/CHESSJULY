package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class RookMoves extends PieceMovesFar {
    public RookMoves(ChessBoard GameBoard, ChessPosition StartPosition) {
        super(GameBoard, StartPosition);
        calculateMoves(GameBoard);
    }

    @Override
    public void calculateMoves(ChessBoard GameBoard) {
        checkLine(GameBoard,-1, 0);
        checkLine(GameBoard,1, 0);
        checkLine(GameBoard,0, -1);
        checkLine(GameBoard,0, 1);
    }
}
