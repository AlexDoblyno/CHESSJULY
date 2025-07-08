package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] GameBoard;

    public ChessBoard() {

        GameBoard = new ChessPiece[8][8]; //创建8x8棋盘方块
    }


    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    //添加棋子
    public void addPiece(ChessPosition position, ChessPiece piece) {
        GameBoard[position.getRow() - 1][position.getColumn() - 1] = piece;
    }
    // -1是因为数组索引为0开始，而棋盘在1开始

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    //获取指定棋子
    public ChessPiece getPiece(ChessPosition position) {
        return GameBoard[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 1; i <= 8; i++){
            switch (i){
                case 1:
                    setBackRow(i, ChessGame.TeamColor.WHITE);
                    break;
                case 2:
                    setPawnRow(i, ChessGame.TeamColor.WHITE);
                    break;
                case 7:
                    setPawnRow(i, ChessGame.TeamColor.BLACK);
                    break;
                case 8:
                    setBackRow(i, ChessGame.TeamColor.BLACK);
                    break;
                default:
                    setNullSpaces(i);
            }
        }
    }

    public void setNullSpaces(int row) {
        for (int i = 0; i < 8; i++){
            GameBoard[row][i] = null;
        }
    }
    //将NullRoll改为Nullspaces
     //这里没有改7/7
    private void setPawnRow(int row, ChessGame.TeamColor teamColor) {
        for (int col = 1; col <= 8; col++){
            addPiece(new ChessPosition(row, col), new ChessPiece(teamColor, ChessPiece.PieceType.PAWN));
        }
    }

    private void setBackRow(int row, ChessGame.TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            switch (i){
                case 1, 8:
                    addPiece(new ChessPosition(row, i), new ChessPiece(teamColor, ChessPiece.PieceType.ROOK));
                    break;
                case 2, 7:
                    addPiece(new ChessPosition(row, i), new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT));
                    break;
                case 3, 6:
                    addPiece(new ChessPosition(row, i), new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP));
                    break;
                case 4:
                    addPiece(new ChessPosition(row, i), new ChessPiece(teamColor, ChessPiece.PieceType.QUEEN));
                    break;
                case 5:
                    addPiece(new ChessPosition(row, i), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
                    break;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChessBoard that)) {
            return false;
        }
        return Objects.deepEquals(GameBoard, that.GameBoard);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(GameBoard);
    }
}

