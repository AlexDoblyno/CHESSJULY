package chess;

public class CastlingHistory {
    boolean WHITEKingRookMoved;
    boolean WHITEQueenRookMoved;
    boolean BLACKKingRookMoved;
    boolean BLACKQueenRookMoved;
    boolean WHITEKingMoved;
    boolean BLACKKingMoved;
}
    CastlingHistory(){
        WHITEKingMoved = false;
        BLACKKingMoved = false;
        WHITEKingRookMoved = false;
        BLACKKingRookMoved = false;

    }