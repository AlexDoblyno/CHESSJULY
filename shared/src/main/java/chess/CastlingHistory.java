package chess;

public class CastlingHistory {
    boolean WHITEKingRookMoved;
    boolean WHITEQueenRookMoved;
    boolean BLACKKingRookMoved;
    boolean BLACKQueenRookMoved;
    boolean WHITEKingMoved;
    boolean BLACKKingMoved;

    CastlingHistory() {
        WHITEKingMoved = false;
        BLACKKingMoved = false;
        WHITEKingRookMoved = false;
        BLACKKingRookMoved = false;
        WHITEQueenRookMoved = false;
        BLACKQueenRookMoved = false;
    } //王车易位前，检测王与车有无移动，若是先前移动，无法王车易位

    public void resetHistory() {
        WHITEKingMoved = false;
        BLACKKingMoved = false;
        WHITEKingRookMoved = false;
        BLACKKingRookMoved = false;
        WHITEQueenRookMoved = false;
        BLACKQueenRookMoved = false;
    }
    public boolean isWhiteKingsideRookMoved() {
        return whiteKingsideRookMoved;
    }

    public void setWhiteKingsideRookMoved(boolean whiteKingsideRookMoved) {
        this.whiteKingsideRookMoved = whiteKingsideRookMoved;
    }

    public boolean isWhiteQueensideRookMoved() {
        return whiteQueensideRookMoved;
    }

    public void setWhiteQueensideRookMoved(boolean whiteQueensideRookMoved) {
        this.whiteQueensideRookMoved = whiteQueensideRookMoved;
    }

    public boolean isBlackKingsideRookMoved() {
        return blackKingsideRookMoved;
    }

    public void setBlackKingsideRookMoved(boolean blackKingsideRookMoved) {
        this.blackKingsideRookMoved = blackKingsideRookMoved;
    }

    public boolean isBlackQueensideRookMoved() {
        return blackQueensideRookMoved;
    }

    public void setBlackQueensideRookMoved(boolean blackQueensideRookMoved) {
        this.blackQueensideRookMoved = blackQueensideRookMoved;
    }

    public boolean isWhiteKingMoved() {
        return whiteKingMoved;
    }

    public void setWhiteKingMoved(boolean whiteKingMoved) {
        this.whiteKingMoved = whiteKingMoved;
    }

    public boolean isBlackKingMoved() {
        return blackKingMoved;
    }

    public void setBlackKingMoved(boolean blackKingMoved) {
        this.blackKingMoved = blackKingMoved;
    }
}