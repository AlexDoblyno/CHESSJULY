package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static java.lang.Math.abs;
/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPosition CapturedPosition = null;

    ChessPiece[][] squares;
    public ChessBoard() {
        squares =  new ChessPiece[8][8]; //创建8x8棋盘方块
    }
    public ChessPiece doMove(ChessMove move) {
        ChessPiece piece;
        ChessPiece capturedPiece = getPiece(move.getEndPosition());
        capturedPosition = move.getEndPosition();
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(this.getPiece(move.getStartPosition()).getTeamColor(), move.getPromotionPiece());
        } else {
            piece = this.getPiece(move.getStartPosition());
        }
    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {


    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        throw new RuntimeException("Not implemented");
    }
}
