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
        checkStalemate = new CheckStalemate(GameBoard); //没有编写导致报错
        inCheckDeterminer = new InCheckDeterminer(GameBoard);
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
            pieceMoves = GameBoard.getPiece(startPosition).pieceMoves(GameBoard, startPosition);
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
     * @param TeamColor which team to check for check
     * @return True if the specified team is in check
     */
        public boolean isInCheck(TeamColor TeamColor) {
            inCheckDeterminer.setGameBoard(GameBoard);
            return inCheckDeterminer.isInCheck(TeamColor);
        }

    /**
     * Determines if the given team is in checkmate
     *
     * @param TeamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
        public boolean isInCheckmate(TeamColor TeamColor){
            if (!isInCheck(TeamColor)) {
                return false;
            }
            else {
                for (ChessMove move : getAllTeamMoves(TeamColor)) {
                    if (!testMove(move)) {
                        return false;
                    }
                }
                return true;
            }
        }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param TeamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
        private Collection<ChessMove> getAllTeamMoves(TeamColor TeamColor) {
            Collection<ChessMove> allMoves = new HashSet<>();
            ChessPosition checkPosition;
            ChessPiece checkPiece;
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    checkPosition = new ChessPosition(row, col);
                    checkPiece = GameBoard.getPiece(checkPosition);
                    if (checkPiece != null && checkPiece.getTeamColor() == TeamColor) {
                        allMoves.addAll(validMoves(checkPosition));
                    }
                }
            }
            return allMoves;
        }

        public boolean isInStalemate(TeamColor TeamColor) {
            checkStalemate.setGameBoard(GameBoard);
            return checkStalemate.isInStalemate(isInCheck(TeamColor), getAllTeamMoves(TeamColor));
        }
    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
        public void setBoard(ChessBoard board) {
            ChessPosition setPosition;
            ChessPiece setPiece;
            castlingHistory.resetHistory();
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 8; j++) {
                    setPosition = new ChessPosition(i, j);
                    if (board.getPiece(setPosition) != null) {
                        setPiece = new ChessPiece(board.getPiece(setPosition).getTeamColor(), board.getPiece(setPosition).getPieceType());
                        GameBoard.addPiece(setPosition, setPiece);
                    }
                    else {
                        GameBoard.addPiece(setPosition, null);
                    }
                }
            }
        }
    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
        public ChessBoard getBoard() {
            return GameBoard;
        }



        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ChessGame chessGame)) {
                return false;
            }
            return currentTeam == chessGame.currentTeam && Objects.equals(GameBoard, chessGame.GameBoard);
        }

        @Override
        public int hashCode() {
            return Objects.hash(currentTeam, GameBoard);
        }
    }