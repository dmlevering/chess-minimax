package chess.model;

/**
 * Implement GameListener and call chess.model.Game.addGameListener() to listen
 * for game events
 */
public interface GameListener {
    /**
     * Invoked when a turn ends
     */
    void turnCompleted();

    /**
     * Invoked when an undo operation completes
     */
    void undoCompleted();

    /**
     * Invoked when a redo operation completes
     */
    void redoCompleted();

    /**
     * Invoked when the game has completed
     */
    void gameCompleted(GameResult result);
}
