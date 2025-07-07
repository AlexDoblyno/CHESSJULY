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
   public boolean isWHITEKingMoved() {
        return WHITEKingMoved;
    }
    public boolean isBLACKKingMoved() {
        return BLACKKingMoved;
    }
}