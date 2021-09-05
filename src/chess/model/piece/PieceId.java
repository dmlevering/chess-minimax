package chess.model.piece;

/**
 * Piece identifier
 */
public enum PieceId {
    KING(   "King",   "K", 900),
    QUEEN(  "Queen",  "Q", 90 ),
    ROOK(   "Rook",   "R", 50 ),
    BISHOP( "Bishop", "B", 35 ),
    KNIGHT( "Knight", "N", 30 ),
    PAWN(   "Pawn",   "P", 10 );
    
    /** This piece's relative "value" used by engines */
    private int value;
    public int getValue() { return this.value; }
    public void setValue(int value) { this.value = value; }
    
    /** Piece's full name (e.g., "Bishop") */
    protected String fullName;
    public String getFullName() { return this.fullName; }
    
    /** Piece's abbreviated name (e.g., "B") */
    protected String abrvName;
    public String getAbrvName() { return this.abrvName; }
    
    /**
     * PieceId constructor
     */
    private PieceId(String fullName, String abrvName, int value) {
        this.fullName = fullName;
        this.abrvName = abrvName;
        this.value = value;
    }
};