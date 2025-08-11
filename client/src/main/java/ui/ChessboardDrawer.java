package ui;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

import static java.lang.Math.abs;

public class ChessboardDrawer {
    private ChessGame chessGame;
    private ChessGame.TeamColor perspective;
    private final StringBuilder boardString;

    public ChessboardDrawer() {
        chessGame = new ChessGame();
        perspective = ChessGame.TeamColor.WHITE;
        boardString = new StringBuilder();
    }

    public ChessboardDrawer(ChessGame currentGame, ChessGame.TeamColor teamColor) {
        chessGame = currentGame;
        perspective = teamColor;
        boardString = new StringBuilder();
    }

    public String drawBoardString(Collection<ChessPosition> highlightPos) {

        String formatCoordinates = EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_BG_COLOR_DARK_GREEN;
        String clearFormatting = EscapeSequences.RESET_TEXT_BOLD_FAINT + EscapeSequences.RESET_TEXT_COLOR;

        // 通过当前队伍设置打印方向
        boolean direction = (perspective == ChessGame.TeamColor.WHITE);

        boardString.append(EscapeSequences.ERASE_SCREEN);

        // 打印A~H标签
        boardString.append(formatCoordinates);
        boardString.append(" \u2003 a  \u2003 b  \u2003 c  \u2003 d  \u2003 e  \u2003 f  \u2003 g  \u2003 h  \u2003 ");
        boardString.append(EscapeSequences.RESET_TEXT_COLOR).append(EscapeSequences.RESET_BG_COLOR);
        boardString.append("\n");
        boardString.append(clearFormatting);

        writeChessBoard(direction, formatCoordinates, clearFormatting, highlightPos);

        boardString.append(formatCoordinates);
        boardString.append(" \u2003 a  \u2003 b  \u2003 c  \u2003 d  \u2003 e  \u2003 f  \u2003 g  \u2003 h  \u2003 ");
        boardString.append(EscapeSequences.RESET_TEXT_COLOR).append(EscapeSequences.RESET_BG_COLOR);
        boardString.append("\n");
        boardString.append(clearFormatting);
        return boardString.toString();
    }

    private void writeChessBoard(boolean direction, String formatCoordinates, String clearFormatting, Collection<ChessPosition> highlightPos) {
        // Loop打印
        int startRow = direction ? 7 : 0;
        int endRow = direction ? 0 : 7;
        int rowStep = direction ? -1 : 1;
        int startCol = direction ? 0 : 7;
        int endCol = direction ? 7 : 0;
        int colStep = direction ? 1 : -1;
        for (int row = startRow; row - rowStep != endRow; row += rowStep) {
            // 打印1~8标签

            boardString.append(formatCoordinates).append(" ").append(row+ 1).append(" ").append(clearFormatting);

            // 打印棋盘 (回头看看是什么导致位置错误)
            //for (int col = 0; col < 8; col++) {
            //    ChessPosition printPosition = new ChessPosition(displayRow, col+1);
            //    ChessPiece printPiece = getChessGame().getBoard().getPiece(printPosition);
            //    boardString.append(getSquareColor(row, col)).append(getPiece(printPiece));
            //    boardString.append(EscapeSequences.RESET_TEXT_COLOR).append(EscapeSequences.RESET_BG_COLOR);
            //}
            boardString.append(EscapeSequences.RESET_TEXT_COLOR).append(EscapeSequences.RESET_BG_COLOR);
            if (row % 2 == 0) {
                for (int j = startCol; j - colStep != endCol; j += colStep) {
                    String backgroundColor = getSquareColor(row+1, j);
                    if (highlightPos != null && highlightPos.contains(new ChessPosition(row+1, j+1))) {
                        backgroundColor = (j % 2 == 0) ? EscapeSequences.SET_BG_COLOR_DARK_GREEN :
                                EscapeSequences.SET_BG_COLOR_GREEN;
                    }
                    boardString.append(backgroundColor).append(getPiece(getChessGame().getBoard().getPiece(new ChessPosition(row+1, j+1))));
                }
            } else {
                for (int j = startCol; j - colStep != endCol; j += colStep) {
                    String backgroundColor = getSquareColor(row+1, j);
                    if (highlightPos != null && highlightPos.contains(new ChessPosition(row + 1, j + 1))) {
                        backgroundColor = (j % 2 != 0) ? EscapeSequences.SET_BG_COLOR_DARK_GREEN :
                                EscapeSequences.SET_BG_COLOR_GREEN;
                    }
                    boardString.append(backgroundColor).append(getPiece((getChessGame().getBoard().getPiece(new ChessPosition(row + 1, j + 1)))));
                }
            }
            boardString.append(formatCoordinates).append(" ").append(row+ 1).append(" ").append(clearFormatting).append(EscapeSequences.RESET_BG_COLOR);
            boardString.append("\n");
        }
    }

    private String getSquareColor(int row, int col) {
        boolean isWhiteSquare = (row + col) % 2 == 0;
        return (isWhiteSquare ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY);
    }

    /**
     * Function to get the correct piece for display on the board
     *
     * @param chessPiece is the piece we're checking
     * @return the correct color and Unicode chess piece
     */
    private String getPiece(ChessPiece chessPiece) {
        StringBuilder pieceString = new StringBuilder();
        if (chessPiece == null) {
            pieceString.append("  \u2003  ");
        } else {
            pieceString.append(" "); //这里大概不需要

            switch (chessPiece.getPieceType()) {
                case KING -> pieceString.append(chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                        EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING);
                case QUEEN -> pieceString.append(chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                        EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN);
                case BISHOP -> pieceString.append(chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                        EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP);
                case KNIGHT -> pieceString.append(chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                        EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT);
                case ROOK -> pieceString.append(chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                        EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK);
                case PAWN -> pieceString.append(chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                        EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN);
            }
            ;
            pieceString.append(" ");//
        }
        return pieceString.toString();
    }

    public ChessGame getChessGame() {
        return chessGame;
    }

    public void setChessGame(ChessGame chessGame) {
        this.chessGame = chessGame;
    }

    public ChessGame.TeamColor getPerspective() {
        return perspective;
    }

    public void setPerspective(ChessGame.TeamColor perspective) {
        this.perspective = perspective;
    }

    public void printHighlightedMoves(ChessBoard board, ChessGame.TeamColor bottomColor, Collection<ChessMove> moves) {
        if (moves != null) {
            Collection<ChessPosition> positions = new ArrayList<>();
            for (ChessMove move : moves) {
                positions.add(move.getStartPosition());
                positions.add(move.getEndPosition());
            }

            System.out.println(this.drawBoardString(positions));
        } else {
            System.out.println("No moves found");
        }
    }

}
