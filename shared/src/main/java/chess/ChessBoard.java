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

    ChessPiece[][] GameBoard;
    public ChessBoard() {
        GameBoard =  new ChessPiece[8][8]; //创建8x8棋盘方块
    }


    public ChessPiece doMove(ChessMove move) {

        }
    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    //添加棋子
    public void addPiece(ChessPosition position, ChessPiece piece) {
        GameBoard[position.getRow()-1][position.getColumn()-1] = piece;
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
        return GameBoard[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {}
        for (int row = 1; row <= 8; row++) {
            switch (row) {
                case 1, 8:
                    setBackRow(row);
                    break;
                case 2, 7:
                    setPawnRow(row);
                    break;
                default:
                    setNullRow(row);
            }
        }
    }

