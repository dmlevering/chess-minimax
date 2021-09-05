package chess.model.piece;

/**
 * Piece/player color
 */
public enum PieceColor {
    WHITE("White", "w"),
    BLACK("Black", "b");

    private String fullName;
    public String getFullName() {
        return this.fullName;
    }

    private String abrvName;
    public String getAbrvName() {
        return this.abrvName;
    }

    private PieceColor(String fullName, String abrvName) {
        this.fullName = fullName;
        this.abrvName = abrvName;
    }

    @Override
    public String toString() {
        return this.fullName;
    }
}
