package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import static chess.ChessPiece.PieceType.ROOK;
import static java.lang.Math.abs;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor CurrentTeam;
    ChessBoard GameBoard;
    ChessMove PreviousMove;
    CastlingHistory CastlingHistory; //添加castling规则
    CheckStalemate CheckStalemate; //添加平局
    InCheckDeterminer InCheckDeterminer; //

    public ChessGame() {
        CurrentTeam = TeamColor.WHITE;
        GameBoard = new ChessBoard();
        GameBoard.resetBoard();
        PreviousMove = null;
        CastlingHistory = new CastlingHistory();
        CheckStalemate = new CheckStalemate(GameBoard); //没有编写导致报错
        InCheckDeterminer = new InCheckDeterminer(GameBoard);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return CurrentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        CurrentTeam = team;
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
            if (PreviousMove != null) {

                // Check for en passant

                }
            }
            // Check for castling
            ChessPiece checkPiece = GameBoard.getPiece(startPosition);
            if (checkPiece.getPieceType() == ChessPiece.PieceType.KING
                    && startPosition.getColumn() == 5
                    && !isInCheck(GameBoard.getPiece(startPosition).getTeamColor())) {
                if ((checkPiece.getTeamColor() == TeamColor.WHITE && !CastlingHistory.isWhiteKingMoved())
                        ||(checkPiece.getTeamColor() == TeamColor.BLACK && !CastlingHistory.isBlackKingMoved())) {
                    pieceMoves.addAll(getCastleMoves(startPosition));
                }
            }
            return pieceMoves;
        }
        else {
            return null;
        }
    }
    private Collection<ChessMove> getCastleMoves (ChessPosition startPosition) {
        Collection<ChessMove> CastleMoves = new HashSet<>();
        ChessPiece king = GameBoard.getPiece(startPosition);

         if (king.getTeamColor() == TeamColor.WHITE && !CastlingHistory.isWHITEKingMoved()) {
            if (!CastlingHistory.isWHITEKingRookMoved() && checkPathClear(startPosition, 1)) {
                CastleMoves.add(new ChessMove(startPosition, new ChessPosition(1, 7)));
        }
            if (!CastlingHistory.isWHITEQueenRookMoved() && checkPathClear(startPosition, -1)) {
                CastleMoves.add(new ChessMove(startPosition, new ChessPosition(1, 3)));
        }
    }
    else if (king.getTeamColor() == TeamColor.BLACK && !CastlingHistory.isBLACKKingMoved()) {
        if (!CastlingHistory.isBLACKKingRookMoved() && checkPathClear(startPosition, 1)) {
            CastleMoves.add(new ChessMove(startPosition, new ChessPosition(8, 7)));
        }
        if (!CastlingHistory.isBLACKQueenRookMoved() && checkPathClear(startPosition, -1)) {
            CastleMoves.add(new ChessMove(startPosition, new ChessPosition(8, 3)));
        }
    }
    return CastleMoves;
    /// castle ///

    private boolean checkPathClear(ChessPosition startPosition, int direction) {
        ChessPosition pathPosition;
        ChessMove pathMove;
        for (int i = 1; i * direction + 5 > 1 && i * direction + 5 < 8; i++) {
            pathPosition = new ChessPosition(startPosition.getRow(), (i * direction) + 5);
            pathMove = new ChessMove(startPosition, pathPosition);
            if (i <= 2) {
                if (GameBoard.getPiece(pathPosition) != null || testMove(pathMove)) {
                    return false;
                }
            } else if (GameBoard.getPiece(pathPosition) != null) {
                return false;
            }
        }
        return true;
    }
    
    public boolean testMove(ChessMove move) {
        boolean inCheck;
        ChessPiece targetPiece = GameBoard.getPiece(move.getEndPosition());
        moveMaker(move);
        inCheck = isInCheck(GameBoard.getPiece(move.getEndPosition()).getTeamColor());
        if (GameBoard.getPiece(move.getEndPosition()).getPieceType() == ChessPiece.PieceType.KING) {
            int direction = move.getEndPosition().getColumn() - move.getStartPosition().getColumn();
            if (Math.abs(direction) == 2) {
                undoCastleRook(move, direction);
            }
        }
        undoMove(move, targetPiece);
        return inCheck;
    }
    ChessPiece CheckPiece = GameBoard.getPiece(startPosition);
    if (CheckPiece.getPieceType() == ChessPiece.PieceType.KING
            && startPosition.getColumn() == 5
            && !InCheckDeterminer(GameBoard.getPiece(startPosition).getTeamColor())) {
        if ((CheckPiece.getTeamColor() == TeamColor.WHITE && !CastlingHistory.isWHITEKingMoved())
                || (CheckPiece.getTeamColor() == TeamColor.BLACK && !CastlingHistory.isBLACKKingMoved())) {
            pieceMoves.addAll(getCastleMoves(startPosition));
        }
    }
    return PieceMoves;
}

/**
 * Makes a move in a chess game
 *
 * @param move chess move to perform
 * @throws InvalidMoveException if move is invalid
 */
public void makeMove(ChessMove move) throws InvalidMoveException {
    if (GameBoard.getPiece(move.getStartPosition()) == null) {
        throw new InvalidMoveException("no piece at start position");
    }
    else if (!validMoves(move.getStartPosition()).contains(move)) {
        throw new InvalidMoveException("ERROR FOUND WHEN ATTEMPTING MOVE " + move + " {(" +
                move.getStartPosition().getRow() + ", " + move.getStartPosition().getColumn() + ") ("
                + move.getEndPosition().getRow() + ", " + move.getEndPosition().getColumn() + ")} " +
                "move is invalid");
    }
    else if (GameBoard.getPiece(move.getStartPosition()).getTeamColor() != currentTeam) {
        throw new InvalidMoveException("current team is" + currentTeam.toString() + ", wrong team move");
    }
    else {
        moveMaker(move);
        PreviousMove = move;
        RookKingMoved(move);
        CurrentTeam = (CurrentTeam == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE; // <- Me trying to make my code more concise
    }
}
/**
 * Determines if the given team is in check
 *
 * @param TeamColor which team to check for check
 * @return True if the specified team is in check
 */
public boolean isInCheck(TeamColor TeamColor) {
    InCheckDeterminer.setGameBoard(GameBoard);
    return InCheckDeterminer.isInCheck(TeamColor);
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
    ChessPiece CheckPiece;
    for (int row = 1; row <= 8; row++) {
        for (int col = 1; col <= 8; col++) {
            checkPosition = new ChessPosition(row, col);
            CheckPiece = GameBoard.getPiece(checkPosition);
            if (CheckPiece != null && CheckPiece.getTeamColor() == TeamColor) {
                allMoves.addAll(validMoves(checkPosition));
            }
        }
    }
    return allMoves;
}

public boolean isInStalemate(TeamColor TeamColor) {
    CheckStalemate.setGameBoard(GameBoard);
    return CheckStalemate.isInStalemate(isInCheck(TeamColor), getAllTeamMoves(TeamColor));
}
/**
 * Sets this game's chessboard with a given board
 *
 * @param board the new board to use
 */
public void setBoard(ChessBoard board) {
    ChessPosition setPosition;
    ChessPiece setPiece;
    CastlingHistory.resetHistory();
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



