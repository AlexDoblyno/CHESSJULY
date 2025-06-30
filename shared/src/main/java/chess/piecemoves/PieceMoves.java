package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;
import java.util.Objects;

public abstract class PieceMoves {

    protected ChessBoard GameBoard;
    protected ChessPosition StartPosition;
    protected HashSet<ChessMove> moveList;
    protected ChessGame.TeamColor team;

    public PieceMoves(ChessBoard GameBoard, ChessPosition StartPosition) {
        this.GameBoard = GameBoard;
        this.StartPosition = StartPosition;
        team = GameBoard.getPiece(StartPosition).getTeamColor();
        moveList = new HashSet<ChessMove>();
    }

    public void calculateMoves(){}

    public HashSet<ChessMove> getMoveList() {
        return moveList;
    }

    protected boolean isInBounds(int row, int col) {
        return row > 0 && col > 0 && row <= 8 && col <= 8;
    }

    protected boolean checkSpace(ChessPosition EndPosition) {
        if (GameBoard.getPiece(EndPosition) != null) {
            if (GameBoard.getPiece(EndPosition).getTeamColor() != team) {
                moveList.add(new ChessMove(StartPosition, EndPosition));
                System.out.print("Captured piece at (" + EndPosition.getRow() + ", " + EndPosition.getColumn() + "): ");
            }
            System.out.println("Stop checking");
            return false;
        }
        else {
            moveList.add(new ChessMove(StartPosition, EndPosition));
            System.out.println("Added move to null space at (" + EndPosition.getRow() + ", " + EndPosition.getColumn() + ") ");
            return true;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PieceMoves that)) {
            return false;
        }
        return Objects.equals(moveList, that.moveList);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(moveList);
    }
}
