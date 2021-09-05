package chess.model;

/**
 * Square colors
 */
public enum SquareColor {
    LIGHT("Light"),
    DARK("Dark");

    private String name;

    /**
     * SquareColor constructor
     */
    private SquareColor(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}