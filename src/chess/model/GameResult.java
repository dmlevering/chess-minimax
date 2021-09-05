package chess.model;

/**
 * Represents the result of a chess game
 */
public class GameResult {
    /** Result - draw */
    public static final int DRAW = 0;

    /** Result - checkmate */
    public static final int CHECKMATE = 1;

    /** The result ID */
    public int result;

    /** The winning player, if any */
    public Player winner;

    /**
     * GameResult constructor
     */
    public GameResult(int result, Player winner) {
        this.winner = winner;
        this.result = result;
    }
}
