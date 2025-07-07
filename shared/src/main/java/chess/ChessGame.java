package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessPiece.PieceType.ROOK;
import static java.lang.Math.abs;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard GameBoard;
    TeamColor currentTeam;
    ChessMove previousMove;
    CastlingHistory castlingHistory; //添加castling规则
    CheckStalemate checkStalemate; //添加平局
    InCheckDeterminer inCheckDeterminer; //

    public ChessGame() {
        currentTeam = TeamColor.WHITE;
        GameBoard = new ChessBoard();
        GameBoard.resetBoard();
        previousMove = null;
        castlingHistory = new CastlingHistory();
        checkStalemate = new CheckStalemate(GameBoard);
        inCheckDeterminer = new InCheckDeterminer(gameBoard);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> pieceMoves;
        if (GameBoard.getPiece(startPosition) != null) {
            pieceMoves = GameBoard.getPiece(startPosition).pieceMoves(gameBoard, startPosition);
            pieceMoves.removeIf(this::testMove);
            if (previousMove != null) {
                /// inster ENPASSENT///
            }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor TeamColor) {
            inCheckDeterminer.setGameBoard(GameBoard);
            return inCheckDeterminer.isInCheck(TeamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        throw new RuntimeException("Not implemented");
    }
}
