package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

public class KnightMoves extends PieceMoves {
    public KingMoves(ChessBoard gameBoard, ChessPosition startPosition) {
        super(gameBoard, startPosition);
        calculateMoves();
    }

    @Override
    public void calculateMoves() {
        ChessPosition checkPosition;
        for (int row = StartPosition.getRow() - 2; row <= startPosition.getRow() + 2; row++) {
            for (int col = StartPosition.getColumn() - 2; col <= startPosition.getColumn() + 2; col++) {
                if (Math.abs(StartPosition.getRow() - row) + Math.abs(startPosition.getColumn() - col) == 3 && isInBounds(row, col)) {
                    checkPosition = new ChessPosition(row, col);
                    checkSpace(checkPosition);
                }
            }
        }
    }
}